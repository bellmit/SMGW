package star.sms.notify.service;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.notify.dao.NotifyDao;
import star.sms.notify.domain.Notify;
import star.sms.notify.vo.NotifyParam;

/**
 * 系统公告
 */
@Service
public class NotifyService extends BaseServiceProxy<Notify>{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NotifyDao notifyDao;
    @Autowired
    private EntityManager entityManager;
	@Override
	protected BaseRepository<Notify> getBaseRepository() {
		return notifyDao;
	}
	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}
	
	/**
	 * 分页查
	 * @param keyword
	 * @param loginUser
	 * @param pageable
	 * @return
	 */
	public Page findByPage(String keyword,Integer isRead, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" FROM tb_notify a,tb_notify_user b where a.id=b.notifyId ");
		fromWhereSql.append(" and b.userId='"+getLoginUser().getId()+"' ");
		if(StringUtils.isNotEmpty(keyword)) {
			fromWhereSql.append("  and a.title like '%"+keyword+"%' ");
		}
		if(isRead!=null && isRead!=-1) {
			fromWhereSql.append("  and b.isRead='"+isRead+"' ");
		}
		Page<NotifyParam> page = super.findPageBySql(NotifyParam.class, "select a.title,a.createTime,a.createName,b.notifyId,b.isRead,b.id ", fromWhereSql.toString(), " order by a.createTime desc", null, pageable);
		return page;
	}
}
