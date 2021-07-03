package star.sms.wallet.service;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import star.sms.wallet.dao.WalletDao;
import star.sms.wallet.domain.Wallet;

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
