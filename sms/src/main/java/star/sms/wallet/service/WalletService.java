package star.sms.wallet.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.platmanager.domain.PlatManager;
import star.sms.platmanager.service.PlatManagerService;
import star.sms.sms.domain.SmsTask;
import star.sms.wallet.dao.WalletDao;
import star.sms.wallet.domain.Wallet;
import star.sms.wallet.domain.WalletLog;

/**
 * 客户钱包
 */
@Service
@Transactional
public class WalletService extends BaseServiceProxy<Wallet>{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private WalletDao accountDao;
    
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private WalletLogService walletLogService;
    
    @Autowired
    private PlatManagerService platManagerService;
    
    
	@Override
	protected BaseRepository<Wallet> getBaseRepository() {
		return accountDao;
	}
	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}
	
	/**
	 * 得到钱包列表
	 * @param userList
	 */
	public List<Wallet> findWalletListByUserId(List<Integer> userIdList) {
		List<Wallet> walletList = new ArrayList<Wallet>();
		StringBuilder userIds=new StringBuilder();
		int i=0;
		for (Integer userId : userIdList) {
			userIds.append("'"+userId+"'");
			if(i<userIdList.size()-1) {
				userIds.append(",");
			}
			i++;
		}
		walletList = jdbcTemplate.query("select * from tb_wallet where userId in ("+userIds.toString()+")", new BeanPropertyRowMapper<Wallet>(Wallet.class));
		return walletList;
	}

	/**
	 * 更新钱包
	 * @param walletList
	 */
	public void updateWalletMoney(List<Wallet> walletList) {
		StringBuilder sbUpdateWallet= new StringBuilder("");
		sbUpdateWallet.append(" update tb_wallet set money=money-? where userId=? ");
		List<Object[]> argsWalletList= new ArrayList<Object[]>();
		for (Wallet wallet : walletList) {
			Object[] walletArray= {wallet.getMoney(),wallet.getUserId()};
			argsWalletList.add(walletArray);
		}
		if(argsWalletList.size()>0) {
			jdbcTemplate.batchUpdate(sbUpdateWallet.toString(), argsWalletList);
		}
	}
	/**
	 * 根据发短信数量更新短信钱包
	 * @param platManager
	 * @param smsNum
	 */
	@Transactional
	public void updateWalletMoneyBySmsNum(PlatManager platManager, int smsNum,SmsTask task) {
		logger.info("开始计费，用户=" + platManager.getLoginName() + ",数量=" + smsNum);
		try {
			if (platManager != null && platManager.getPrice() != null && smsNum > 0) {
				//得到当前钱包
				Wallet wallet= findIfExist(platManager.getId());
				BigDecimal oldMoney = wallet.getMoney();
				//更新钱包
				BigDecimal totalPrice = platManager.getPrice().multiply(new BigDecimal(smsNum));
				StringBuilder sbUpdateWallet = new StringBuilder("");
				sbUpdateWallet.append(" update tb_wallet set money=money-? where userId=? ");
				Object[] walletArray = { totalPrice, platManager.getId() };
				jdbcTemplate.update(sbUpdateWallet.toString(), walletArray);
				//记录钱包日志
				WalletLog walletLog = new WalletLog();
				walletLog.setWalletId(wallet.getId());
				walletLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
				walletLog.setUserId(platManager.getId());
				walletLog.setMoney(oldMoney.subtract(totalPrice));
				walletLog.setOldMoney(oldMoney);
				walletLog.setMemo("创建短信任务:"+task.getTitle());
				walletLogService.insertWalletLog(walletLog);
			}
		} catch (Exception e) {
			logger.error("计费失败",e);
		}
	}
	/**
	 * 金额返还，通过任务用户id，
	 * @param platManagerId
	 * @param smsNum
	 */
	@Transactional
	public void updateWalletMoneyByUserIdSmsNum(int platManagerId, int smsNum) {
		try {
			PlatManager platManager =platManagerService.findOne(platManagerId);
			logger.info("开始返还金额，用户=" + platManager.getLoginName() + ",数量=" + smsNum);	
			if (platManager != null && platManager.getPrice() != null && smsNum > 0) {
				//得到当前钱包
				Wallet wallet= findIfExist(platManager.getId());
				BigDecimal oldMoney = wallet.getMoney();
				//更新钱包
				BigDecimal totalPrice = platManager.getPrice().multiply(new BigDecimal(smsNum));
				StringBuilder sbUpdateWallet = new StringBuilder("");
				sbUpdateWallet.append(" update tb_wallet set money=money+? where userId=? ");
				Object[] walletArray = { totalPrice, platManager.getId() };
				jdbcTemplate.update(sbUpdateWallet.toString(), walletArray);
				//记录钱包日志
				WalletLog walletLog = new WalletLog();
				walletLog.setWalletId(wallet.getId());
				walletLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
				walletLog.setUserId(platManager.getId());
				walletLog.setMoney(oldMoney.add(totalPrice));
				walletLog.setOldMoney(oldMoney);
				walletLog.setMemo("提交失败归还余额");
				walletLogService.insertWalletLog(walletLog);
			}
		} catch (Exception e) {
			logger.error("返还金额失败",e);
		}
	}
	
	/**
	 * 根据当前登录用户获取当前用户的钱包信息
	 * 如果因为特殊原因没有找到当前登录用户的钱包信息，则新创建一个
	 * @return
	 */
	public Wallet findIfExist() {
		List<Wallet> walletList = jdbcTemplate.query("select * from tb_wallet where userId='"+getLoginUser().getId()+"' order by id asc", new BeanPropertyRowMapper<Wallet>(Wallet.class));
		if(walletList!=null && walletList.size()>0) {
			return walletList.get(0);
		}else {
			Wallet wallet = new Wallet();
			wallet.setUserId(getLoginUser().getId());
			wallet.setMoney(new BigDecimal(0));
			return wallet;
		}
	}
	
	/**
	 * 根据当前登录用户获取当前用户的钱包信息
	 * 如果因为特殊原因没有找到当前登录用户的钱包信息，则新创建一个
	 * @return
	 */
	public Wallet findIfExist(Integer id) {
		List<Wallet> walletList = jdbcTemplate.query("select * from tb_wallet where userId='"+id+"' order by id asc", new BeanPropertyRowMapper<Wallet>(Wallet.class));
		if(walletList!=null && walletList.size()>0) {
			return walletList.get(0);
		}else {
			Wallet wallet = new Wallet();
			wallet.setUserId(id);
			wallet.setMoney(new BigDecimal(0));
			return wallet;
		}
	}
}
