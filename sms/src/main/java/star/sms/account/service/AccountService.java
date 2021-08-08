package star.sms.account.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.google.common.util.concurrent.RateLimiter;
import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.account.dao.AccountDao;
import star.sms.account.domain.AccountInfo;
import star.sms.smpp.service.SmppService;

/**
 * 大四喜账号
 */
@Service
@Transactional
public class AccountService extends BaseServiceProxy<AccountInfo>{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	//短信缓存
	private static Map<String, AccountInfo> accountMap = new HashMap<String, AccountInfo>();
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private SmppService smppService;
    
	@Override
	protected BaseRepository<AccountInfo> getBaseRepository() {
		return accountDao;
	}
	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}
	public Map<String, AccountInfo> getAccountMap() {
		return accountMap;
	}
	
	/**
	 * 分页查
	 * @param keyword
	 * @param loginUser
	 * @param pageable
	 * @return
	 */
	public Page findByPage(String keyword, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" FROM tb_account a where isDelete=0 ");
		if(StringUtils.isNotEmpty(keyword)) {
			fromWhereSql.append("  and (title like '%"+keyword+"%' or account like '%"+keyword+"%' or extno like '%"+keyword+"%') ");
		}
		Page<AccountInfo> page = super.findPageBySql(AccountInfo.class, "select * ", fromWhereSql.toString(), " order by createTime desc", null, pageable);
		return page;
	}
	
	/**
	 * 批量删除信息
	 * @param ids
	 */
	public void deleteByIds(String ids) {
		if(ids.endsWith(",")) ids=ids.substring(0,ids.length()-1);
		jdbcTemplate.update("update tb_account set isDelete=1 where id in ("+ids+")");
		//得到删除账号列表
		StringBuilder sbLimit= new StringBuilder("");
		sbLimit.append(" select * from tb_account where id in ("+ids+")");
		List<AccountInfo> accountList = jdbcTemplate.query(sbLimit.toString(), new BeanPropertyRowMapper<AccountInfo>(AccountInfo.class));
		//缓存存在账号则删除
		for (AccountInfo accountInfo : accountList) {
			getAccountMap().remove(accountInfo.getAccount());
			logger.info("删除账号："+accountInfo.getAccount());
			if(accountInfo.getChannelType()==2) {
				smppService.disableChannel(accountInfo.getId()+"");
			}
		}
	}
	/**
	 * 得到可用账号信息
	 * @return
	 */
	public List<AccountInfo> findAccountInfoList(){
		StringBuilder sb= new StringBuilder("");
		sb.append(" select * from tb_account where accountStatus =1  and isDelete = 0 ");
		List<AccountInfo> accountInfoList =  jdbcTemplate.query(sb.toString(), new BeanPropertyRowMapper<AccountInfo>(AccountInfo.class));
		return accountInfoList;
	}
	/**
	 * 得到账号数量
	 * @param channelType
	 * @return
	 */
	public int getAccountInfoCount(int channelType) {
		StringBuilder sb= new StringBuilder("");
		sb.append(" select count(*) from tb_account where accountStatus =1  and isDelete = 0 and channelType=" + channelType);
		int account = jdbcTemplate.queryForObject(sb.toString(), Integer.class);
		return account;
	}
	
	/**
	 * 获取所有smpp通道的账号
	 */
	public List<AccountInfo> getSmppAccount(){
		StringBuilder sb= new StringBuilder("");
		sb.append(" select * from tb_account where accountStatus =1  and isDelete = 0 and channelType='2' ");
		List<AccountInfo> accountInfoList =  jdbcTemplate.query(sb.toString(), new BeanPropertyRowMapper<AccountInfo>(AccountInfo.class));
		return accountInfoList;
	}
	
	
	
	/**
	 * 重写save，增加缓存操作
	 */
	@Override
	public <S extends AccountInfo> S save(S accountInfo) {
		S s=  super.save(accountInfo);
		//设置限流器
		Integer limiter = accountInfo.getLimiter();
		if(limiter==null) {
			limiter=0;
		}
		if(limiter>0) {
			//设置最大并发
			accountInfo.setAccountLimiter(RateLimiter.create(limiter));
		} else {
			//默认最大并发10
			accountInfo.setAccountLimiter(RateLimiter.create(10));
		}
		//修改缓存账号
		AccountInfo account = getAccountMap().get(accountInfo.getAccount());
		if (account != null) {
			// 存在账号覆盖
			logger.info("修改账号："+accountInfo.getAccount());
			getAccountMap().put(accountInfo.getAccount(), accountInfo);
			if (s.getAccountStatus() == 0 || s.getIsDelete() == 1) {
				// 移除账号
				logger.info("删除账号："+accountInfo.getAccount());
				getAccountMap().remove(accountInfo.getAccount());
			}
		} else {
			// 缓存增加限流器
			logger.info("添加账号："+accountInfo.getAccount());
			getAccountMap().put(accountInfo.getAccount(), accountInfo);
		}
		return s;
	}
	/**
	 * 账号缓存,每30秒同步一次
	 */
	@PostConstruct
	@Scheduled(cron = "0/30 * * * * ?")
	public void accountCache(){
		//查询未删除和启用的账号
		StringBuilder sbLimit= new StringBuilder("");
		sbLimit.append(" select * from tb_account where accountStatus =1  and isDelete = 0 ");
		List<AccountInfo> accountAllList = jdbcTemplate.query(sbLimit.toString(), new BeanPropertyRowMapper<AccountInfo>(AccountInfo.class));
		if(accountAllList!=null) {
			for (AccountInfo accountInfo : accountAllList) {
				if(accountInfo!=null) {
					Integer limiter = accountInfo.getLimiter();
					if(limiter==null) {
						limiter=0;
					}
					if(limiter>0) {
						//设置最大并发
						accountInfo.setAccountLimiter(RateLimiter.create(limiter));
					} else {
						//默认最大并发10
						accountInfo.setAccountLimiter(RateLimiter.create(10));
					}
					accountMap.put(accountInfo.getAccount(), accountInfo);
				}
			}
		}
	}
	
}
