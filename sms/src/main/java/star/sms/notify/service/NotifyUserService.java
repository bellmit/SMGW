package star.sms.notify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.notify.dao.NotifyUserDao;
import star.sms.notify.domain.NotifyUser;

/**
 * 系统公告
 */
@Service
public class NotifyUserService extends BaseServiceProxy<NotifyUser>{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NotifyUserDao notifyUserDao;
	@Override
	protected BaseRepository<NotifyUser> getBaseRepository() {
		return notifyUserDao;
	}
	
	/**
	 * 批量删除信息
	 * @param ids
	 */
	public void deleteByIds(String ids) {
		if(ids.endsWith(",")) ids=ids.substring(0,ids.length()-1);
		jdbcTemplate.update("delete from tb_notify_user where id in ("+ids+")");
	}
	
	/**
	 * 标记全部公告为已读
	 */
	public void readAll() {
		jdbcTemplate.update("update tb_notify_user set isRead=1 where userId='"+getLoginUser().getId()+"'");
	}
	
}
