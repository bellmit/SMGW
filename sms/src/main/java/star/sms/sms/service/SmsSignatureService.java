package star.sms.sms.service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.sms.dao.SmsSignatureDao;
import star.sms.sms.domain.SmsSignature;

/**
 * 短信签名
 * @author star
 *
 */
@Service
@Transactional
public class SmsSignatureService extends BaseServiceProxy<SmsSignature> {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private SmsSignatureDao smsSignatureDao;
	@Autowired
	private EntityManager entityManager;

	@Override
	protected BaseRepository<SmsSignature> getBaseRepository() {
		return smsSignatureDao;
	}
	
	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * 分页查
	 * @param keyword
	 * @param pageable
	 * @return
	 */
	public Page findByPage(String keyword, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" FROM tb_sms_signature a where createUserId='"+getLoginUser().getId()+"' ");
		if(StringUtils.isNotEmpty(keyword)) {
			fromWhereSql.append(" and title like '%"+keyword+"%' ");
		}
		Page<SmsSignature> page = super.findPageBySql(SmsSignature.class, "select * ", fromWhereSql.toString(), " order by createTime desc", null, pageable);
		return page;
	}
	
	/**
	 * 批量删除信息
	 * @param ids
	 */
	public void deleteByIds(String ids) {
		if(ids.endsWith(",")) ids=ids.substring(0,ids.length()-1);
		jdbcTemplate.update("delete from tb_sms_signature where id in ("+ids+")");
	}
}