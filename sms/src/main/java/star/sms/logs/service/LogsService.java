package star.sms.logs.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.logs.dao.LogsRepository;
import star.sms.logs.domain.Logs;
import star.sms.sms.domain.SmsTask;
import star.sms.sysconfig.domain.SysConfig;
import star.sms.sysconfig.service.SysConfigService;

/**
 * 日志信息
 * @author star
 */
@Slf4j
@Service
@Transactional
public class LogsService extends BaseServiceProxy<Logs> {
	
	@Resource
	private LogsRepository logsRepository;
	
	@Resource
	private JdbcTemplate jdbcTemplate;
	
	@Resource
	private EntityManager entityManager;
	
	@Resource
	private SysConfigService smsConfigService;
	
	@Override
	protected BaseRepository<Logs> getBaseRepository() {
		return logsRepository;
	}
	
	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}
	
	public boolean addData(String logNr){
		try {
			Logs l = new Logs();
			l.setLogTjsj(new Timestamp(System.currentTimeMillis()));
			l.setLogNr(logNr);
			l.setLogTjr(getLoginUser().getNickName());
			this.save(l);
			log.info(logNr);
			return true;
		} catch (Exception e) {
			log.error("写日志错误。"+e.getMessage());
		}
		return false;
	}
	
	/**
	 * 分页查
	 * @param keyword
	 * @param loginUser
	 * @param pageable
	 * @return
	 */
	public Page findByPage(String keyword,String startTime,String endTime, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" FROM tb_logs a where 1=1 ");
		if(StringUtils.isNotEmpty(keyword)) {
			fromWhereSql.append(" and a.log_nr like '%"+keyword+"%' ");
		}
		if(StringUtils.isNotEmpty(startTime)) {
			fromWhereSql.append(" and a.log_tjsj>='"+startTime+"'");
		}
		if(StringUtils.isNotEmpty(endTime)) {
			fromWhereSql.append(" and a.log_tjsj<='"+endTime+" 23:59:59'");
		}
		Page<Logs> page = super.findPageBySql(Logs.class, "select * ", fromWhereSql.toString(), " order by a.log_tjsj desc", null, pageable);
		return page;
	}
	
	/**
	 * 清除指定用户的记录
	 * @param userId
	 */
	public void clean(Integer userId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<SmsTask> list = jdbcTemplate.query("select * from tb_sms_task where createTime<now() and createUserId='"+userId+"' and sendStatus in (-1,1,2,3)", new BeanPropertyRowMapper<SmsTask>(SmsTask.class));
		String ids="";
		for(SmsTask task:list) {
			ids = ids+task.getId()+",";
			this.addData("清除任务信息：{任务ID："+task.getId()+"，任务名称："+task.getTitle()+"，会员名称："+task.getNickName()+"，任务发送时间："+(task.getSendTime()==null?"":sdf.format(task.getSendTime()))+"，任务数据总条数："+task.getTotalCount()+"}");
		}
		if (ids.endsWith(",")) ids = ids.substring(0, ids.length() - 1);
		if(ids.length()>0) {
			List<String> sqlList = new ArrayList<String>();
			sqlList.add("delete from tb_sms where taskId in ("+ids+") ");
			sqlList.add("delete from tb_sms_batch where taskId in ("+ids+") ");
			
			for(String sql:sqlList) {
				try {
					jdbcTemplate.update(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 定时清除指定所有用户的记录
	 * @param userId
	 */
	public void cleanAll() {
		SysConfig sysConfig = smsConfigService.getConfig();
		Integer saveDays = sysConfig.getSaveDays()==null?0:sysConfig.getSaveDays();
		this.addData("清除任务信息,保留"+saveDays+"天数据");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<SmsTask> list = jdbcTemplate.query("select * from tb_sms_task where DATEDIFF(NOW(),sendTime)>"+saveDays+" and sendStatus in (-1,1,2,3)", new BeanPropertyRowMapper<SmsTask>(SmsTask.class));
		String ids="";
		for(SmsTask task:list) {
			ids = ids+task.getId()+",";
			this.addData("清除任务信息：{任务ID："+task.getId()+"，任务名称："+task.getTitle()+"，会员名称："+task.getNickName()+"，任务发送时间："+(task.getSendTime()==null?"":sdf.format(task.getSendTime()))+"，任务数据总条数："+task.getTotalCount()+"}");
		}
		if (ids.endsWith(",")) ids = ids.substring(0, ids.length() - 1);
		if(ids.length()>0) {
			List<String> sqlList = new ArrayList<String>();
			sqlList.add("delete from tb_sms_task where id in ("+ids+") ");
			sqlList.add("delete from tb_sms_batch where taskId in ("+ids+") ");
			sqlList.add("delete from tb_sms where taskId in ("+ids+") ");
			
			for(String sql:sqlList) {
				try {
					jdbcTemplate.update(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
