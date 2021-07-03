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
import star.sms._frame.utils.StringUtils;
import star.sms._frame.utils.UUIDUtils;
import star.sms.account.domain.AccountInfo;
import star.sms.account.service.AccountService;
import star.sms.config.SystemConfig;
import star.sms.phonearea.domain.PhoneArea;
import star.sms.platmanager.domain.PlatManager;
import star.sms.platmanager.service.PlatManagerService;
import star.sms.sms.dao.SmsDao;
import star.sms.sms.domain.Sms;
import star.sms.sms.domain.SmsBatch;
import star.sms.sms.vo.SmsDateParam;
import star.sms.sms.vo.SmsLogParam;
import star.sms.smsmq.domain.http.ReportRequest;
import star.sms.smsmq.domain.http.ReportResponse;
import star.sms.smsmq.domain.http.ReportResponseMobile;
import star.sms.smsmq.domain.http.SendRequest;
import star.sms.smsmq.domain.http.SendResponse;
import star.sms.smsmq.domain.http.SendResponseMobile;
import star.sms.smsmq.domain.smpp.SendRequestSmpp;
import star.sms.smsmq.smstask.ReportTask;
import star.sms.smsmq.smstask.SendTask;
import star.sms.wallet.domain.Wallet;
import star.sms.wallet.domain.WalletLog;
import star.sms.wallet.service.WalletLogService;
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
    private EntityManager entityManager;
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private WalletLogService walletLogService;
    
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
    
	@Override
	protected BaseRepository<Sms> getBaseRepository() {
		return smsDao;
	}
	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}
	/**
	 * http发送短信开始
	 */
	public void sendStart(SendRequest sendRequest,String batchId,AccountInfo accountInfo) {
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
		// 更新账号，统计使用
		updateSmsAccount(sendRequest.getMobileIdList(), accountInfo);
	}

	/**
	 * http处理返回结果
	 */
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
	 * 定时读取未发送短信到消息队列
	 * @return
	 */
	@Scheduled(cron = "0/2 * * * * ?")
	public void readNotSentSmsToMqHttp(){
		if(systemConfig.getIsAdmin()) return;
		String serialNumber=UUIDUtils.random();
		List<Sms> smsList =new ArrayList<Sms>();
		Map<Integer, List<Sms>> smsMap = new HashMap<Integer, List<Sms>>();
		//选择http账号
		List<AccountInfo> accountHttpList = new ArrayList<AccountInfo>();
		Map<String, AccountInfo> accountMap = accountService.getAccountMap();
		if (accountMap != null) {
			for (Map.Entry<String, AccountInfo> entry : accountMap.entrySet()) {
				AccountInfo account = entry.getValue();
				if(account.getChannelType()==1) {
					accountHttpList.add(account);
				}
			}
		}
		if(accountHttpList.size() == 0) {
			log.info(" 开始读取短信 serialNumber: "+serialNumber+" , 未找到可用http账号！ ");
			return;
		}
		lock.lock();
		try {
			log.info(" 开始读取短信 serialNumber: "+serialNumber+" , 读取内容: " + smsList);
			//读取待发送
			StringBuilder sb= new StringBuilder("");
			sb.append(" select a.*,b.contentType,b.content as taskContent from tb_sms a left join tb_sms_task b on a.taskId=b.id where b.channelType=1 and a.sendStatus=0 and b.sendStatus in (0,4) and b.sendTime<=now() order by b.priority desc limit 3000 ");
			smsList =  jdbcTemplate.query(sb.toString(), new BeanPropertyRowMapper<Sms>(Sms.class));
			if(smsList.size()==0) {
				return;
			}
			//按任务分组
			if(smsList!=null) {
				smsMap = smsList.stream().filter(sms -> sms.getTaskId()!= null).collect(Collectors.groupingBy(Sms::getTaskId));
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
		if(smsMap!=null&&smsMap.size()>0) {
			for(Map.Entry<Integer, List<Sms>>  entry : smsMap.entrySet()) {
				//按分组分发短信
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
					int i=0;
					for (Sms sms : smsGroupList) {
						contentType=sms.getContentType();
						if(i==0) {
							taskId = sms.getTaskId();
							taskContent=sms.getTaskContent();
							createUserId=sms.getCreateUserId();
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

					log.info(" 开始发送短信 serialNumber: "+serialNumber);

					sendTask.hanlderHttp(sendRequest);
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
		if(systemConfig.getIsAdmin()) return;
		String serialNumber=UUIDUtils.random();
		List<Sms> smsList =new ArrayList<Sms>();
		Map<Integer, List<Sms>> smsMap = new HashMap<Integer, List<Sms>>();
		//选择http账号
		List<AccountInfo> accountHttpList = new ArrayList<AccountInfo>();
		Map<String, AccountInfo> accountMap = accountService.getAccountMap();
		if (accountMap != null) {
			for (Map.Entry<String, AccountInfo> entry : accountMap.entrySet()) {
				AccountInfo account = entry.getValue();
				if(account.getChannelType()==1) {
					accountHttpList.add(account);
				}
			}
		}
		if(accountHttpList.size() == 0) {
			log.info(" 开始读取短信 serialNumber: "+serialNumber+" , 未找到可用smpp账号！ ");
			return;
		}
		lock.lock();
		try {
			log.info(" 开始读取短信 serialNumber: "+serialNumber+" , 读取内容: " + smsList);
			//读取待发送
			StringBuilder sb= new StringBuilder("");
			sb.append(" select a.*,b.contentType,b.content as taskContent from tb_sms a left join tb_sms_task b on a.taskId=b.id where b.channelType=2 and a.sendStatus=0 and b.sendStatus in (0,4) and b.sendTime<=now() order by b.priority desc limit 3000 ");
			smsList =  jdbcTemplate.query(sb.toString(), new BeanPropertyRowMapper<Sms>(Sms.class));
			if(smsList.size()==0) {
				return;
			}
			//按任务分组
			if(smsList!=null) {
				smsMap = smsList.stream().filter(sms -> sms.getTaskId()!= null).collect(Collectors.groupingBy(Sms::getTaskId));
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
		if(smsMap!=null&&smsMap.size()>0) {
			for(Map.Entry<Integer, List<Sms>>  entry : smsMap.entrySet()) {
				//按分组分发短信
				List<Sms> smsGroupList = entry.getValue();
				if(smsGroupList!=null && smsGroupList.size()>0) {
					for (Sms sms : smsGroupList) {
						if(sms!=null) {
							SendRequestSmpp sendRequestSmpp =  new SendRequestSmpp();
							sendRequestSmpp.setSmsId(sms.getId());
							sendRequestSmpp.setMobile(sms.getPhone());
							sendRequestSmpp.setContent(sms.getContent());
							sendTask.hanlderSmpp(sendRequestSmpp);
						}
					}
				}
			}
		}
	}
	/**
	 * 得到用户消费记录
	 * @param midSet
	 * @return
	 */
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
		sb.append(" select createUserId as userId,count(*) as count from tb_sms where mid in ("+mids+") group by userId ");
		
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
	 * 定时查询状态报告，更新短信表，每2秒查询2000条
	 */
	@Scheduled(cron = "0/5 * * * * ?")
	@Transactional
	public void findSmsReportToMq(){
		if(systemConfig.getIsAdmin()) return;
		log.info(" 开始查询报告 ");
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
					reportRequest.setSize(2000);
					//请求放入mq
					reportTask.hanlderHttp(reportRequest);
				}
			}
		}
	}
	/**
	 * 更新短信状态
	 * @param reportResponse
	 */
	public void updateSmsReport(ReportResponse reportResponse){
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
		Map<Integer,BigDecimal> smsUserMoneyMap=null;
		if(midSet.size()>0) {
			//得到用户消费（用户id，费用）
			smsUserMoneyMap=findUserIdSmsNum(midSet);
		}
		lock.lock();
		try {
			if(argsList.size()>0) {
				//更新短信状态
				jdbcTemplate.batchUpdate(sbUpdate.toString(), argsList);
			}
			if(midSet.size()>0) {
				//需要计费短信
				log.info("计费短信标识: " + midSet);
				//更新
				if(smsUserMoneyMap!=null) {
					//插入钱包日志,首先记录日志
					List<WalletLog> walletLogList = new ArrayList<WalletLog>();
					for (Map.Entry<Integer, BigDecimal> entrySet : smsUserMoneyMap.entrySet()) {
						WalletLog walletLog = new WalletLog();
						walletLog.setUserId(entrySet.getKey());
						walletLog.setMoney(entrySet.getValue());
						walletLogList.add(walletLog);
					}
					walletLogService.insertWalletLog(walletLogList);
					//更新钱包
					List<Wallet> walletList = new ArrayList<Wallet>();
					for (Map.Entry<Integer, BigDecimal> entrySet : smsUserMoneyMap.entrySet()) {
						Wallet wallet = new Wallet();
						wallet.setUserId(entrySet.getKey());
						wallet.setMoney(entrySet.getValue());
						walletList.add(wallet);
					}
					walletService.updateWalletMoney(walletList);
				}
			}
		} finally {
            lock.unlock();
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
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" FROM tb_sms a where  sendStatus>0 ");
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
		if(smsLogParam.getTaskId()!=null) {
			fromWhereSql.append(" and taskId='"+smsLogParam.getTaskId()+"' ");
		}
		if(smsLogParam.getSendStatus()!=null && smsLogParam.getSendStatus()!=-1) {
			fromWhereSql.append(" and sendStatus='"+smsLogParam.getSendStatus()+"' ");
		}
		Page<SmsLogParam> page = super.findPageBySql(SmsLogParam.class, "select * ", fromWhereSql.toString(), " order by sendTime desc", null, pageable);
		//处理归属地
		List<SmsLogParam> smsLogParamList = page.getContent();
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
		fromWhereSql.append(" FROM tb_sms a where createUserId='"+getLoginUser().getId()+"' ");
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
			fromWhereSql.append(" and sendStatus='"+smsLogParam.getSendStatus()+"' ");
		}
		Page<Sms> page = super.findPageBySql(Sms.class, "select * ", fromWhereSql.toString(), " order by sendTime desc", null, pageable);
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
		fromWhereSql.append("select DATE_FORMAT(sendTime,'%Y-%m-%d') day,count(1) totalCount,sum(case when sendStatus=1 then 1 else 0 end) successCount,sum(case when sendStatus=2 then 1 else 0 end) failCount,sum(case when sendStatus=4 then 1 else 0 end) sendingCount,sum(case when sendStatus=3 then 1 else 0 end) unknowCount FROM tb_sms a where  sendStatus>0 ");
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
		fromWhereSql.append("select DATE_FORMAT(sendTime,'%Y-%m-%d') day,count(1) totalCount,sum(case when sendStatus=1 then 1 else 0 end) successCount,sum(case when sendStatus=2 then 1 else 0 end) failCount,sum(case when sendStatus=4 then 1 else 0 end) sendingCount,sum(case when sendStatus=3 then 1 else 0 end) unknowCount FROM tb_sms a where  sendStatus>0 ");
		if(!getLoginUser().getRoleCode().equals("ADMIN")) {
			fromWhereSql.append(" and createUserId='"+getLoginUser().getId()+"' ");
		}
		if(StringUtils.isNotEmpty(account) && !"all".equals(account)) {
			fromWhereSql.append(" and account='"+account+"' ");
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
    
    public Integer getDayCount() {
    	String sql="select count(1) from tb_sms where to_days(sendTime) = to_days(now()) and sendStatus=1 ";
    	if(!getLoginUser().getRoleCode().equals("ADMIN")) {
    		sql=sql+" and createUserId='"+getLoginUser().getId()+"' ";
		}
    	return jdbcTemplate.queryForObject(sql, Integer.class);
    }
    
    public Integer getMonthCount() {
    	String sql="select count(1) from tb_sms where  DATE_FORMAT(sendTime, '%Y-%m') = DATE_FORMAT(now(), '%Y-%m') and sendStatus=1 ";
    	if(!getLoginUser().getRoleCode().equals("ADMIN")) {
    		sql=sql+" and createUserId='"+getLoginUser().getId()+"' ";
		}
    	return jdbcTemplate.queryForObject(sql, Integer.class);
    }
    
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
	
	public List<String> getCountList(List<String> dateList){
		String sql="select count(1) c,DATE_FORMAT(sendTime, '%Y-%m-%d') sendTime from tb_sms where sendStatus in (1,2) and DATE_SUB(CURDATE(), INTERVAL 10 DAY) <= sendTime ";
		if(!getLoginUser().getRoleCode().equals("ADMIN")) {
    		sql=sql+" and createUserId='"+getLoginUser().getId()+"' ";
		}
		sql=sql+" group by DATE_FORMAT(sendTime, '%Y-%m-%d')";
		List<Map<String, Object>> listMap = jdbcTemplate.queryForList(sql);
		
		List<String> result = new ArrayList<>();
		for(String date:dateList) {
			String value = "0";
			for(Map<String, Object> map:listMap) {
				String day = map.get("sendTime")==null?"":map.get("sendTime").toString();
				String c = map.get("c")==null?"0":((Long)map.get("c")).intValue()+"";
				if(day.equals(date)) value=c;
			}
			result.add(value);
		}
		return result;
	}
	
	public List<String> getPercentList(List<String> dateList){
		String sql="select count(1) c,sum(case when sendStatus=1 then 1 else 0 end) c2,DATE_FORMAT(sendTime, '%Y-%m-%d') sendTime from tb_sms where sendStatus in (1,2) and DATE_SUB(CURDATE(), INTERVAL 10 DAY) <= sendTime ";
		if(!getLoginUser().getRoleCode().equals("ADMIN")) {
			sql=sql+" and createUserId='"+getLoginUser().getId()+"' ";
		}
		sql=sql+" group by DATE_FORMAT(sendTime, '%Y-%m-%d')";
		List<Map<String, Object>> listMap = jdbcTemplate.queryForList(sql);
		
		List<String> result = new ArrayList<>();
		for(String date:dateList) {
			String value = "0";
			for(Map<String, Object> map:listMap) {
				String day = map.get("sendTime")==null?"":map.get("sendTime").toString();
				Integer c = map.get("c")==null?0:((Long)map.get("c")).intValue();
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
	 */
	public void updateSmsForSmpp(Integer smsId,SubmitSmResp response,Boolean isSuccess) {
		if(smsId!=null && smsId!=0) {
			if(isSuccess) {
				jdbcTemplate.update("update tb_sms set mid='"+response.getMessageId()+"',sendStatus=1,statTime=now() where id="+smsId);
			}else {
				jdbcTemplate.update("update tb_sms set mid='"+response.getMessageId()+"',sendStatus=2,sendStat='"+response.getResultMessage()+"',statTime=now() where id="+smsId);
			}
			//计费,发送一条
			if(isSuccess) {
				//得到用户短信费用
				PlatManager platManager =null;
				List<PlatManager> list = jdbcTemplate.query("select * from tb_plat_manager where id in (select createUserId from tb_sms where id = "+smsId+")", new BeanPropertyRowMapper<PlatManager>(PlatManager.class));
				if(list!=null&&list.size()>0) {
					platManager = list.get(0);
				}
				lock.lock();
				try {
					//插入钱包日志,首先记录日志
					List<WalletLog> walletLogList = new ArrayList<WalletLog>();
					WalletLog walletLog = new WalletLog();
					walletLog.setUserId(platManager.getId());
					walletLog.setMoney(platManager.getPrice());
					walletLogList.add(walletLog);
					walletLogService.insertWalletLog(walletLogList);
					//更新钱包
					List<Wallet> walletList = new ArrayList<Wallet>();
					Wallet wallet = new Wallet();
					wallet.setUserId(platManager.getId());
					wallet.setMoney(platManager.getPrice());
					walletList.add(wallet);
					walletService.updateWalletMoney(walletList);
				} finally {
		            lock.unlock();
		        }
			}
		}
	}
}
