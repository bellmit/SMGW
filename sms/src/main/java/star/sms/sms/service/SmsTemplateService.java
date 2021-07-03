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
import star.sms.sms.dao.SmsTemplateDao;
import star.sms.sms.domain.SmsTemplate;

/**
 * 短信批次
 */
@Service
@Transactional
public class SmsTemplateService extends BaseServiceProxy<SmsTemplate>{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SmsTemplateDao smsTemplateDao;
    @Autowired
    private EntityManager entityManager;
	@Override
	protected BaseRepository<SmsTemplate> getBaseRepository() {
		return smsTemplateDao;
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
	public Page findByPage(String keyword,String approveStatus, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" FROM tb_sms_template a where createUserId='"+getLoginUser().getId()+"' ");
		if(StringUtils.isNotEmpty(keyword)) {
			fromWhereSql.append("  and (title like '%"+keyword+"%' or content like '%"+keyword+"%') ");
		}
		if(StringUtils.isNotEmpty(approveStatus)) {
			fromWhereSql.append(" and approveStatus='"+approveStatus+"' ");
		}
		Page<SmsTemplate> page = super.findPageBySql(SmsTemplate.class, "select * ", fromWhereSql.toString(), " order by createTime desc", null, pageable);
		return page;
	}
	
	/**
	 * 分页查
	 * @param keyword
	 * @param pageable
	 * @return
	 */
	public Page getSmsTemplateApproveList(String keyword,String approveStatus, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" FROM tb_sms_template a left join tb_plat_manager b on a.createUserId=b.id where 1=1 ");
		if(StringUtils.isNotEmpty(keyword)) {
			fromWhereSql.append("  and (title like '%"+keyword+"%' or content like '%"+keyword+"%') ");
		}
		if(StringUtils.isNotEmpty(approveStatus)&&!"-1".equals(approveStatus)) {
			fromWhereSql.append(" and approveStatus='"+approveStatus+"' ");
		}
		Page<SmsTemplate> page = super.findPageBySql(SmsTemplate.class, "select a.*,b.nick_name as nickName ", fromWhereSql.toString(), " order by createTime desc", null, pageable);
		return page;
	}
	
	/**
	 * 批量删除信息
	 * @param ids
	 */
	public void deleteByIds(String ids) {
		if(ids.endsWith(",")) ids=ids.substring(0,ids.length()-1);
		jdbcTemplate.update("delete from tb_sms_template where id in ("+ids+")");
	}
}
