package star.sms.sms.job;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import star.sms.sms.domain.SmsTask;

/**
 * 自动加载新词库
 */
@Slf4j
@Component
@EnableScheduling   // 2.开启定时任务
public class ReportSyncJob {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	public static volatile Timestamp TASK_TIME = new Timestamp(System.currentTimeMillis());
	
	/**
	 * 更新任务的统计信息
	 */
    @Scheduled(cron = "0/10 * * * * ?")
    public void syncTask(){
    	Long diffTime = System.currentTimeMillis()-TASK_TIME.getTime();
    	if(diffTime<15*1000) {
    		log.info("开始更新任务统计数据");
    		//1.查询出来所有待更新的任务
    		List<SmsTask> list = jdbcTemplate.query("select * FROM tb_sms_task where sendStatus>=0 and (totalCount is null or totalCount=0 or (totalCount>successCount+failCount+unknowCount))", new BeanPropertyRowMapper<SmsTask>(SmsTask.class));
    		
    		//2.将所有的需要更新的任务id组合成列表
    		if(list!=null && list.size()>0) {
    			//3.查询出来所有的短信状态记录
    			StringBuffer sb = new StringBuffer();
    			sb.append("select taskId,sendStatus,count(1) totalCount from tb_sms where 1=0 ");
    			List<Integer> l = new ArrayList<>();
    			for(SmsTask task:list) {
    				l.add(task.getId());
    				if(l.size()>=1000) {
    					sb.append(" or taskId in ("+StringUtils.join(l,",")+")");
    					l = new ArrayList<>();
    				}
    			}
    			if(l.size()>0) sb.append(" or taskId in ("+StringUtils.join(l,",")+")");
    			sb.append(" group by taskId,sendStatus");
    			List<Map<String, Object>> listMap = jdbcTemplate.queryForList(sb.toString());
    			
    			//4.同步短信状态记录，格式化为需要的字段
    			Map<String, Integer> resultMap = new HashMap<String, Integer>();
    			for(Map<String, Object> map:listMap) {
    				Integer taskId = map.get("taskId")==null?0:((Integer) map.get("taskId")).intValue();
    				Integer sendStatus = map.get("sendStatus")==null?0:((Integer) map.get("sendStatus")).intValue();
    				Integer count = map.get("totalCount")==null?0:((Long) map.get("totalCount")).intValue();
    				
    				resultMap.put(taskId+"_"+sendStatus, count);
    			}
    			
    			//5.查询需要更新的sql语句和内容
    			List<String> sqlList = new ArrayList<String>();
    			for(SmsTask info : list) {
    				Integer successCount=resultMap.get(info.getId()+"_1")==null?0:resultMap.get(info.getId()+"_1");
    				Integer successCount2=resultMap.get(info.getId()+"_5")==null?0:resultMap.get(info.getId()+"_5");
    				successCount = successCount+successCount2;//1、5属于发送成功
    				Integer failCount=resultMap.get(info.getId()+"_2")==null?0:resultMap.get(info.getId()+"_2");
    				Integer sendingCount=resultMap.get(info.getId()+"_4")==null?0:resultMap.get(info.getId()+"_4");
    				Integer unknowCount=resultMap.get(info.getId()+"_3")==null?0:resultMap.get(info.getId()+"_3");
    				Integer totalCount = successCount+failCount+sendingCount+unknowCount+(resultMap.get(info.getId()+"_0")==null?0:resultMap.get(info.getId()+"_0"));
    				if(info.getTotalCount()!=totalCount
    						||info.getSuccessCount()!=successCount
    						||info.getFailCount()!=failCount
    						||info.getSendingCount()!=sendingCount
    						||info.getUnknowCount()!=unknowCount) {
    					sqlList.add("update tb_sms_task set totalCount='"+totalCount+"',successCount='"+successCount+"',failCount='"+failCount+"',sendingCount='"+sendingCount+"',unknowCount='"+unknowCount+"' where id='"+info.getId()+"' ");
    					
    					if(sqlList.size()>=1000) {
    						jdbcTemplate.batchUpdate(sqlList.toArray(new String[sqlList.size()]));
    						sqlList=new ArrayList<String>();
    					}
    				}
    			}
    			if(sqlList.size()>0) jdbcTemplate.batchUpdate(sqlList.toArray(new String[sqlList.size()]));
    		}
    		log.info("结束更新任务统计数据");
    	}
    }
}
