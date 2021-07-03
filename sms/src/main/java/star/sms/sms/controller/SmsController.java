package star.sms.sms.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import star.sms._frame.base.BaseController;
import star.sms._frame.base.PageSupport;
import star.sms._frame.utils.excel.ExcelExportUtil2;
import star.sms.logs.service.LogsService;
import star.sms.sms.service.SmsService;
import star.sms.sms.vo.SmsLogParam;

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

	/**
	 * 短信日志-明细查询
	 * @return
	 */
	@RequestMapping(value = "/smsLogList", method = RequestMethod.GET)
	public String smsTemplateList(ModelMap model) {
		return "/sms/smsLogList";
	}
	
	/**
	 * 短信日志-数据统计
	 * @return
	 */
	@RequestMapping(value = "/smsDateList", method = RequestMethod.GET)
	public String smsDateList(ModelMap model) {
		return "/sms/smsDateList";
	}
	
	/**
	 * 短信日志-线路统计
	 * @return
	 */
	@RequestMapping(value = "/smsAccountList", method = RequestMethod.GET)
	public String smsAccountList(ModelMap model) {
		List<String> options = smsService.getAccounts();
		model.addAttribute("options",options);
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
		ExcelExportUtil2 excelExportUtil = new ExcelExportUtil2(SmsLogParam.class);
		excelExportUtil.export(rows, "短信明细");
		excelExportUtil.down(response, "短信明细.xlsx");
		logsService.addData("导出短信明细");
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
