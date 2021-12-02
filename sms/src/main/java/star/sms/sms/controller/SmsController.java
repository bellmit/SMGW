package star.sms.sms.controller;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import star.sms._frame.base.BaseController;
import star.sms._frame.base.PageSupport;
import star.sms._frame.utils.CSVExportUtil;
import star.sms._frame.utils.excel.ExcelExportUtil2;
import star.sms.account.domain.AccountInfo;
import star.sms.account.service.AccountService;
import star.sms.logs.service.LogsService;
import star.sms.platmanager.domain.PlatManager;
import star.sms.platmanager.service.PlatManagerService;
import star.sms.sms.domain.SmsTask;
import star.sms.sms.job.ReportSyncJob;
import star.sms.sms.service.SmsService;
import star.sms.sms.vo.SendingDetailParam;
import star.sms.sms.vo.SmsLogParam;
import star.sms.smsmq.config.SmsCode;

/**
 * 短信统计和日志
 * @author star
 */
@Controller
@RequestMapping("/sms")
public class SmsController extends BaseController {
	
	@Autowired
	private LogsService logsService;
	
	@Autowired
	private SmsService smsService;
	
	@Autowired
	private DefaultMQProducer defaultMQProducer;
	
	@Autowired
	private PlatManagerService platManagerService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	Gson json;
	
	/**
	 * 导出类型选择
	 * @return
	 */
	@RequestMapping(value = "/exportBasic", method = RequestMethod.GET)
	public String exportBasic(ModelMap model) {
		return "/sms/exportBasic";
	}
	

	/**
	 * 短信日志-明细查询
	 * @return
	 */
	@RequestMapping(value = "/smsLogList", method = RequestMethod.GET)
	public String smsTemplateList(ModelMap model) {
		List<PlatManager> users = platManagerService.findValidUser();
		List<SmsTask> tasks = smsService.findValidTask();
		model.addAttribute("statMap",json.toJson(SmsCode.statMap));
		model.addAttribute("smppMap",json.toJson(SmsCode.smppMap));
		model.addAttribute("users",users);
		model.addAttribute("tasks",json.toJson(tasks));
		return "/sms/smsLogList";
	}
	
	/**
	 * 短信日志-数据统计
	 * @return
	 */
	@RequestMapping(value = "/smsDateList", method = RequestMethod.GET)
	public String smsDateList(ModelMap model) {
		ReportSyncJob.TASK_TIME=new Timestamp(System.currentTimeMillis());
		return "/sms/smsDateList";
	}
	
	/**
	 * 短信日志-任务统计
	 * @return
	 */
	@RequestMapping(value = "/smsTaskList", method = RequestMethod.GET)
	public String smsTaskList(ModelMap model) {
		ReportSyncJob.TASK_TIME=new Timestamp(System.currentTimeMillis());
		List<PlatManager> users = platManagerService.findValidUser();
		model.addAttribute("users",users);
		return "/sms/smsTaskList";
	}
	
	/**
	 * 短信日志-线路统计
	 * @return
	 */
	@RequestMapping(value = "/smsAccountList", method = RequestMethod.GET)
	public String smsAccountList(ModelMap model) {
		ReportSyncJob.TASK_TIME=new Timestamp(System.currentTimeMillis());
		if(getLoginUser().getRoleCode().equalsIgnoreCase("ADMIN")) {
			// 线路列表
			List<AccountInfo> accountInfoList = accountService.findAccountInfoList();
			model.addAttribute("accountInfoList",accountInfoList);
		}else {
			model.addAttribute("accountInfoList",new ArrayList<>());
		}
		return "/sms/smsAccountList";
	}
	
	/**
	 * 分页查询
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getTaskDetailList")
	@ResponseBody
	public Object getTaskDetailList(SmsLogParam smsLogParam, PageSupport pagesupport) {
		Page page = smsService.getTaskDetailList(smsLogParam, pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}
	
	/**
	 * 导出
	 * @param queryParam
	 */
	@RequestMapping("/downTaskDetailList")
	public void downTaskDetailList(SmsLogParam smsLogParam, PageSupport pagesupport) throws Exception {
		pagesupport.setPageNumber(0);
		pagesupport.setPageSize(Integer.MAX_VALUE);
		Page page = smsService.getTaskDetailList(smsLogParam, pagesupport.getPage());
		List<SendingDetailParam> rows = page.getContent();
		ExcelExportUtil2 excelExportUtil = new ExcelExportUtil2(SendingDetailParam.class);
		excelExportUtil.export(rows, "任务详情");
		excelExportUtil.down(response, "任务详情.xlsx");
		logsService.addData("导出任务详情,任务ID："+smsLogParam.getTaskId());
	}
	
	/**
	 * 分页查询
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getSmsTaskList")
	@ResponseBody
	public Object getSmsTaskList(SmsLogParam smsLogParam, PageSupport pagesupport) {
		ReportSyncJob.TASK_TIME=new Timestamp(System.currentTimeMillis());
		Page page = smsService.getSmsTaskList(smsLogParam, pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}
	
	/**
	 * 分页查询
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getSmsLogList")
	@ResponseBody
	public Object getSmsLogList(SmsLogParam smsLogParam, PageSupport pagesupport) {
		Page page = smsService.getSmsLogList(smsLogParam, pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}
	
	/**
	 * 导出
	 * @param queryParam
	 */
	@RequestMapping("/downSmsLog")
	public void downSmsLog(SmsLogParam smsLogParam, PageSupport pagesupport) throws Exception {
		pagesupport.setPageNumber(0);
		pagesupport.setPageSize(Integer.MAX_VALUE);
		Page page = smsService.getSmsLogList(smsLogParam, pagesupport.getPage());
		List<SmsLogParam> rows = page.getContent();
		if(smsLogParam.getFileType().equals("1")) {
			ExcelExportUtil2 excelExportUtil = new ExcelExportUtil2(SmsLogParam.class);
			excelExportUtil.export(rows, StringUtils.isNotEmpty(smsLogParam.getFileName())?smsLogParam.getFileName():"短信明细");
			excelExportUtil.down(response, (StringUtils.isNotEmpty(smsLogParam.getFileName())?smsLogParam.getFileName():"短信明细")+".xlsx");
		}else {
            CSVExportUtil util=new CSVExportUtil<>();
        	List<String> objs=new ArrayList<>();
     		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     		objs.add("mid,用户,线路名称,手机号码,归属运营地,发送内容,发送时间,报告时间,发送状态,发送结果");
     		for(SmsLogParam info :rows) {
            	 StringBuffer sb=new StringBuffer();
            	 sb.append(info.getMid()==null?"":info.getMid()).append(",");
            	 sb.append(info.getNickName()==null?"":info.getNickName()).append(",");
            	 sb.append(info.getChannelName()==null?"":info.getChannelName()).append(",");
            	 sb.append(info.getPhone()==null?"":info.getPhone()).append(",");
            	 sb.append(info.getIsp()==null?"":info.getIsp()).append(",");
            	 sb.append(info.getContent()==null?"":info.getContent()).append(",");
            	 sb.append(info.getSendTime()==null?"":dateFormat.format(info.getSendTime())).append(",");
            	 sb.append(info.getStatTime()==null?"":dateFormat.format(info.getStatTime())).append(",");
            	 if(info.getSendStatus()==0){
               	 	sb.append("待发送,");
            	 }else if(info.getSendStatus()==1){
            		 sb.append("发送成功,");
            	 }else if(info.getSendStatus()==2){
            		 sb.append("发送失败,");
            	 }else if(info.getSendStatus()==3){
            		 sb.append("已终止,");
            	 }else if(info.getSendStatus()==4){
            		 sb.append("发送中,");
            	 } 
            	 String format="";
				 if (SmsCode.statMap.containsKey(info.getSendStat())) {
					 if("0".equals(info.getSendStat())) {
						 format = "DELIVRD-" + SmsCode.statMap.get(info.getSendStat());
					 } else {
						 format = info.getSendStat() + "-" + SmsCode.statMap.get(info.getSendStat());
					 }
				 }
				 if (SmsCode.smppMap.containsKey(info.getSendStat())) {
					 if("0".equals(info.getSendStat())) {
						 format = "DELIVRD-" + SmsCode.smppMap.get(info.getSendStat());
					 } else {
						 format = info.getSendStat() + "-" + SmsCode.smppMap.get(info.getSendStat());
					 }
				 }
				 if (StringUtils.isEmpty(format) && StringUtils.isNotEmpty(info.getSendStat())) {
					format = info.getSendStat() + "-未投递成功";
				 }
        		 sb.append(format);
            	 objs.add(sb.toString());
     		}
            util.downCommonCsv(response,(StringUtils.isNotEmpty(smsLogParam.getFileName())?smsLogParam.getFileName():"短信明细")+".csv",objs);
		}
		logsService.addData("导出短信明细,taskId:"+smsLogParam.getTaskId());
	}
	
	/**
	 * 分页查询
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getSmsDateList")
	@ResponseBody
	public Object getSmsDateList(String beginTime,String endTime, PageSupport pagesupport) {
		ReportSyncJob.TASK_TIME=new Timestamp(System.currentTimeMillis());
		Page page = smsService.getSmsDateList(beginTime,endTime, pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}
	
	/**
	 * 分页查询
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getSmsAccountList")
	@ResponseBody
	public Object getSmsAccountList(String beginTime,String endTime,String account, PageSupport pagesupport) {
		ReportSyncJob.TASK_TIME=new Timestamp(System.currentTimeMillis());
		Page page = smsService.getSmsAccountList(beginTime,endTime,account, pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}
	
	
	@RequestMapping(value = "/mqtest")
	@ResponseBody
	public Object mqtest() throws UnsupportedEncodingException, MQClientException, RemotingException, MQBrokerException, InterruptedException {
		
        Message msg = new Message("sms_topic",
                "sms_tag",
                "OrderID188",
                "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
       defaultMQProducer.send(msg,new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
            	System.out.printf("%s%n", sendResult.getSendStatus());
            }
  
            @Override
            public void onException(Throwable throwable) {
            }
        });
		return "";
	}
}
