package star.sms.wallet.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms._frame.utils.StringUtils;
import star.sms.wallet.dao.WalletLogDao;
import star.sms.wallet.domain.Wallet;
import star.sms.wallet.domain.WalletLog;
import star.sms.wallet.vo.WalletLogParams;

/**
 * 客户钱包
 */
@Service
@Transactional
public class WalletLogService extends BaseServiceProxy<WalletLog>{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private WalletLogDao walletLogDao;
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private WalletService walletService;
    
	@Override
	protected BaseRepository<WalletLog> getBaseRepository() {
		return walletLogDao;
	}
	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * 插入钱包日志
	 * @param walletLogList
	 */
	public void insertWalletLog(WalletLog walletLog) {	
		walletLogDao.save(walletLog);
	}
	
	
	/**
	 * 分页查
	 * @param keyword 备注
	 * @param loginUser
	 * @param pageable
	 * @return
	 */
	public Page findByPage(String keyword, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" FROM tb_wallet_log a where userId='"+getLoginUser().getId()+"'  ");
		if(StringUtils.isNotEmpty(keyword)) {
			fromWhereSql.append("  and memo like '%"+keyword+"%' ");
		}
		Page<WalletLog> page = super.findPageBySql(WalletLog.class, "select * ", fromWhereSql.toString(), " order by id desc", null, pageable);
		return page;
	}
	
	/**
	 * 分页查
	 * @param keyword 备注
	 * @param loginUser
	 * @param pageable
	 * @return
	 */
	public Page findByPage2(String keyword, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" FROM tb_wallet_log a,tb_plat_manager b where a.userId=b.id and a.memo like '%充值了%'  ");
		if(!getLoginUser().getRoleCode().equals("ADMIN")) {
			fromWhereSql.append(" and a.userId='"+getLoginUser().getId()+"' ");
		}
		if(StringUtils.isNotEmpty(keyword)) {
			fromWhereSql.append("  and a.memo like '%"+keyword+"%' ");
		}
		Page<WalletLogParams> page = super.findPageBySql(WalletLogParams.class, "select a.*,b.login_name loginName,b.nick_name nickName ", fromWhereSql.toString(), " order by a.createTime desc", null, pageable);
		return page;
	}
}
