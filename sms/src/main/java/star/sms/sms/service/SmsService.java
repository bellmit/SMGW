package star.sms.sms.service;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms._frame.utils.DateUtils;
import star.sms._frame.utils.StringUtils;
import star.sms._frame.utils.UUIDUtils;
import star.sms.account.domain.AccountInfo;
import star.sms.account.service.AccountService;
import star.sms.config.SystemConfig;
import star.sms.logs.service.LogsService;
import star.sms.phonearea.domain.PhoneArea;
import star.sms.platmanager.domain.PlatManager;
import star.sms.platmanager.service.PlatManagerService;
import star.sms.sms.dao.SmsDao;
import star.sms.sms.dao.SmsTaskDao;
import star.sms.sms.domain.Sms;
import star.sms.sms.domain.SmsBatch;
import star.sms.sms.domain.SmsTask;
import star.sms.sms.vo.SendingDetailParam;
import star.sms.sms.vo.SmsDateParam;
import star.sms.sms.vo.SmsLogParam;
import star.sms.smsmq.domain.http.ReportRequest;
import star.sms.smsmq.domain.http.ReportResponse;
import star.sms.smsmq.domain.http.ReportResponseMobile;
import star.sms.smsmq.domain.http.SendRequest;
import star.sms.smsmq.domain.http.SendResponse;
import star.sms.smsmq.domain.http.SendResponseMobile;
import star.sms.smsmq.domain.httpv2.HttpV2Errorstatus;
import star.sms.smsmq.domain.httpv2.HttpV2SendRequest;
import star.sms.smsmq.domain.httpv2.HttpV2SendResponse;
import star.sms.smsmq.domain.httpv2.HttpV2StatusRequest;
import star.sms.smsmq.domain.httpv2.HttpV2StatusResponse;
import star.sms.smsmq.domain.httpv2.HttpV2Statusbox;
import star.sms.smsmq.domain.httpv3.HttpV3Errorstatus;
import star.sms.smsmq.domain.httpv3.HttpV3SendRequest;
import star.sms.smsmq.domain.httpv3.HttpV3SendResponse;
import star.sms.smsmq.domain.httpv3.HttpV3StatusRequest;
import star.sms.smsmq.domain.httpv3.HttpV3StatusResponse;
import star.sms.smsmq.domain.httpv3.HttpV3Statusbox;
import star.sms.smsmq.domain.smpp.SendRequestSmpp;
import star.sms.smsmq.smstask.ReportTask;
import star.sms.smsmq.smstask.SendTask;
import star.sms.wallet.domain.Wallet;
import star.sms.wallet.service.WalletService;
import star.smscore.codec.smpp.msg.SubmitSmResp;

/**
 * 短信接口
 */
@Slf4j
@Service
public class SmsService extends BaseServiceProxy<Sms>{
	
	private Lock lock = new ReentrantLock();

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private SmsDao smsDao;
    
    @Autowired
    private SmsTaskDao smsTaskDao;
    
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private PlatManagerService platManagerService;
    
    @Autowired
    private SmsBatchService smsBatchService;
    
    @Autowired
    private RedissonClient redissonClient;
    
    @Autowired
    private SystemConfig systemConfig;
    
    @Autowired
    private SendTask sendTask;
    
    @Autowired
    private ReportTask reportTask;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private LogsService logsService;
    
	@Override
	protected BaseRepository<Sms> getBaseRepository() {
		return smsDao;
	}
	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}
	/**
	 * http提交短信开始
	 */
	@Deprecated
	public void sendStart(SendRequest sendRequest,String batchId ) {
		Timestamp now=new Timestamp(System.currentTimeMillis());
		//插入批次表
		SmsBatch smsBatch = new SmsBatch();
		smsBatch.setId(batchId);
		smsBatch.setCreateTime(now);
		smsBatch.setCreateUserId(sendRequest.getCreateUserId());
		smsBatch.setUpdateTime(now);
		smsBatch.setUpdateUserId(sendRequest.getCreateUserId());
		smsBatch.setTaskId(sendRequest.getTaskId());
		smsBatch.setContent(sendRequest.getTaskContent());
		smsBatch.setSendStatus(0);
		smsBatch.setSendTime(now);
		smsBatchService.insertSmsBatch(smsBatch);
	}

	/**
	 * http提交短信结果
	 */
	@Deprecated
	public void sendSuccess(SendRequest sendRequest,SendResponse result,String batchId) {
		Timestamp now=new Timestamp(System.currentTimeMillis());
		String serialNumber=UUIDUtils.random();
		log.info(" 返回发送结果 serialNumber: "+serialNumber+" , 返回内容: " + result);
		//更新批次表
		log.info(" 更新批次表 serialNumber: "+serialNumber);

		SmsBatch smsBatch = new SmsBatch();
		smsBatch.setId(batchId);
		smsBatch.setSendStatus(4);
		smsBatch.setSendResult(Integer.valueOf(result.getStatus()));
		smsBatch.setUpdateTime(now);

		smsBatchService.updateSmsBatch(smsBatch);
		//更新短信表
		StringBuilder sb= new StringBuilder("");
		sb.append(" update  tb_sms set mid=?,sendStatus=?,sendResult=?,batchId=? where id=?");
		//得到请求顺序与相应数据匹配更新
		List<SendResponseMobile>  sendResponseMobileList= result.getList();
		List<Object[]> argsList= new ArrayList<Object[]>();
		List<Integer> mobileIdList = sendRequest.getMobileIdList();
		for (int i = 0; i < mobileIdList.size(); i++) {
			int mobileId = mobileIdList.get(i);
			if(sendResponseMobileList!=null) {
				SendResponseMobile sendResponseMobile = sendResponseMobileList.get(i);
				if(sendResponseMobile!=null) {
					String mid = sendResponseMobile.getMid();
					int returnResult =  sendResponseMobile.getResult();
					//发送中
					int sendStatus=4;
					if(returnResult == 0) {
						//发送成功
						returnResult=1;
					} else {
						sendStatus=2;
					}
					Object[] smsArray= {mid,sendStatus,returnResult,batchId,mobileId};
					argsList.add(smsArray);
				}
			}
		}
		if(argsList.size()>0) {
			jdbcTemplate.batchUpdate(sb.toString(), argsList);
		}
	}
	/**
	 * httpV2提交短信开始
	 */
	public void sendStartV2(HttpV2SendRequest sendRequest,String batchId ) {
		Timestamp now=new Timestamp(System.currentTimeMillis());
		//插入批次表
		SmsBatch smsBatch = new SmsBatch();
		smsBatch.setId(batchId);
		smsBatch.setCreateTime(now);
		smsBatch.setCreateUserId(sendRequest.getCreateUserId());
		smsBatch.setUpdateTime(now);
		smsBatch.setUpdateUserId(sendRequest.getCreateUserId());
		smsBatch.setTaskId(sendRequest.getTaskId());
		smsBatch.setContent(sendRequest.getTaskContent());
		smsBatch.setSendStatus(0);
		smsBatch.setSendTime(now);
		smsBatchService.insertSmsBatch(smsBatch);
	}

	/**
	 * httpV2提交短信结果
	 */
	public void sendSuccessV2(HttpV2SendRequest sendRequest,HttpV2SendResponse sendResponse,String batchId) {
		Timestamp now=new Timestamp(System.currentTimeMillis());
		String serialNumber=UUIDUtils.random();
		log.info(" httpv2返回发送结果 serialNumber: "+serialNumber+" , 返回内容: " + sendResponse);
		//提交记录
		int returnResult = 0;
		int sendStatusBatch = 0;
		String message = "";
		String submitTaskId = "-1";
		//得到请求顺序与相应数据匹配更新
		List<Object[]> argsList= new ArrayList<Object[]>();
		List<Integer> mobileIdList = sendRequest.getMobileIdList();
		List<String> mobileList = sendRequest.getMobileList();
		//如果有返回值
		if(sendResponse!=null) {
			//提交成功返回参数
			String returnstatus = sendResponse.getReturnstatus();
			message = sendResponse.getMessage();
			submitTaskId = sendResponse.getTaskID();
			//提交结果,成功返回Success 失败返回：Faild
			if("Success".equals(returnstatus)) {
				returnResult = 1;
				sendStatusBatch = 1;
			} else {
				returnResult = 0;
				sendStatusBatch = 2;
			}
		} 
		//记录batch
		SmsBatch smsBatch = new SmsBatch();
		smsBatch.setId(batchId);
		smsBatch.setSendStatus(sendStatusBatch);
		smsBatch.setSendResult(returnResult);
		smsBatch.setUpdateTime(now);
		smsBatch.setSubmitTaskId(submitTaskId);
		smsBatchService.updateSmsBatch(smsBatch);
		//更新短信表
		StringBuilder sb= new StringBuilder("");
		sb.append(" update tb_sms set sendStatus=?,sendResult=?,batchId=?,memo=?,sendStat=?,mid=? where id=?");

		for (int i = 0; i < mobileIdList.size(); i++) {
			//短信id
			int smsId = mobileIdList.get(i);
			String mobile = mobileList.get(i);
			//发送中
			int sendStatus=4;
			String sendStat = "";
			if(returnResult == 0) {
				//提交失败，也就是发送失败
				sendStatus=2;
				//发送失败
				sendStat="SUBMITFAILD";
			}
			if(sendResponse==null) {
				//提交失败，也就是发送失败
				sendStatus=2;
				//发送失败
				sendStat="RESPONSEFAILD";
			}
			String  mid = submitTaskId+mobile;
			Object[] smsArray= {sendStatus,returnResult,batchId,message,sendStat,mid,smsId};
			argsList.add(smsArray);
		}
		if(argsList.size()>0) {
			jdbcTemplate.batchUpdate(sb.toString(), argsList);
		}
		//提交失败返还金额,一般不会发生
		if(returnResult == 0) {
			walletService.updateWalletMoneyByUserIdSmsNum(sendRequest.getCreateUserId(),mobileIdList.size());
		}
	}
	
	/**
	 * httpV3提交短信开始
	 */
	public void sendStartV3(HttpV3SendRequest sendRequest,String batchId ) {
		Timestamp now=new Timestamp(System.currentTimeMillis());
		//插入批次表
		SmsBatch smsBatch = new SmsBatch();
		smsBatch.setId(batchId);
		smsBatch.setCreateTime(now);
		smsBatch.setCreateUserId(sendRequest.getCreateUserId());
		smsBatch.setUpdateTime(now);
		smsBatch.setUpdateUserId(sendRequest.getCreateUserId());
		smsBatch.setTaskId(sendRequest.getTaskId());
		smsBatch.setContent(sendRequest.getTaskContent());
		smsBatch.setSendStatus(0);
		smsBatch.setSendTime(now);
		smsBatchService.insertSmsBatch(smsBatch);
	}
	/**
	 * httpV3提交短信结果
	 */
	public void sendSuccessV3(HttpV3SendRequest sendRequest,HttpV3SendResponse sendResponse,String batchId) {
		Timestamp now=new Timestamp(System.currentTimeMillis());
		String serialNumber=UUIDUtils.random();
		log.info(" httpv3返回发送结果 serialNumber: "+serialNumber+" , 返回内容: " + sendResponse);
		//提交记录
		int returnResult = 0;
		int sendStatusBatch = 0;
		String message = "";
		String submitTaskId = "-1";
		//得到请求顺序与相应数据匹配更新
		List<Object[]> argsList= new ArrayList<Object[]>();
		List<Integer> mobileIdList = sendRequest.getMobileIdList();
		List<String> mobileList = sendRequest.getMobileList();
		//如果有返回值
		if(sendResponse!=null) {
			//提交成功返回参数
			String returnstatus = sendResponse.getReturnstatus();
			message = sendResponse.getMessage();
			submitTaskId = sendResponse.getTaskID();
			//提交结果,成功返回Success 失败返回：Faild
			if("Success".equals(returnstatus)) {
				returnResult = 1;
				sendStatusBatch = 1;
			} else {
				returnResult = 0;
				sendStatusBatch = 2;
			}
		} 
		//记录batch
		SmsBatch smsBatch = new SmsBatch();
		smsBatch.setId(batchId);
		smsBatch.setSendStatus(sendStatusBatch);
		smsBatch.setSendResult(returnResult);
		smsBatch.setUpdateTime(now);
		smsBatch.setSubmitTaskId(submitTaskId);
		smsBatchService.updateSmsBatch(smsBatch);
		//更新短信表
		StringBuilder sb= new StringBuilder("");
		sb.append(" update tb_sms set sendStatus=?,sendResult=?,batchId=?,memo=?,sendStat=?,mid=? where id=?");

		for (int i = 0; i < mobileIdList.size(); i++) {
			//短信id
			int smsId = mobileIdList.get(i);
			String mobile = mobileList.get(i);
			//发送中
			int sendStatus=4;
			String sendStat = "";
			if(returnResult == 0) {
				//提交失败，也就是发送失败
				sendStatus=2;
				//发送失败
				sendStat="SUBMITFAILD";
			}
			if(sendResponse==null) {
				//提交失败，也就是发送失败
				sendStatus=2;
				//发送失败
				sendStat="RESPONSEFAILD";
			}
			String  mid = submitTaskId+mobile;
			Object[] smsArray= {sendStatus,returnResult,batchId,message,sendStat,mid,smsId};
			argsList.add(smsArray);
		}
		if(argsList.size()>0) {
			jdbcTemplate.batchUpdate(sb.toString(), argsList);
		}
		//提交失败返还金额,一般不会发生
		if(returnResult == 0) {
			walletService.updateWalletMoneyByUserIdSmsNum(sendRequest.getCreateUserId(),mobileIdList.size());
		}
	}
	/**
	 * 定时读取未发送短信到消息队列
	 * @return
	 */
	@Deprecated
	public void readNotSentSmsToMqHttp(){
		if(!systemConfig.getIsTest()) {
			if(systemConfig.getIsAdmin()) return;
		}
		String serialNumber=UUIDUtils.random();
		// 短信列表
		List<Sms> smsList =new ArrayList<Sms>();
		// 短信任务分组
		Map<Integer, List<Sms>> smsMap = new HashMap<Integer, List<Sms>>();
		
		lock.lock();
		try {
			log.info(" 开始读取http短信 serialNumber: "+serialNumber+" , 读取内容: " + smsList);
			// 读取待发送
			StringBuilder sb= new StringBuilder("");
			sb.append(" select a.*,b.contentType,b.content as taskContent from tb_sms a left join tb_sms_task b on a.taskId=b.id where b.channelType=1 and a.sendStatus=0 and b.sendStatus in (0,4) and b.sendTime<=now() order by b.priority desc limit 400 ");
			smsList =  jdbcTemplate.query(sb.toString(), new BeanPropertyRowMapper<Sms>(Sms.class));
			if(smsList.size()==0) {
				return;
			}
			// 按任务分组
			if(smsList!=null) {
				smsMap = smsList.stream().filter(sms -> sms.getTaskId()!= null).collect(Collectors.groupingBy(Sms::getTaskId));
			}
			// 更新短信为发送中
			StringBuilder sbUpdate= new StringBuilder("");
			sbUpdate.append("update tb_sms set sendStatus=4 where id=? ");
			List<Object[]> argsList= new ArrayList<Object[]>();
			if(smsList!=null) {
				for (Sms sms : smsList) {
					Object[] smsArray= {sms.getId()};
					argsList.add(smsArray);
				}
			}
			jdbcTemplate.batchUpdate(sbUpdate.toString(), argsList);
		} finally {
            lock.unlock();
        }
		if(smsMap!=null&&smsMap.size()>0) {
			for(Map.Entry<Integer, List<Sms>>  entry : smsMap.entrySet()) {
				// 按分组分发短信
				List<Sms> smsGroupList = entry.getValue();
				if(smsGroupList!=null && smsGroupList.size()>0) {
					//构造短信请求参数
					List<Integer> mobileIdList = new ArrayList<Integer>(); 
					List<String> mobileList = new ArrayList<String>(); 
					List<String> contentList = new ArrayList<String>();
					int taskId = -1;
					String action = "";
					String rt = "json";
					String taskContent = "";
					int contentType = 1;
					int createUserId=0;
					String account ="";
					String password ="";
					String extno ="";
					String ip ="";
					int i=0;
					for (Sms sms : smsGroupList) {
						contentType=sms.getContentType();
						if(i==0) {
							taskId = sms.getTaskId();
							taskContent=sms.getTaskContent();
							createUserId=sms.getCreateUserId();
							//设置账号
							account =sms.getAccount();
							password =sms.getPassword();
							extno =sms.getExtno();
							ip =sms.getIp();
							
						}
						mobileIdList.add(sms.getId());
						mobileList.add(sms.getPhone());
						contentList.add(sms.getContent());
						i++;
					}
					SendRequest sendRequest = new  SendRequest();
					sendRequest.setTaskId(taskId);
					sendRequest.setAction(action);
					sendRequest.setRt(rt);
					sendRequest.setTaskContent(taskContent);
					sendRequest.setContentType(contentType);
					sendRequest.setContentList(contentList);
					sendRequest.setMobileList(mobileList);
					sendRequest.setMobileIdList(mobileIdList);
					sendRequest.setCreateUserId(createUserId);
					
					sendRequest.setAccount(account);
					sendRequest.setPassword(password);
					sendRequest.setExtno(extno);
					sendRequest.setIp(ip);

					log.info(" 开始发送短信 serialNumber: "+serialNumber);

					sendTask.hanlderHttp(sendRequest);
				}
			}
		}
	}
	
	/**
	 * 定时读取未发送短信到消息队列,V2接口不支持点对点
	 * @return
	 */
	@Scheduled(cron = "0/2 * * * * ?")
	public void readNotSentSmsToMqHttpV2(){
		if(!systemConfig.getIsTest()) {
			if(systemConfig.getIsAdmin()) return;
		}
		String serialNumber=UUIDUtils.random();
		// 短信列表
		List<Sms> smsList =new ArrayList<Sms>();
		// 短信任务分组
		Map<Integer, List<Sms>> smsMap = new HashMap<Integer, List<Sms>>();
		
		lock.lock();
		try {
			log.info(" 开始读取httpv2短信 serialNumber: "+serialNumber+" , 读取内容: " + smsList);
			// 读取待发送
			StringBuilder sb= new StringBuilder("");
			sb.append(" SELECT a.id,a.taskId,a.content,a.createUserId,a.account,a.accountId,a.phone FROM tb_sms a WHERE a.taskId IN ( SELECT id FROM tb_sms_task b WHERE b.channelType = 4  AND b.sendStatus IN ( 0, 4 ) AND b.sendTime <= now() ) AND a.sendStatus = 0 ORDER BY a.priority DESC LIMIT 4000 ");
			smsList =  jdbcTemplate.query(sb.toString(), new BeanPropertyRowMapper<Sms>(Sms.class));
			if(smsList.size()==0) {
				return;
			}
			// 按任务分组
			if(smsList!=null) {
				smsMap = smsList.stream().filter(sms -> sms.getTaskId()!= null).collect(Collectors.groupingBy(Sms::getTaskId));
			}
			// 更新短信为发送中
			StringBuilder sbUpdate= new StringBuilder("");
			sbUpdate.append("update tb_sms set sendStatus=4 where id=? ");
			List<Object[]> argsList= new ArrayList<Object[]>();
			if(smsList!=null) {
				for (Sms sms : smsList) {
					Object[] smsArray= {sms.getId()};
					argsList.add(smsArray);
				}
			}
			jdbcTemplate.batchUpdate(sbUpdate.toString(), argsList);
		} finally {
            lock.unlock();
        }
		if(smsMap!=null&&smsMap.size()>0) {
			for(Map.Entry<Integer, List<Sms>>  entry : smsMap.entrySet()) {
				// 按分组分发短信
				List<Sms> smsGroupList = entry.getValue();
				if(smsGroupList!=null && smsGroupList.size()>0) {
					//构造短信请求参数
					List<Integer> mobileIdList = new ArrayList<Integer>(); 
					List<String> mobileList = new ArrayList<String>(); 
					int taskId = -1;
					String rt = "xml";
					String taskContent = "";
					String content = "";
					int contentType = 1;
					int createUserId=0;
					String account ="";
					int accountId=0;
					int i=0;
					for (Sms sms : smsGroupList) {
						if(i==0) {
							taskId = sms.getTaskId();
							//不支持点对点
							taskContent=sms.getContent();
							content = sms.getContent();
							createUserId=sms.getCreateUserId();
							//设置账号
							accountId =sms.getAccountId();
							account =sms.getAccount();
						}
						mobileIdList.add(sms.getId());
						mobileList.add(sms.getPhone());
						i++;
					}
					HttpV2SendRequest sendRequest = new  HttpV2SendRequest();
					sendRequest.setTaskId(taskId);
					sendRequest.setRt(rt);
					sendRequest.setTaskContent(taskContent);
					sendRequest.setContent(content);
					sendRequest.setContentType(contentType);
					sendRequest.setMobileList(mobileList);
					sendRequest.setMobileIdList(mobileIdList);
					sendRequest.setCreateUserId(createUserId);
					
					sendRequest.setAccountId(accountId);
					sendRequest.setAccount(account);
					sendRequest.setNumber(200);

					log.info(" 开始发送短信 serialNumber: "+serialNumber);

					sendTask.hanlderHttpV2(sendRequest);
				}
			}
		}
	}
	/**
	 * 定时读取未发送短信到消息队列,V3接口不支持点对点
	 * @return
	 */
	@Scheduled(cron = "0/2 * * * * ?")
	public void readNotSentSmsToMqHttpV3(){
		if(!systemConfig.getIsTest()) {
			if(systemConfig.getIsAdmin()) return;
		}
		String serialNumber=UUIDUtils.random();
		// 短信列表
		List<Sms> smsList =new ArrayList<Sms>();
		// 短信任务分组
		Map<Integer, List<Sms>> smsMap = new HashMap<Integer, List<Sms>>();
		
		lock.lock();
		try {
			log.info(" 开始读取httpv3短信 serialNumber: "+serialNumber+" , 读取内容: " + smsList);
			// 读取待发送
			StringBuilder sb= new StringBuilder("");
			sb.append(" SELECT a.id,a.taskId,a.content,a.createUserId,a.account,a.accountId,a.phone FROM tb_sms a WHERE a.taskId IN ( SELECT id FROM tb_sms_task b WHERE b.channelType = 5  AND b.sendStatus IN ( 0, 4 ) AND b.sendTime <= now() ) AND a.sendStatus = 0 ORDER BY a.priority DESC LIMIT 4000 ");
			smsList =  jdbcTemplate.query(sb.toString(), new BeanPropertyRowMapper<Sms>(Sms.class));
			if(smsList.size()==0) {
				return;
			}
			// 按任务分组
			if(smsList!=null) {
				smsMap = smsList.stream().filter(sms -> sms.getTaskId()!= null).collect(Collectors.groupingBy(Sms::getTaskId));
			}
			// 更新短信为发送中
			StringBuilder sbUpdate= new StringBuilder("");
			sbUpdate.append("update tb_sms set sendStatus=4 where id=? ");
			List<Object[]> argsList= new ArrayList<Object[]>();
			if(smsList!=null) {
				for (Sms sms : smsList) {
					Object[] smsArray= {sms.getId()};
					argsList.add(smsArray);
				}
			}
			jdbcTemplate.batchUpdate(sbUpdate.toString(), argsList);
		} finally {
            lock.unlock();
        }
		if(smsMap!=null&&smsMap.size()>0) {
			for(Map.Entry<Integer, List<Sms>>  entry : smsMap.entrySet()) {
				// 按分组分发短信
				List<Sms> smsGroupList = entry.getValue();
				if(smsGroupList!=null && smsGroupList.size()>0) {
					//构造短信请求参数
					List<Integer> mobileIdList = new ArrayList<Integer>(); 
					List<String> mobileList = new ArrayList<String>(); 
					int taskId = -1;
					String rt = "xml";
					String taskContent = "";
					String content = "";
					int contentType = 1;
					int createUserId=0;
					String account ="";
					int accountId=0;
					int i=0;
					for (Sms sms : smsGroupList) {
						if(i==0) {
							taskId = sms.getTaskId();
							//不支持点对点
							taskContent=sms.getContent();
							content = sms.getContent();
							createUserId=sms.getCreateUserId();
							//设置账号
							accountId =sms.getAccountId();
							account =sms.getAccount();
						}
						mobileIdList.add(sms.getId());
						mobileList.add(sms.getPhone());
						i++;
					}
					HttpV3SendRequest sendRequest = new  HttpV3SendRequest();
					sendRequest.setTaskId(taskId);
					sendRequest.setRt(rt);
					sendRequest.setTaskContent(taskContent);
					sendRequest.setContent(content);
					sendRequest.setContentType(contentType);
					sendRequest.setMobileList(mobileList);
					sendRequest.setMobileIdList(mobileIdList);
					sendRequest.setCreateUserId(createUserId);
					
					sendRequest.setAccountId(accountId);
					sendRequest.setAccount(account);
					sendRequest.setNumber(4000);

					log.info(" 开始发送短信 serialNumber: "+serialNumber);

					sendTask.hanlderHttpV3(sendRequest);
				}
			}
		}
	}
	/**
	 * 定时读取未发送短信到消息队列
	 * @return
	 */
	@Scheduled(cron = "0/2 * * * * ?")
	public void readNotSentSmsToMqSmpp(){
		if(!systemConfig.getIsTest()) {
			if(systemConfig.getIsAdmin()) return;
		}
		String serialNumber=UUIDUtils.random();
		// 短信列表
		List<Sms> smsList =new ArrayList<Sms>();

		lock.lock();
		try {
			log.info(" 开始读取smpp短信 serialNumber: "+serialNumber+" , 读取内容: " + smsList);
			//读取待发送
			StringBuilder sb= new StringBuilder("");
			sb.append(" SELECT a.id,a.phone,a.content,a.createUserId,a.account,a.accountId FROM tb_sms a WHERE a.taskId IN ( SELECT id FROM tb_sms_task b WHERE b.channelType = 2 AND b.sendStatus IN ( 0, 4 ) AND b.sendTime <= now() ) AND a.sendStatus = 0 ORDER BY a.priority DESC LIMIT 4000 ");
			smsList =  jdbcTemplate.query(sb.toString(), new BeanPropertyRowMapper<Sms>(Sms.class));
			if(smsList.size()==0) {
				return;
			}
			//更新短信为发送中
			StringBuilder sbUpdate= new StringBuilder("");
			sbUpdate.append("update tb_sms set sendStatus=4 where id=? ");
			List<Object[]> argsList= new ArrayList<Object[]>();
			if(smsList!=null) {
				for (Sms sms : smsList) {
					Object[] smsArray= {sms.getId()};
					argsList.add(smsArray);
				}
			}
			jdbcTemplate.batchUpdate(sbUpdate.toString(), argsList);
		} finally {
            lock.unlock();
        }
		if (smsList != null) {
			for (Sms sms : smsList) {
				if(sms!=null) {
					SendRequestSmpp sendRequestSmpp =  new SendRequestSmpp();
					sendRequestSmpp.setSmsId(sms.getId());
					sendRequestSmpp.setMobile(sms.getPhone());
					sendRequestSmpp.setContent(sms.getContent());
					sendRequestSmpp.setAccountId(sms.getAccountId());
					sendRequestSmpp.setAccount(sms.getAccount());
					sendRequestSmpp.setCreateUserId(sms.getCreateUserId());
					sendTask.hanlderSmpp(sendRequestSmpp);
				}
			}
		}
	}
	/**
	 * 得到用户消费记录
	 * @param midSet
	 * @return
	 */
	@Deprecated
	public Map<Integer,BigDecimal>  findUserIdSmsNum(Set<String> midSet){
		//用户消费
		Map<Integer,BigDecimal> smsUserMoneyMap= new HashMap<Integer, BigDecimal>();
		StringBuilder mids= new StringBuilder();
		int i=0;
		for (String mid : midSet) {
			mids.append("'"+mid+"'");
			if(i<midSet.size()-1) {
				mids.append(",");
			}
			i++;
		}
		//账户发送短信数量
		StringBuilder sb= new StringBuilder("");
		sb.append(" select createUserId as userId,count(*) as count from tb_sms where mid in ("+mids+") group by createUserId ");
		
		Map<Integer,Integer> smsNumMap = jdbcTemplate.query(sb.toString(), new ResultSetExtractor<Map<Integer,Integer>>(){
			@Override
			public Map<Integer,Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<Integer,Integer> smsNumMap= new HashMap<Integer,Integer>();
				while(rs.next()) {
					Integer userId =  rs.getInt("userId");
					Integer count = rs.getInt("count");
					smsNumMap.put(userId, count);
				}
				return smsNumMap;
			}
		});
		
		//用户消费
		if(smsNumMap!=null) {
			Set<Integer> smsNumKeySet = smsNumMap.keySet();
			List<PlatManager> platManagerList = platManagerService.findUserListByUserId(smsNumKeySet);
			if(platManagerList!=null) {
				for (PlatManager platManager : platManagerList) {
					if(platManager.getPrice()!=null&&smsNumMap.get(platManager.getId())!=null) {
						smsUserMoneyMap.put(platManager.getId(), platManager.getPrice().multiply(new BigDecimal(smsNumMap.get(platManager.getId()))));
					}
				}
			}
		}
		return smsUserMoneyMap;
	}
	
	/**
	 * 定时查询http状态报告，更新短信表，每5秒查询500条
	 */
	@Transactional
	@Deprecated
	public void findSmsReportToMqHttp(){
		if(!systemConfig.getIsTest()) {
			if(systemConfig.getIsAdmin()) return;
		}
		log.info(" 开始查询http报告 ");
		//得到系统账号信息
		StringBuilder sbAccount= new StringBuilder("");
		sbAccount.append(" select * from tb_account where  accountStatus =1  and isDelete = 0  and channelType=1 ");
		List<AccountInfo> accountList = jdbcTemplate.query(sbAccount.toString(), new BeanPropertyRowMapper<AccountInfo>(AccountInfo.class));
		if(accountList!=null) {
			for (AccountInfo accountInfo : accountList) {
				if(accountInfo!=null) {
					//查询状态报告
					ReportRequest reportRequest = new ReportRequest();
					reportRequest.setIp(accountInfo.getIp());
					reportRequest.setAccount(accountInfo.getAccount());
					reportRequest.setPassword(accountInfo.getPassword());
					reportRequest.setSize(500);
					reportRequest.setAction("");
					reportRequest.setRt("");
					//请求放入mq
					reportTask.hanlderHttp(reportRequest);
				}
			}
		}
	}
	/**
	 * 定时查询httpV2状态报告，更新短信表，每5秒全部查询一遍
	 */
	@Scheduled(cron = "0/5 * * * * ?")
	@Transactional
	public void findSmsReportToMqHttpV2(){
		if(!systemConfig.getIsTest()) {
			if(systemConfig.getIsAdmin()) return;
		}
		log.info(" 开始查询httpV2报告 ");
		//得到系统账号信息
		StringBuilder sbAccount= new StringBuilder("");
		sbAccount.append(" select * from tb_account where  accountStatus =1  and isDelete = 0  and channelType=4 ");
		List<AccountInfo> accountList = jdbcTemplate.query(sbAccount.toString(), new BeanPropertyRowMapper<AccountInfo>(AccountInfo.class));
		if(accountList!=null) {
			for (AccountInfo accountInfo : accountList) {
				if(accountInfo!=null) {
					//查询状态报告
					HttpV2StatusRequest reportRequest = new HttpV2StatusRequest();
					reportRequest.setIp(accountInfo.getIp());
					reportRequest.setUserid(accountInfo.getUserid());
					reportRequest.setAccount(accountInfo.getAccount());
					reportRequest.setPassword(accountInfo.getPassword());
					reportRequest.setSize(2000);
					reportRequest.setAction("");
					reportRequest.setRt("");
					//请求放入mq
					reportTask.hanlderHttpV2(reportRequest);
				}
			}
		}
	}
	/**
	 * 定时查询httpV3状态报告，更新短信表，每5秒全部查询一遍
	 */
	@Scheduled(cron = "0/5 * * * * ?")
	@Transactional
	public void findSmsReportToMqHttpV3(){
		if(!systemConfig.getIsTest()) {
			if(systemConfig.getIsAdmin()) return;
		}
		log.info(" 开始查询httpV3报告 ");
		//得到系统账号信息
		StringBuilder sbAccount= new StringBuilder("");
		sbAccount.append(" select * from tb_account where  accountStatus =1  and isDelete = 0  and channelType=5 ");
		List<AccountInfo> accountList = jdbcTemplate.query(sbAccount.toString(), new BeanPropertyRowMapper<AccountInfo>(AccountInfo.class));
		if(accountList!=null) {
			for (AccountInfo accountInfo : accountList) {
				if(accountInfo!=null) {
					//查询状态报告
					HttpV3StatusRequest reportRequest = new HttpV3StatusRequest();
					reportRequest.setIp(accountInfo.getIp());
					reportRequest.setUserid(accountInfo.getUserid());
					reportRequest.setAccount(accountInfo.getAccount());
					reportRequest.setPassword(accountInfo.getPassword());
					reportRequest.setSize(2000);
					reportRequest.setAction("");
					reportRequest.setRt("");
					//请求放入mq
					reportTask.hanlderHttpV3(reportRequest);
				}
			}
		}
	}
	/**
	 * 更新smpp提交短信状态
	 * @param reportResponse
	 */
	@Deprecated
	public void updateSmsReportHttp(ReportResponse reportResponse){
		Timestamp now=new Timestamp(System.currentTimeMillis());
		//更新短信表
		StringBuilder sbUpdate= new StringBuilder("");
		sbUpdate.append(" update  tb_sms set sendStat=?,statTime=?,sendStatus=? where mid=? ");
		List<Object[]> argsList= new ArrayList<Object[]>();
		//消息id列表
		Set<String> midSet = new HashSet<String>();
		
		if(reportResponse!=null) {
			//状态入库
			List<ReportResponseMobile> reportList = reportResponse.getList();
			if(reportList!=null) {
				//返回状态列表
				for (ReportResponseMobile reportResponseMobile : reportList) {
					String mid = reportResponseMobile.getMid();
					String stat = reportResponseMobile.getStat();
					int sendStatus=4;
					if("DELIVRD".equals(stat)) {
						sendStatus=1;
						//成功计费
						midSet.add(mid);
					} else {
						sendStatus=2;
					}
					//更新短信状态
					Object[] smsArray= {stat,now,sendStatus,mid};
					argsList.add(smsArray);
				}
			}
		}
		if(argsList.size()>0) {
			//更新短信状态
			jdbcTemplate.batchUpdate(sbUpdate.toString(), argsList);
		}
	}

	/**
	 * 更新短信网关响应状态
	 * @param reportResponse
	 */
	public void updateSmsReportHttpV2(HttpV2StatusResponse reportResponse){
		//更新短信表
		StringBuilder sbUpdate= new StringBuilder("");
		sbUpdate.append(" update  tb_sms set sendStat=?,statTime=?,sendStatus=? where mid=? ");
		List<Object[]> argsList= new ArrayList<Object[]>();
		
		if(reportResponse!=null) {
			//返回值
			List<HttpV2Statusbox> statusboxList = reportResponse.getStatusboxList();
			//返回值错误代码,只有一个暂时使用list，不确定多个
			List<HttpV2Errorstatus> errorstatusList = reportResponse.getErrorstatusList();
			//错误打印
			if(errorstatusList!=null) {
				for (HttpV2Errorstatus httpV2Errorstatus : errorstatusList) {
					String error = httpV2Errorstatus.getError();
					String remark = httpV2Errorstatus.getRemark();
					//只在日志打印出来
					log.error("httpV2短信状态查询错误,error = " + error + ",remark = " + remark);
				}
			}
			if(statusboxList!=null) {
				//返回状态列表
				for (HttpV2Statusbox httpV2Statusbox : statusboxList) {
					String mobile = httpV2Statusbox.getMobile();
					String submitTaskId = httpV2Statusbox.getTaskid();
					String status = httpV2Statusbox.getStatus();
					String errorcode = httpV2Statusbox.getErrorcode();
					if(errorcode==null) {
						errorcode="";
					}
					//当前数据库没有这个值
					String receivetime = httpV2Statusbox.getReceivetime();
					//时间格式化
					Timestamp receiveTimestamp  = DateUtils.stringToTimestamp(receivetime);
					String errorCode = httpV2Statusbox.getErrorcode();
					String sendStat = "";
					String mid = submitTaskId+mobile;
					int sendStatus=4;
					if("10".equals(status)) {
						//成功发送
						sendStatus=1;
						sendStat = "0";
					} else {
						//发送失败
						if(!errorcode.startsWith("ACCEPTD")) {
							sendStatus=2;
							sendStat = errorCode;
						}
					}
					//更新短信状态
					Object[] smsArray= {sendStat,receiveTimestamp,sendStatus,mid};
					argsList.add(smsArray);
				}
			}
		}
		if(argsList.size()>0) {
			//更新短信状态
			jdbcTemplate.batchUpdate(sbUpdate.toString(), argsList);
		}
	}
	/**
	 * 更新短信网关响应状态
	 * @param reportResponse
	 */
	public void updateSmsReportHttpV3(HttpV3StatusResponse reportResponse){
		//更新短信表
		StringBuilder sbUpdate= new StringBuilder("");
		sbUpdate.append(" update  tb_sms set sendStat=?,statTime=?,sendStatus=? where mid=? ");
		List<Object[]> argsList= new ArrayList<Object[]>();
		
		if(reportResponse!=null) {
			//返回值
			List<HttpV3Statusbox> statusboxList = reportResponse.getStatusboxList();
			//返回值错误代码,只有一个暂时使用list，不确定多个
			List<HttpV3Errorstatus> errorstatusList = reportResponse.getErrorstatusList();
			//错误打印
			if(errorstatusList!=null) {
				for (HttpV3Errorstatus httpV3Errorstatus : errorstatusList) {
					String error = httpV3Errorstatus.getError();
					String remark = httpV3Errorstatus.getRemark();
					//只在日志打印出来
					log.error("httpV3短信状态查询错误,error = " + error + ",remark = " + remark);
				}
			}
			if(statusboxList!=null) {
				//返回状态列表
				for (HttpV3Statusbox httpV3Statusbox : statusboxList) {
					String mobile = httpV3Statusbox.getMobile();
					String submitTaskId = httpV3Statusbox.getTaskid();
					String status = httpV3Statusbox.getStatus();
					String errorcode = httpV3Statusbox.getErrorcode();
					if(errorcode==null) {
						errorcode="";
					}
					//当前数据库没有这个值
					String receivetime = httpV3Statusbox.getReceivetime();
					//时间格式化
					Timestamp receiveTimestamp  = DateUtils.stringToTimestamp(receivetime);
					String errorCode = httpV3Statusbox.getErrorcode();
					String sendStat = "";
					String mid = submitTaskId+mobile;
					int sendStatus=4;
					if("10".equals(status)) {
						//成功发送
						sendStatus=1;
						sendStat = "0";
					} else {
						//发送失败
						if(!errorcode.startsWith("ACCEPTD")) {
							sendStatus=2;
							sendStat = errorCode;
						}
					}
					//更新短信状态
					Object[] smsArray= {sendStat,receiveTimestamp,sendStatus,mid};
					argsList.add(smsArray);
				}
			}
		}
		if(argsList.size()>0) {
			//更新短信状态
			jdbcTemplate.batchUpdate(sbUpdate.toString(), argsList);
		}
	}
	/**
	 * 分页查
	 * @param keyword 手机号码、短信内容
	 * @param loginUser
	 * @param pageable
	 * @return
	 */
	public Page getSmsLogList(SmsLogParam smsLogParam, Pageable pageable) {
		List<Integer> idList = new ArrayList<>();
		if(smsLogParam.getTaskId()!=null&&smsLogParam.getTaskId()!=0) {
			idList.add(smsLogParam.getTaskId());
		}else {
			StringBuffer sql = new StringBuffer();
			sql.append("select id from tb_sms_task where sendStatus>0 ");
			if(!getLoginUser().getRoleCode().equals("ADMIN")) {
				sql.append("and createUserId="+getLoginUser().getId()+" ");
			}
			idList = jdbcTemplate.queryForList(sql.toString(),Integer.class);
		}
		
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" FROM tb_sms a where sendStatus>0 ");
		if(!getLoginUser().getRoleCode().equals("ADMIN")) {
			fromWhereSql.append(" and createUserId='"+getLoginUser().getId()+"' ");
		}
		if(StringUtils.isNotEmpty(smsLogParam.getKeyword())) {
			fromWhereSql.append("  and (phone like '%"+smsLogParam.getKeyword()+"%' or content like '%"+smsLogParam.getKeyword()+"%') ");
		}
		if(StringUtils.isNotEmpty(smsLogParam.getBeginTime())) {
			fromWhereSql.append(" and sendTime>='"+smsLogParam.getBeginTime()+" 00:00:00' ");
		}
		if(StringUtils.isNotEmpty(smsLogParam.getEndTime())) {
			fromWhereSql.append(" and sendTime<='"+smsLogParam.getEndTime()+" 23:59:59' ");
		}
		if(smsLogParam.getTaskId()!=null&&smsLogParam.getTaskId()!=0) {
			fromWhereSql.append(" and taskId='"+smsLogParam.getTaskId()+"' ");
		}else {
			fromWhereSql.append(" and (1=0 ");
			List<Integer> l = new ArrayList<>();
			for(Integer id:idList) {
				l.add(id);
				if(l.size()>=1000) {
					fromWhereSql.append(" or taskId in ("+StringUtils.join(l,",")+")");
					l = new ArrayList<>();
				}
			}
			if(l.size()>0) fromWhereSql.append(" or taskId in ("+StringUtils.join(l,",")+")");
			fromWhereSql.append(" ) ");
		}
		if(smsLogParam.getCreateUserId()!=null&&smsLogParam.getCreateUserId()!=0) {
			fromWhereSql.append(" and createUserId='"+smsLogParam.getCreateUserId()+"' ");
		}
		if(smsLogParam.getSendStatus()!=null && smsLogParam.getSendStatus()!=-1) {
			fromWhereSql.append(" and sendStatus='"+smsLogParam.getSendStatus()+"' ");
		}
		Page<SmsLogParam> page = super.findPageBySql(SmsLogParam.class, "select * ", fromWhereSql.toString(), " order by sendTime desc", null, pageable);
		//处理归属地
		List<SmsLogParam> smsLogParamList = page.getContent();
		List<AccountInfo> accountList = accountService.findAllAccountInfoList();
		Map<Integer, String> accountMap = accountList.stream().collect(Collectors.toMap(AccountInfo::getId, AccountInfo::getTitle));
		if(smsLogParamList!=null) {
			RMap<String,PhoneArea> map =  redissonClient.getMap("phoneAreaMap");
			for (SmsLogParam smsLog : smsLogParamList) {
				String phone07="";
				String phone = smsLog.getPhone();
				if(phone.length()>7) {
					phone07=phone.substring(0, 7);
					PhoneArea phoneArea= map.get(phone07);
					if(phoneArea!=null) {
						smsLog.setIsp(phoneArea.getIsp());
					}
				}
				String channelName = accountMap.get(smsLog.getAccountId());
				if (channelName == null) {
					channelName = "";
				}
				smsLog.setChannelName(channelName);
				if(smsLog.getSendStatus().intValue()==5) smsLog.setSendStatus(1);
			}
		}
		return page;
	}
	
	/**
	 * 分页查
	 * @param keyword 手机号码、短信内容
	 * @param loginUser
	 * @param pageable
	 * @return
	 */
	public Page getTaskDetailList(SmsLogParam smsLogParam, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" FROM tb_sms a where 1=1 ");
		if(!getLoginUser().getRoleCode().equals("ADMIN")) {
			fromWhereSql.append(" and createUserId='"+getLoginUser().getId()+"' ");
		}
		if(StringUtils.isNotEmpty(smsLogParam.getKeyword())) {
			fromWhereSql.append("  and (phone like '%"+smsLogParam.getKeyword()+"%' or content like '%"+smsLogParam.getKeyword()+"%') ");
		}
		if(StringUtils.isNotEmpty(smsLogParam.getBeginTime())) {
			fromWhereSql.append(" and sendTime>='"+smsLogParam.getBeginTime()+"' ");
		}
		if(StringUtils.isNotEmpty(smsLogParam.getEndTime())) {
			fromWhereSql.append(" and sendTime<='"+smsLogParam.getEndTime()+" 23:59:59' ");
		}
		if(smsLogParam.getTaskId()!=null) {
			fromWhereSql.append(" and taskId='"+smsLogParam.getTaskId()+"' ");
		}
		if(smsLogParam.getSendStatus()!=null && smsLogParam.getSendStatus()!=-1) {
			if(smsLogParam.getTaskId()!=null) {
				SmsTask task = smsTaskDao.findOne(smsLogParam.getTaskId());
				if(task.getSendStatus()<=0 && smsLogParam.getSendStatus().intValue()==0) {
					fromWhereSql.append(" and sendStatus in (0,5) ");
					
				} else if(task.getSendStatus()>0 && smsLogParam.getSendStatus().intValue()==1) {
					fromWhereSql.append(" and sendStatus in (1,5) ");
				} else {
					fromWhereSql.append(" and sendStatus='"+smsLogParam.getSendStatus()+"' ");
				}
			}else {
				fromWhereSql.append(" and sendStatus='"+smsLogParam.getSendStatus()+"' ");
			}
		}
		Page<SendingDetailParam> page = super.findPageBySql(SendingDetailParam.class, "select * ", fromWhereSql.toString(), " order by sendTime desc", null, pageable);
		if(smsLogParam.getTaskId()!=null) {
			SmsTask task = smsTaskDao.findOne(smsLogParam.getTaskId());
			for(SendingDetailParam info:page.getContent()) {
				if(task.getSendStatus()<=0) {
					info.setMid("");
					info.setStatTime(null);
					info.setSendStatus(0);
				}else if(info.getSendStatus().intValue()==5) {
					info.setSendStatus(1);
				}
			}
		}
		return page;
	}
	
	/**
	 * 分页查
	 * @param keyword 手机号码、短信内容
	 * @param loginUser
	 * @param pageable
	 * @return
	 */
	public Page getSmsDateList(String beginTime,String endTime, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" from ( ");
		fromWhereSql.append("select DATE_FORMAT(sendTime,'%Y-%m-%d') day,sum(totalCount) totalCount,sum(successCount) successCount,sum(failCount) failCount,sum(sendingCount) sendingCount,sum(unknowCount) unknowCount FROM tb_sms_task a where sendStatus>0 ");
		if(!getLoginUser().getRoleCode().equals("ADMIN")) {
			fromWhereSql.append(" and createUserId='"+getLoginUser().getId()+"' ");
		}
		if(StringUtils.isNotEmpty(beginTime)) {
			fromWhereSql.append(" and sendTime>='"+beginTime+"' ");
		}
		if(StringUtils.isNotEmpty(endTime)) {
			fromWhereSql.append(" and sendTime<='"+endTime+" 23:59:59' ");
		}
		fromWhereSql.append(" group by DATE_FORMAT(sendTime,'%Y-%m-%d') ) xx");
		Page<SmsDateParam> page = super.findPageBySql(SmsDateParam.class, "select * ", fromWhereSql.toString(), " order by day desc", null, pageable);
		return page;
	}
	
	/**
	 * 分页查
	 * @param keyword 手机号码、短信内容
	 * @param loginUser
	 * @param pageable
	 * @return
	 */
	public Page getSmsAccountList(String beginTime,String endTime,String account, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" from ( ");
		fromWhereSql.append("select DATE_FORMAT(sendTime,'%Y-%m-%d') day,sum(totalCount) totalCount,sum(successCount) successCount,sum(failCount) failCount,sum(sendingCount) sendingCount,sum(unknowCount) unknowCount FROM tb_sms_task a where sendStatus>0 ");
		if(!getLoginUser().getRoleCode().equals("ADMIN")) {
			fromWhereSql.append(" and createUserId='"+getLoginUser().getId()+"' ");
		}
		if(StringUtils.isNotEmpty(account) && !"all".equals(account)) {
			fromWhereSql.append(" and channelId='"+account+"' ");
		}
		if(StringUtils.isNotEmpty(beginTime)) {
			fromWhereSql.append(" and sendTime>='"+beginTime+"' ");
		}
		if(StringUtils.isNotEmpty(endTime)) {
			fromWhereSql.append(" and sendTime<='"+endTime+" 23:59:59' ");
		}
		fromWhereSql.append(" group by DATE_FORMAT(sendTime,'%Y-%m-%d') ) xx");
		Page<SmsDateParam> page = super.findPageBySql(SmsDateParam.class, "select * ", fromWhereSql.toString(), " order by day desc", null, pageable);
		return page;
	}
	
	/**
     * 批量导入
     * @param list
     */
    public void batchSave(List<Sms> list) {
        if(list!=null && list.size()>0){
            final List<Sms> tempList = list;
            StringBuffer sb = new StringBuffer();
            sb.append("insert into tb_sms(taskId, batchId, content, sendResult, sendStatus, ");
    		sb.append("sendTime,phone,account,password,extno, " );
    		sb.append("mid,memo,createTime,createUserId,updateTime,");
    		sb.append("updateUserId) ");
			
			sb.append(" values(?,?,?,?,?");
			sb.append(",?,?,?,?,?");
			sb.append(",?,?,?,?,?");
			sb.append(",?)");
            jdbcTemplate.batchUpdate(sb.toString(),
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i)
                                throws SQLException {
                        	Sms info = tempList.get(i);
                            ps.setInt(1, info.getTaskId()==null?0:info.getTaskId());
                            ps.setString(2, info.getBatchId()==null?"":info.getBatchId().trim());
                            ps.setString(3, info.getContent()==null?"":info.getContent().trim());
                            ps.setInt(4, info.getSendResult()==null?0:info.getSendResult());
                            ps.setInt(5, info.getSendStatus()==null?0:info.getSendStatus());
                            
                            ps.setTimestamp(6, info.getSendTime()==null?null:info.getSendTime());
                            ps.setString(7, info.getPhone()==null?"":info.getPhone().trim());
                            ps.setString(8, info.getAccount()==null?"":info.getAccount().trim());
                            ps.setString(9, info.getPassword()==null?"":info.getPassword().trim());
                            ps.setString(10, info.getExtno()==null?"":info.getExtno().trim());
                            
                            ps.setString(11, info.getMid()==null?"":info.getMid().trim());
                            ps.setString(12, info.getMemo()==null?"":info.getMemo().trim());
                            ps.setTimestamp(13, info.getCreateTime()==null?null:info.getCreateTime());
                            ps.setInt(14, info.getCreateUserId()==null?0:info.getCreateUserId());
                            ps.setTimestamp(15, info.getUpdateTime()==null?null:info.getUpdateTime());

                            ps.setInt(16, info.getUpdateUserId()==null?0:info.getUpdateUserId());
                        }
                        public int getBatchSize() {
                            return tempList.size();
                        }
                    });

        }
    }
    
    /**
     * 批量更新数据库数据，包括删除重复数据、删除无效数据
     * @param list
     */
    public void batchUpdate(List<Sms> list) {
    	List<String> temp = new ArrayList<String>();
    	for(Sms sms:list) {
    		temp.add("delete from tb_sms where id='"+sms.getId()+"' ");
    		if(temp.size()>=500) {
    			jdbcTemplate.batchUpdate(temp.toArray(new String[temp.size()]));
    			temp = new ArrayList<String>();
    		}
    	}
    	if(temp.size()>0) {
			jdbcTemplate.batchUpdate(temp.toArray(new String[temp.size()]));
    	}
    }
    /**
     * 今日发送
     * @return
     */
    public Integer getDayCount() {
    	int result = 0;
    	String sql="select sum(successCount) from tb_sms_task where DATE_FORMAT(now(),'%Y-%m-%d') = DATE_FORMAT(sendTime,'%Y-%m-%d') ";
    	if(!getLoginUser().getRoleCode().equals("ADMIN")) {
    		sql=sql+" and createUserId='"+getLoginUser().getId()+"' ";
		}
		Integer dayCount = jdbcTemplate.queryForObject(sql, Integer.class);
		if (dayCount != null) {
			result = dayCount.intValue();
		}
		return result;
    }
     /**
      * 本月发送
      * @return
      */
    public Integer getMonthCount() {
    	int result = 0;
    	String sql="select sum(successCount) from tb_sms_task where DATE_FORMAT(now(),'%Y-%m') = DATE_FORMAT(sendTime,'%Y-%m') ";
    	if(!getLoginUser().getRoleCode().equals("ADMIN")) {
    		sql=sql+" and createUserId='"+getLoginUser().getId()+"' ";
		}
    	Integer monthCount = jdbcTemplate.queryForObject(sql, Integer.class);
		if (monthCount != null) {
			result = monthCount.intValue();
		}
		return result;
    }
    /**
     * 用户数
     * @return
     */
    public Integer getSurplusCountCount() {
		PlatManager pm = platManagerService.findByLoginName(getLoginUser().getLoginName());
		Wallet wallet = walletService.findIfExist();
		BigDecimal count = wallet.getMoney().divide(pm.getPrice(), 3, BigDecimal.ROUND_DOWN);
    	return count.intValue();
    }
    
    
    

	/**
	 * 得到短信发送条数
	 * @param accountInfo 账户
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 */
	public int accountSmsNum(AccountInfo accountInfo,Timestamp startTime,Timestamp endTime){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//查询短信条数
		StringBuilder sb= new StringBuilder("");
		sb.append(" select ");
		//超过70个字符，按照67个短信计费
		sb.append(" sum(case when length(content)  >70  then  ceiling(length(content)/67) else 1 end ) as num ");
		sb.append(" from tb_sms ");
		sb.append(" where account= '"+ accountInfo.getAccount() +"'");
		if(startTime!=null) {
			sb.append(" create >= '"+df.format(startTime)+"'");
		}
		if(endTime!=null) {
			sb.append(" create <= '"+df.format(endTime)+"'");
		}
		
		Integer  returnSum = jdbcTemplate.query(sb.toString(), new ResultSetExtractor<Integer>(){
			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				int sum = 0;
				if(rs.next()) {
					sum = rs.getInt("sum");
				}
				return sum;
			}
		});
		return returnSum;
	}
	/**
	 * 统计总数
	 * @param dateList
	 * @return
	 */
	public List<String> getCountList(List<String> dateList){
		String sql=" SELECT sum( successCount + failCount ) c,DATE_FORMAT(sendTime,'%Y-%m-%d') sendTime FROM tb_sms_task WHERE DATE_SUB( CURDATE(), INTERVAL 10 DAY ) <= sendTime  ";
		if(!getLoginUser().getRoleCode().equals("ADMIN")) {
    		sql=sql+" and createUserId='"+getLoginUser().getId()+"' ";
		}
		sql=sql+" group by DATE_FORMAT(sendTime,'%Y-%m-%d') ";
		List<Map<String, Object>> listMap = jdbcTemplate.queryForList(sql);
		
		List<String> result = new ArrayList<>();
		for(String date:dateList) {
			String value = "0";
			for(Map<String, Object> map:listMap) {
				String day = map.get("sendTime")==null?"":map.get("sendTime").toString();
				String c = map.get("c")==null?"0":((BigDecimal)map.get("c")).intValue()+"";
				if(day.equals(date)) value=c;
			}
			result.add(value);
		}
		return result;
	}
	/**
	 * 统计百分比
	 * @param dateList
	 * @return
	 */
	public List<String> getPercentList(List<String> dateList){
		String sql="select sum(successCount+failCount) c,sum(successCount) c2,DATE_FORMAT(sendTime,'%Y-%m-%d') sendTime from tb_sms_task where DATE_SUB(CURDATE(), INTERVAL 10 DAY) <=  sendTime ";
		if(!getLoginUser().getRoleCode().equals("ADMIN")) {
			sql=sql+" and createUserId='"+getLoginUser().getId()+"' ";
		}
		sql=sql+" group by DATE_FORMAT(sendTime,'%Y-%m-%d') ";
		List<Map<String, Object>> listMap = jdbcTemplate.queryForList(sql);
		
		List<String> result = new ArrayList<>();
		for(String date:dateList) {
			String value = "0";
			for(Map<String, Object> map:listMap) {
				String day = map.get("sendTime")==null?"":map.get("sendTime").toString();
				Integer c = map.get("c")==null?0:((BigDecimal)map.get("c")).intValue();
				Integer c2 = map.get("c2")==null?0:((BigDecimal)map.get("c2")).intValue();
				if(day.equals(date)) {
					if(c>0 && c2>0) {
						DecimalFormat df= new DecimalFormat("######0.00");   
						value = df.format(c2*100/c)+"";
					}
				}
			}
			result.add(value);
		}
		return result;
	}
	
	public List<String> getAccounts(){
		if(!getLoginUser().getRoleCode().equals("ADMIN")) {
			return new ArrayList<>();
		}
		List<AccountInfo> list = jdbcTemplate.query("select * from tb_account where isDelete=0 and accountStatus=1", new BeanPropertyRowMapper<AccountInfo>(AccountInfo.class));
		return list.stream().map(AccountInfo::getAccount).collect(Collectors.toList());
	}
	
	/**
	 * 更新短信账号信息
	 * @param idList
	 * @param accountInfo
	 */
	public void updateSmsAccount(List<Integer> idList,AccountInfo accountInfo){
		StringBuilder sbUpdate= new StringBuilder("");
		sbUpdate.append("update tb_sms set account=?,password=?,extno=?,accountId=? where id=? ");
		List<Object[]> argsList= new ArrayList<Object[]>();
		if(idList!=null) {
			for (Integer mobileId : idList) {
				Object[] mobileArray= {accountInfo.getAccount(),accountInfo.getPassword(),accountInfo.getExtno(),accountInfo.getId(),mobileId};
				argsList.add(mobileArray);
			}
		}
		jdbcTemplate.batchUpdate(sbUpdate.toString(), argsList);
	}
	/**
	 * 更新短信账号信息smpp
	 * @param smsId
	 * @param accountInfo
	 */
	public void updateSmsAccountForSmpp(int smsId,AccountInfo accountInfo){
		StringBuilder sbUpdate= new StringBuilder("");
		sbUpdate.append("update tb_sms set account=?,password=?,accountId=? where id=? ");
		Object[] mobileArray= {accountInfo.getAccount(),accountInfo.getPassword(),accountInfo.getId(),smsId};
		jdbcTemplate.update(sbUpdate.toString(), mobileArray);
	}
	
	/**
	 * 更新smpp返回值
	 * @param smsId
	 * @param response
	 * @throws Exception 
	 */
	public void updateSmsForSmpp(int platManagerId,String accountId,Integer smsId,SubmitSmResp response,Boolean isSuccess) throws Exception {
		if(smsId!=null && smsId!=0) {
			if(isSuccess) {
				//提交成功
				jdbcTemplate.update("update tb_sms set mid='"+response.getMessageId()+"',statTime=now(),sendStat='已提交网关' where id="+smsId);
			}else {
				//提交失败
				jdbcTemplate.update("update tb_sms set mid='"+response.getMessageId()+"',sendStatus=2,sendStat='"+response.getCommandStatus()+"',memo='"+response.getResultMessage()+"',statTime=now() where id="+smsId);
				//失败返还金额,一般不会发生
				walletService.updateWalletMoneyByUserIdSmsNum(platManagerId,1);
			}
		}
	}
	
	/**
	 * 更新smpp返回值
	 * @param smsId
	 * @param response
	 * @throws Exception 
	 */
	public void updateSmsForSmppInit(int platManagerId,Integer smsId) throws Exception {
		if (smsId != null && smsId != 0) {
			int i = jdbcTemplate.update("update tb_sms set mid='',sendStatus=0,sendStat='',memo='' where id=" + smsId);
			// 提交失败，重置为待发送状态
			if (i > 0) {
				jdbcTemplate.update("update tb_sms_task set sendStatus=0  where id in (select taskid from tb_sms where id =" + smsId + ")");
			}
		}
	}
	
	/**
	 * 更新smpp返回值
	 * @param smsId
	 * @param response
	 */
	public void updateSmsForSmppResponse(String messageId,String sendStat) {
		if(StringUtils.isNotEmpty(messageId)) {
			if(StringUtils.isNotEmpty(sendStat) && sendStat.equals("DELIVRD")) {
				jdbcTemplate.update("update tb_sms set sendStatus=1,statTime=now(),sendStat=0 where mid='"+messageId+"'");
			}else {
				jdbcTemplate.update("update tb_sms set sendStatus=2,sendStat='"+sendStat+"',statTime=now() where mid='"+messageId+"'");
			}
		}
	}

	/**
	 * 分页查
	 * @param keyword 手机号码、短信内容
	 * @param loginUser
	 * @param pageable
	 * @return
	 */
	public Page getSmsTaskList(SmsLogParam smsLogParam, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" FROM tb_sms_task b where b.sendStatus>0 ");
		if(!getLoginUser().getRoleCode().equalsIgnoreCase("ADMIN")) {
			fromWhereSql.append(" and b.createUserId='"+getLoginUser().getId()+"' ");
		}
		if(StringUtils.isNotEmpty(smsLogParam.getKeyword())) {
			fromWhereSql.append("  and (phone like '%"+smsLogParam.getKeyword()+"%' or content like '%"+smsLogParam.getKeyword()+"%') ");
		}
		if(StringUtils.isNotEmpty(smsLogParam.getBeginTime())) {
			fromWhereSql.append(" and sendTime>='"+smsLogParam.getBeginTime()+" 00:00:00' ");
		}
		if(StringUtils.isNotEmpty(smsLogParam.getEndTime())) {
			fromWhereSql.append(" and sendTime<='"+smsLogParam.getEndTime()+" 23:59:59' ");
		}
		if(smsLogParam.getCreateUserId()!=null&&smsLogParam.getCreateUserId()!=0) {
			fromWhereSql.append(" and createUserId='"+smsLogParam.getCreateUserId()+"' ");
		}
		Page<SmsDateParam> page = super.findPageBySql(SmsDateParam.class, "select b.*,b.id taskId ", fromWhereSql.toString(), " order by b.createTime desc", null, pageable);
		List<AccountInfo> accountList = accountService.findAllAccountInfoList();
		Map<Integer, String> accountMap = accountList.stream().collect(Collectors.toMap(AccountInfo::getId, AccountInfo::getTitle));
		List<SmsDateParam> list = page.getContent();
		if(list!=null) {
			for (SmsDateParam info : list) {
				String channelName = accountMap.get(info.getChannelId());
				if (channelName == null) {
					channelName = "";
				}
				info.setChannelName(channelName);
			}
		}
		return page;
	}
	
	public List<SmsTask> findValidTask(){
		StringBuffer sql = new StringBuffer();
		sql.append("select * from tb_sms_task where sendStatus>0 ");
		if(!getLoginUser().getRoleCode().equals("ADMIN")) {
			sql.append("and createUserId="+getLoginUser().getId()+" ");
		}
		sql.append(" order by createTime desc");
		return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<SmsTask>(SmsTask.class));
	}
	
	/**
	 * 每周一7点执行一次清楚数据
	 */
	@Scheduled(cron = "0 0 7 ? * MON")
	public void cleanLog(){
		if(!systemConfig.getIsTest()) {
			if(systemConfig.getIsAdmin()) return;
		}
		log.info("每周一开始清理数据");
		try {
			logsService.cleanAll();
		} catch (Exception e) {
			log.info("每周一清理数据失败，错误原因："+e.getMessage());
		}
	}
}
