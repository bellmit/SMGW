package star.sms.sms.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms._frame.utils.EncodingDetect;
import star.sms._frame.utils.ExcelData;
import star.sms._frame.utils.FileUtils;
import star.sms._frame.utils.StringUtils;
import star.sms._frame.utils.UUIDUtils;
import star.sms.account.domain.AccountInfo;
import star.sms.config.SystemConfig;
import star.sms.group.domain.GroupMember;
import star.sms.group.service.GroupMemberService;
import star.sms.phonearea.domain.PhoneArea;
import star.sms.phonefilter.domain.PhoneFilter;
import star.sms.platmanager.domain.PlatManager;
import star.sms.platmanager.service.PlatManagerService;
import star.sms.sms.dao.SmsTaskDao;
import star.sms.sms.domain.Sms;
import star.sms.sms.domain.SmsTask;
import star.sms.sms.vo.SendingListParam;
import star.sms.sysconfig.domain.SysConfig;
import star.sms.sysconfig.service.SysConfigService;

/**
 * 短信任务
 */
@Slf4j
@Service
@Transactional
public class SmsTaskService extends BaseServiceProxy<SmsTask>{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SmsTaskDao smsTaskDao;
    @Autowired
    private SmsService smsService;
    @Autowired
    private GroupMemberService groupMemberService;
    @Autowired
    private Gson json;
    @Autowired
    private SystemConfig systemConfig;
    @Autowired
    private RedissonClient redissonClient;
	@Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PlatManagerService platManagerService;
	@Override
	protected BaseRepository<SmsTask> getBaseRepository() {
		return smsTaskDao;
	}
	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}
	
	/**
	 * 批量删除信息
	 * @param ids
	 */
	public void deleteByIds(String ids) {
		if(ids.endsWith(",")) ids=ids.substring(0,ids.length()-1);
		jdbcTemplate.update("delete from tb_sms_task where id in ("+ids+")");
		jdbcTemplate.update("delete from tb_sms where taskId in ("+ids+")");
	}
	
	
	
	
	public Integer getTaskId(Integer taskId) {
		if(taskId==null || taskId==0) {
			SmsTask task = new SmsTask();
			task.setSendStatus(-1);
			task.setCreateTime(new Timestamp(System.currentTimeMillis()));
			task.setCreateUserId(getLoginUser().getId());
			task.setContentType(1);
			this.save(task);
			taskId = task.getId();
		}
		return taskId;
	}
	
	/**
	 * 创建任务导入手机号码
	 * @param remoteFile
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public Integer uploadPhone(MultipartFile remoteFile,Integer taskId) throws Exception {
		//第一步、首先获取任务id
		taskId=getTaskId(taskId);
		
		//第二步、将手机号码导入到手机号码表格中
		List<Sms> smsList = new ArrayList<>();
		List<String[]> excelDate = ExcelData.getExcelData(remoteFile);
		for (int i = 0; i < excelDate.size(); i++) {
			String[] row = excelDate.get(i);
			if(row.length>0) {
				String phone=row[0].trim();
				if(StringUtils.isNotEmpty(phone)) {
					Sms sms = new Sms();
					sms.setTaskId(taskId);
					sms.setPhone(phone);
					sms.setSendStatus(0);
					if(row.length>1) {
						Map<String, String> extMap = new HashMap<String, String>();
						for (int j = 1; j < row.length; j++) {
							String cellString = row[j].trim();
							if(StringUtils.isNotEmpty(cellString)) {
								extMap.put(j+"", cellString);
							}
						}
						sms.setMemo(json.toJson(extMap));
					}
					sms.setCreateTime(new Timestamp(System.currentTimeMillis()));
					sms.setCreateUserId(getLoginUser().getId());
					smsList.add(sms);
				}
			}
		}
		if(smsList.size()>0) smsService.batchSave(smsList);
		return taskId;
	}
	
	/**
	 * 创建任务导入手机号码
	 * @param remoteFile
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public Integer uploadPhone2(MultipartFile remoteFile,Integer taskId) throws Exception {
		//第一步、首先获取任务id
		taskId=getTaskId(taskId);
		
		//第二步、将手机号码导入到手机号码表格中
		InputStream inputStream = null;
		InputStreamReader reader = null;
		BufferedReader br = null;
		List<Sms> smsList = new ArrayList<>();
		String uuid = UUIDUtils.random();
		String absPath = FileUtils.getRootPath();
		File f = new File(absPath+"/temp.txt");
		log.info("获取到临时文件");
		remoteFile.transferTo(f);
		String javaEncode = EncodingDetect.getJavaEncode(f);
        if("BIG5".equals(javaEncode)){
            javaEncode="gb2312";
        }
		log.info("获取到临时文件编码"+javaEncode);
		inputStream = new FileInputStream(f);
		reader = new InputStreamReader(inputStream , javaEncode);
		br = new BufferedReader(reader);
		String text = "";
		int repeat = 0;
		int error = 0;
		while ((text = br.readLine()) != null) {
			if (StringUtils.isNotEmpty(text)) {
				String[] row = text.split(",");
				if(row.length>0) {
					String phone=row[0].trim();
					if(StringUtils.isNotEmpty(phone)) {
						Sms sms = new Sms();
						sms.setTaskId(taskId);
						sms.setPhone(phone);
						sms.setSendStatus(0);
						if(row.length>1) {
							Map<String, String> extMap = new HashMap<String, String>();
							for (int j = 1; j < row.length; j++) {
								String cellString = row[j].trim();
								if(StringUtils.isNotEmpty(cellString)) {
									extMap.put(j+"", cellString);
								}
							}
							sms.setMemo(json.toJson(extMap));
						}
						sms.setCreateTime(new Timestamp(System.currentTimeMillis()));
						sms.setCreateUserId(getLoginUser().getId());
						smsList.add(sms);
					}
				}
			}
		}
		
		if(smsList.size()>0) smsService.batchSave(smsList);
		return taskId;
	}
	
	/**
	 * 创建任务手动添加手机号码
	 * @param remoteFile
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public Integer handlePhone(Integer taskId,String content) throws Exception {
		//第一步、首先获取任务id
		taskId=getTaskId(taskId);
		
		//第二步、将手机号码导入到手机号码表格中
		List<Sms> smsList = new ArrayList<>();
		String[] phones = content.split(",|，|\n");
		for(String phone:phones) {
			if(StringUtils.isNotEmpty(phone)) {
				Sms sms = new Sms();
				sms.setTaskId(taskId);
				sms.setPhone(phone);
				sms.setSendStatus(0);
				sms.setCreateTime(new Timestamp(System.currentTimeMillis()));
				sms.setCreateUserId(getLoginUser().getId());
				smsList.add(sms);
			}
		}
		if(smsList.size()>0) smsService.batchSave(smsList);
		return taskId;
	}
	
	/**
	 * 创建任务 通讯录导入
	 * @param remoteFile
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public Integer groupPhone(Integer taskId,String groupIds) throws Exception {
		//第一步、首先获取任务id
		taskId=getTaskId(taskId);
		
		//第二步、将手机号码导入到手机号码表格中
		List<Sms> smsList = new ArrayList<>();
		List<GroupMember> memberList = groupMemberService.findByGoupIds(groupIds);
		for(GroupMember member:memberList) {
			if(StringUtils.isNotEmpty(member.getPhone())) {
				Sms sms = new Sms();
				sms.setTaskId(taskId);
				sms.setPhone(member.getPhone());
				sms.setSendStatus(0);
				sms.setCreateTime(new Timestamp(System.currentTimeMillis()));
				sms.setCreateUserId(getLoginUser().getId());
				smsList.add(sms);
			}
		}
		if(smsList.size()>0) smsService.batchSave(smsList);
		return taskId;
	}
	
	/**
	 * 
	 * @param taskId
	 * @return
	 */
	public List<Sms> findPhoneByTaskId(Integer taskId){
		return jdbcTemplate.query("select * from tb_sms where taskId='"+taskId+"' ", new BeanPropertyRowMapper<Sms>(Sms.class));
	}
	/**
	 * 
	 * @param taskId
	 * @return
	 */
	public void clearAll(Integer taskId){
		jdbcTemplate.update("delete from tb_sms where taskId='"+taskId+"' ");
	}
	
	public BigDecimal getToUseCount(Integer taskId,String content) {
		Integer c = jdbcTemplate.queryForObject("select count(1) from tb_sms where taskId='"+taskId+"' ",Integer.class);
		BigDecimal l = new BigDecimal(1);
		if(content.length()>70) {
			l = new BigDecimal(content.length()/65+1);
		}
		return l.multiply(new BigDecimal(c));
	}
	
	public void updatePhones(SmsTask task,AccountInfo  accountInfo) {
		List<Sms> list = findPhoneByTaskId(task.getId());
		Pattern pattern = Pattern.compile("(\\$\\{.*\\})");
		Matcher matcher = pattern.matcher(task.getContent());
		boolean needReplace=matcher.find();
		for(Sms sms:list) {
			sms.setAccount(accountInfo.getAccount());
			sms.setPassword(accountInfo.getPassword());
			sms.setExtno(accountInfo.getExtno());
			sms.setIp(accountInfo.getIp());
			sms.setAccountId(accountInfo.getId());
			
			sms.setUpdateTime(new Timestamp(System.currentTimeMillis()));
			sms.setUpdateUserId(getLoginUser().getId());
			sms.setNickName(getLoginUser().getNickName());
			
			sms.setSendTime(task.getSendTime());
			sms.setContent(task.getContent());
			sms.setChannelType(task.getChannelType());
			
			if(StringUtils.isNotEmpty(sms.getMemo()) && needReplace) {
				String content = task.getContent();
				Map<String, String> extMap = json.fromJson(sms.getMemo(), new TypeToken<Map<String, String>>() {}.getType());
				if(extMap.containsKey("1")) content = content.replace("${B}", extMap.get("1"));
				if(extMap.containsKey("2")) content = content.replace("${C}", extMap.get("2"));
				if(extMap.containsKey("3")) content = content.replace("${D}", extMap.get("3"));
				if(extMap.containsKey("4")) content = content.replace("${E}", extMap.get("4"));
				if(extMap.containsKey("5")) content = content.replace("${F}", extMap.get("5"));
				if(extMap.containsKey("6")) content = content.replace("${G}", extMap.get("6"));
				if(extMap.containsKey("7")) content = content.replace("${H}", extMap.get("7"));
				if(extMap.containsKey("8")) content = content.replace("${I}", extMap.get("8"));
				if(extMap.containsKey("9")) content = content.replace("${J}", extMap.get("9"));
				if(extMap.containsKey("10")) content = content.replace("${K}", extMap.get("10"));
				if(extMap.containsKey("11")) content = content.replace("${L}", extMap.get("11"));
				if(extMap.containsKey("12")) content = content.replace("${M}", extMap.get("12"));
				if(extMap.containsKey("13")) content = content.replace("${N}", extMap.get("13"));
				if(extMap.containsKey("14")) content = content.replace("${O}", extMap.get("14"));
				if(extMap.containsKey("15")) content = content.replace("${P}", extMap.get("15"));
				if(extMap.containsKey("16")) content = content.replace("${Q}", extMap.get("16"));
				if(extMap.containsKey("17")) content = content.replace("${R}", extMap.get("17"));
				if(extMap.containsKey("18")) content = content.replace("${S}", extMap.get("18"));
				if(extMap.containsKey("19")) content = content.replace("${T}", extMap.get("19"));
				if(extMap.containsKey("20")) content = content.replace("${U}", extMap.get("20"));
				sms.setContent(content);
			}
		}
		//删除手机号
		List<Sms> removeSmsList =new ArrayList<Sms>();
		//过滤模板
		List<PhoneFilter> phoneFilterList =new ArrayList<PhoneFilter>();
		//查询过滤模板
		StringBuilder sbFilter= new StringBuilder("");
		sbFilter.append("select * from tb_phone_filter");
		phoneFilterList =  jdbcTemplate.query(sbFilter.toString(), new BeanPropertyRowMapper<PhoneFilter>(PhoneFilter.class));
		//list短信规则过了
		//短信过滤，分离不符合规则短信
		if(phoneFilterList!=null&&list!=null) {
			for(PhoneFilter phoneFilter:phoneFilterList) {
				SmsLoop: for (Iterator<Sms> iterator = list.iterator(); iterator.hasNext();) {
					Sms sms = iterator.next();
					//正则关键词过滤,多个
					String contentPatterns = phoneFilter.getKeyword();
					if(contentPatterns!=null) {
						String[] contentPatternArray = contentPatterns.split(",|，|\n");
						for (String contentPattern : contentPatternArray) {
							if(org.apache.commons.lang3.StringUtils.isNotBlank(contentPattern)) {
								Pattern r = Pattern.compile(contentPattern);
								Matcher m = r.matcher(sms.getContent());
								if (m.find( )) {
									//存在匹配删除
									iterator.remove();
									removeSmsList.add(sms);
									continue SmsLoop;
								}
							}
						}
					}
					//号段过滤5-8位，多个
					String phones = phoneFilter.getPhones();
					if(phones!=null) {
						String[] phoneArray = phones.split(",|，|\n");
						for (String phonePattern : phoneArray) {
							if(org.apache.commons.lang3.StringUtils.isNotBlank(phonePattern)) {
								String smsPhone = sms.getPhone();
								if(smsPhone!=null&&smsPhone.length()>=11) {
									String smsPhone48 = smsPhone.substring(4,8);
									if(smsPhone48.contains(phonePattern)) {
										//存在匹配删除
										iterator.remove();
										removeSmsList.add(sms);
										continue SmsLoop;
									}
								}
							}
						}
					}
					//地区过滤
					String areas = phoneFilter.getAreas();
					if(areas!=null) {
						String[] areaArray = areas.split(",|，|\n");
						for (String areaPattern : areaArray) {
							if(areaPattern!=null) {
								String smsPhone = sms.getPhone();
								if(smsPhone!=null&&smsPhone.length()>=11) {
									String smsPhone07 = smsPhone.substring(0,7);
									RMap<String,PhoneArea> map =  redissonClient.getMap("phoneAreaMap");
									PhoneArea phoneArea = map.get(smsPhone07);
									if(phoneArea!=null) {
										String province = phoneArea.getProvince();
										if(province!=null&&province.equals(areaPattern)) {
											//存在匹配删除
											iterator.remove();
											removeSmsList.add(sms);
											continue SmsLoop;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		//删除过滤模板短信
		smsService.batchUpdate(removeSmsList);
		//保存短信列表
		smsService.save(list);
	}
	
	/**
	 * 分页查
	 * @param loginUser
	 * @param pageable
	 * @return
	 */
	public Page getToSendList(String beginTime,String endTime, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" from ( ");
		fromWhereSql.append(" select * FROM tb_sms_task a where createUserId='"+getLoginUser().getId()+"' and sendStatus=0 ");
		if(StringUtils.isNotEmpty(beginTime)) {
			fromWhereSql.append(" and createTime>='"+beginTime+"' ");
		}
		if(StringUtils.isNotEmpty(endTime)) {
			fromWhereSql.append(" and createTime<='"+endTime+" 23:59:59' ");
		}
		fromWhereSql.append(" ) m left join ");
		fromWhereSql.append(" (select taskId,count(1) totalCount,sum(case when sendStatus=1 then 1 else 0 end) successCount from tb_sms where createUserId='"+getLoginUser().getId()+"' group by taskId) n on m.id=n.taskId ");
		Page<SendingListParam> page = super.findPageBySql(SendingListParam.class, "select m.title,m.id taskId,m.sendTime,m.sendStatus,m.createTime,n.totalCount,n.successCount ", fromWhereSql.toString(), " order by m.createTime desc", null, pageable);
		return page;
	}
	
	/**
	 * 分页查
	 * @param loginUser
	 * @param pageable
	 * @return
	 */
	public Page getSendingList(String beginTime,String endTime, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" from ( ");
		fromWhereSql.append(" select * FROM tb_sms_task a where createUserId='"+getLoginUser().getId()+"' and sendStatus=4 ");
		if(StringUtils.isNotEmpty(beginTime)) {
			fromWhereSql.append(" and createTime>='"+beginTime+"' ");
		}
		if(StringUtils.isNotEmpty(endTime)) {
			fromWhereSql.append(" and createTime<='"+endTime+" 23:59:59' ");
		}
		fromWhereSql.append(" ) m left join ");
		fromWhereSql.append(" (select taskId,count(1) totalCount,sum(case when sendStatus=1 then 1 else 0 end) successCount from tb_sms where createUserId='"+getLoginUser().getId()+"' group by taskId) n on m.id=n.taskId ");
		Page<SendingListParam> page = super.findPageBySql(SendingListParam.class, "select m.title,m.id taskId,m.sendTime,m.sendStatus,m.createTime,n.totalCount,n.successCount ", fromWhereSql.toString(), " order by m.createTime desc", null, pageable);
		return page;
	}
	
	/**
	 * 分页查
	 * @param loginUser
	 * @param pageable
	 * @return
	 */
	public Page getCompleteList(String beginTime,String endTime, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" from ( ");
		fromWhereSql.append(" select * FROM tb_sms_task a where createUserId='"+getLoginUser().getId()+"' and sendStatus in (1,2,3) ");
		if(StringUtils.isNotEmpty(beginTime)) {
			fromWhereSql.append(" and createTime>='"+beginTime+"' ");
		}
		if(StringUtils.isNotEmpty(endTime)) {
			fromWhereSql.append(" and createTime<='"+endTime+" 23:59:59' ");
		}
		fromWhereSql.append(" ) m left join ");
		fromWhereSql.append(" (select taskId,count(1) totalCount,sum(case when sendStatus=1 then 1 else 0 end) successCount from tb_sms where createUserId='"+getLoginUser().getId()+"' group by taskId) n on m.id=n.taskId ");
		Page<SendingListParam> page = super.findPageBySql(SendingListParam.class, "select m.title,m.id taskId,m.sendTime,m.sendStatus,m.createTime,n.totalCount,n.successCount ", fromWhereSql.toString(), " order by m.createTime desc", null, pageable);
		return page;
	}
	
	
	/**
	 * 定时更新任务状态
	 */
	@Scheduled(cron = "0/10 * * * * ?")
	public void updateTaskSendStatus(){
		if(systemConfig.getIsAdmin()) return;
		log.info("更新发送状态...");
		StringBuilder sb= new StringBuilder("");
		List<Object[]> argsList= new ArrayList<Object[]>();
		//待发送，发送中
		sb.append(" select * from tb_sms_task where sendStatus in (0,4) ");
		List<SmsTask> smsTaskList =  jdbcTemplate.query(sb.toString(), new BeanPropertyRowMapper<SmsTask>(SmsTask.class));
		if(smsTaskList!=null) {
			for (SmsTask smsTask : smsTaskList) {
				Object[] smsTaskArray= {smsTask.getId()};
				argsList.add(smsTaskArray);
			}
		}
		//更改为发送成功
		StringBuilder sbUpdate= new StringBuilder("");
		sbUpdate.append(" update  tb_sms_task set sendStatus=1 where sendStatus in (0,4) and id=? and id not in (select taskId from tb_sms where sendStatus = 0) ");
		jdbcTemplate.batchUpdate(sbUpdate.toString(), argsList);
	}
	
	/**
	 * 每次创建任务判断当前用户最大任务个数
	 * @return
	 */
	public boolean checkCount() {
		if(getLoginUser().getRoleCode().equals("ADMIN")) return true;
		SysConfig sysConfig = sysConfigService.getConfig();
		Integer count = jdbcTemplate.queryForObject("select count(1) from tb_sms_task where createUserId='"+getLoginUser().getId()+"' and sendStatus in (0,4) ", Integer.class);
		if(count>=sysConfig.getTaskCount()) return false;
		return true;
	}
}
