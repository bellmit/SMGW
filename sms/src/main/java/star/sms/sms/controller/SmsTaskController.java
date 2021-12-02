package star.sms.sms.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import star.sms._frame.base.BaseController;
import star.sms._frame.base.PageSupport;
import star.sms._frame.utils.DateUtils;
import star.sms._frame.utils.ExcelUtils;
import star.sms._frame.utils.StringUtils;
import star.sms.account.domain.AccountInfo;
import star.sms.account.service.AccountService;
import star.sms.logs.service.LogsService;
import star.sms.platmanager.domain.PlatManager;
import star.sms.platmanager.service.PlatManagerService;
import star.sms.sms.domain.Sms;
import star.sms.sms.domain.SmsSignature;
import star.sms.sms.domain.SmsTask;
import star.sms.sms.service.SmsService;
import star.sms.sms.service.SmsSignatureService;
import star.sms.sms.service.SmsTaskService;
import star.sms.smsmq.config.SmsCode;
import star.sms.wallet.domain.Wallet;
import star.sms.wallet.service.WalletService;

/**
 * 短信发送任务管理
 * @author star
 */
@Controller
@RequestMapping("/smsTask")
public class SmsTaskController extends BaseController {
	
	@Autowired
	private SmsTaskService smsTaskService;
	@Autowired
	private SmsService smsService;
	@Autowired
	private WalletService walletService;
	@Autowired
	private LogsService logsService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private PlatManagerService platManagerService;
	@Autowired
	private SmsSignatureService smsSignatureService;
	@Autowired
	Gson json;
	
	/**
	 * 待发送任务
	 * @return
	 */
	@RequestMapping(value = "/toSendList", method = RequestMethod.GET)
	public String toSendList(ModelMap model) {
		return "/sms/toSendList";
	}
	
	/**
	 * 正在发送任务
	 * @return
	 */
	@RequestMapping(value = "/sendingList", method = RequestMethod.GET)
	public String sendingList(ModelMap model) {
		return "/sms/sendingList";
	}
	
	/**
	 * 正在发送任务-短信详情
	 * @return
	 */
	@RequestMapping(value = "/sendingDetail", method = RequestMethod.GET)
	public String sendingDetail(ModelMap model) {
		model.addAttribute("statMap",json.toJson(SmsCode.statMap));
		model.addAttribute("smppMap",json.toJson(SmsCode.smppMap));
		return "/sms/sendingDetail";
	}
	
	/**
	 * 发送完成任务
	 * @return
	 */
	@RequestMapping(value = "/completeList", method = RequestMethod.GET)
	public String completeList(ModelMap model) {
		return "/sms/completeList";
	}
	
	/**
	 * 任务管理表单
	 * @return
	 */
	@RequestMapping(value = "/smsTaskForm", method = RequestMethod.GET)
	public String smsTaskForm(ModelMap model) {
		model.addAttribute("warning","0");
		if(!smsTaskService.checkCount()) {
			model.addAttribute("warning","1");
		}
		//得到当前用户账户信息
		AccountInfo  accountInfo = null;
		PlatManager loginUser = platManagerService.findByLoginName(getLoginUser().getLoginName());
		Integer accountId = loginUser.getAccountId();
		if(accountId!=null) {
			accountInfo = accountService.findOne(accountId);
		}
		String accountName = "";
		Integer channelType = 0;
		if(accountInfo!=null) {
			accountName = accountInfo.getTitle();
			channelType = accountInfo.getChannelType();
		}
		List<SmsSignature> signatureList = smsSignatureService.findByPage(null, new PageRequest(0, 9999)).getContent();
		model.addAttribute("accountName",accountName);
		model.addAttribute("signatureList",json.toJson(signatureList));
		model.addAttribute("channelType",channelType+"");
		
		return "/sms/smsTaskForm";
	}
	
	/**
	 * 任务管理自定义导入表单
	 * @return
	 */
	@RequestMapping(value = "/smsTaskImportForm", method = RequestMethod.GET)
	public String smsTaskImportForm(ModelMap model) {
		return "/sms/smsTaskImportForm";
	}
	
	/**
	 * 任务管理手动添加手机号
	 * @return
	 */
	@RequestMapping(value = "/smsTaskHandleForm", method = RequestMethod.GET)
	public String smsTaskHandleForm(ModelMap model) {
		return "/sms/smsTaskHandleForm";
	}
	
	/**
	 * 任务管理 选择通讯录
	 * @return
	 */
	@RequestMapping(value = "/smsTaskGroupForm", method = RequestMethod.GET)
	public String smsTaskGroupForm(ModelMap model) {
		return "/sms/smsTaskGroupForm";
	}
	
	/**
	 * 任务管理 选择模板
	 * @return
	 */
	@RequestMapping(value = "/chooseTemplateForm", method = RequestMethod.GET)
	public String chooseTemplateForm(ModelMap model) {
		return "/sms/chooseTemplateForm";
	}
	
	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete(String ids) {
		smsTaskService.deleteByIds(ids);
		logsService.addData("批量删除通讯录");
		return SUCCESS();
	}
	
	@RequestMapping(value = "/uploadPhone", method = RequestMethod.POST)
    @ResponseBody
	public Object uploadPhone(HttpServletRequest request, @RequestParam("file")MultipartFile files,Integer taskId){
		try {
			String result = ExcelUtils.getFileSort(files);
			if ("excel".equals(result)) {
				taskId = smsTaskService.uploadPhone(files,taskId);
			} else if("txt".equals(result)||"csv".equals(result)) {
				taskId = smsTaskService.uploadPhone2(files,taskId);
            }else {
            	return ERROR("文件格式不支持");
            }
		}catch (Exception e) {
			e.printStackTrace();
			return ERROR("导入失败");
		}
		return SUCCESS(taskId);
	}
	
	/**
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/handlePhone")
	@ResponseBody
	public Object handlePhone(Integer taskId,String content) {
		try {
			taskId= smsTaskService.handlePhone(taskId,content);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR("操作失败");
		}
		return SUCCESS(taskId);
	}
	
	/**
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/groupPhone")
	@ResponseBody
	public Object groupPhone(Integer taskId,String groupIds) {
		try {
			taskId= smsTaskService.groupPhone(taskId,groupIds);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR("操作失败");
		}
		return SUCCESS(taskId);
	}
	
	/**
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/findTaskPhone")
	@ResponseBody
	public Object findTaskPhone(Integer taskId) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Sms> smsList = smsTaskService.findPhoneByTaskId(taskId);
		
		List<String> existsPhoneList = new ArrayList<String>();
		//过滤查询有几条无效号码
		Integer failPhone=0;
		//过滤查询有几条重复号码
		Integer repeatPhone=0;
		for(Sms sms:smsList) {
			if(!StringUtils.isMobileNO(sms.getPhone())) {
				failPhone++;
			}
			if(!existsPhoneList.contains(sms.getPhone())) {
				existsPhoneList.add(sms.getPhone());
			}else {
				repeatPhone++;
			}
		}
		result.put("totalPhone", smsList.size());
		result.put("failPhone", failPhone);
		result.put("repeatPhone", repeatPhone);
		return SUCCESS(result);
	}
	
	/**
	 * 一键清除案件中的重复手机号码
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/clearRepeat")
	@ResponseBody
	public Object clearRepeat(Integer taskId) {
		List<Sms> smsList = smsTaskService.findPhoneByTaskId(taskId);
		
		List<Sms> remainList = new ArrayList<>();
		List<String> existsPhoneList = new ArrayList<String>();
		for(Sms sms:smsList) {
			if(!existsPhoneList.contains(sms.getPhone())) {
				existsPhoneList.add(sms.getPhone());
			}else {
				remainList.add(sms);
			}
		}
		smsService.batchUpdate(remainList);
		return SUCCESS();
	}
	
	/**
	 * 一键清除案件中的无效手机号码
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/clearFail")
	@ResponseBody
	public Object clearFail(Integer taskId) {
		List<Sms> smsList = smsTaskService.findPhoneByTaskId(taskId);
		
		List<Sms> remainList = new ArrayList<>();
		for(Sms sms:smsList) {
			if(!StringUtils.isMobileNO(sms.getPhone())) {
				remainList.add(sms);
			}
		}
		smsService.batchUpdate(remainList);
		return SUCCESS();
	}
	
	/**
	 * 一键清除案件中的全部手机号码
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/clearAll")
	@ResponseBody
	public Object clearAll(Integer taskId) {
		smsTaskService.clearAll(taskId);
		return SUCCESS();
	}
	
	/**
	 * 任务开始
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/createTask")
	@ResponseBody
	public Object createTask(Integer taskId,String content,String sendTimeStr,String title,String signature ) {
		Wallet wallet = walletService.findIfExist();
		if(!smsTaskService.checkCount()) return ERROR("提交失败，当前未完成任务数量已达上限！");
		if(wallet.getMoney().compareTo(BigDecimal.ZERO)<=0) return ERROR("当前余额不足，所剩余额："+wallet.getMoney()+"元！");
		//如果余额大于0.则需要计算一下本任务所需要的的费用，然后比较一下剩余余额是否够
		PlatManager loginUser = platManagerService.findByLoginName(getLoginUser().getLoginName());
		BigDecimal count = wallet.getMoney().divide(loginUser.getPrice(), 3, BigDecimal.ROUND_DOWN);
		BigDecimal toUseCount = smsTaskService.getToUseCount(taskId,content);
		if(count.compareTo(toUseCount)<0) return ERROR("当前余额不足，所剩余额："+wallet.getMoney()+"元！,所剩条数："+count.intValue()+"条");
		
		//得到当前用户账户信息
		AccountInfo  accountInfo = null;
		Integer accountId = loginUser.getAccountId();
		if(accountId!=null) {
			accountInfo = accountService.findOne(accountId);
		} 
		if(accountInfo == null)  {
			return ERROR("提交失败，该用户未绑定短信账号");
		}
		//创建任务
		SmsTask task = smsTaskService.findOne(taskId);
		if(task!=null) {
			if(StringUtils.isNotEmpty(sendTimeStr)) {
				task.setSendTime(new Timestamp(DateUtils.getDate(sendTimeStr).getTime()));
			}
			task.setChannelId(accountId);
			task.setTitle(title);
			task.setSignature(signature);
			task.setSendStatus(0);
			Integer priority = loginUser.getPriority();
			if (priority == null) {
				priority = 0;
			}
			task.setPriority(priority);
			task.setContent(content);
			task.setChannelType(accountInfo.getChannelType());
			task.setUpdateTime(new Timestamp(System.currentTimeMillis()));
			task.setUpdateUserId(loginUser.getId());
			task.setNickName(loginUser.getNickName());
			Pattern pattern = Pattern.compile("(\\$\\{.*\\})");
			Matcher matcher = pattern.matcher(task.getContent());
			if(matcher.find()) {
				if(accountInfo.getChannelType()==4) {
					return ERROR("提交失败，该通道不支持发送点对点模板。");
				}else {
					task.setContentType(2);
				}
			}
			
			List<Sms> smsList = smsTaskService.findPhoneByTaskId(taskId);
			
			List<Sms> remainList = new ArrayList<>();
			List<String> existsPhoneList = new ArrayList<String>();
			for(Sms sms:smsList) {
				if(!existsPhoneList.contains(sms.getPhone())) {
					existsPhoneList.add(sms.getPhone());
				}else {
					remainList.add(sms);
				}
			}
			//删除重复数
			smsService.batchUpdate(remainList);
			//循环任务下面的手机号码。将字段动态赋值到指定的占位符
			int row = smsTaskService.updatePhones(task,accountInfo);
			//保存任务
			smsTaskService.save(task);
			//创建任务成功,全部根据条数全部计费
			if (row > 0) {
				walletService.updateWalletMoneyBySmsNum(loginUser, row , task);
			}
			return SUCCESS();
		}else {
			return ERROR("提交失败，请刷新页面重新创建任务");
		}
	}
	
	/**
	 * 分页查询
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getToSendList")
	@ResponseBody
	public Object getToSendList(String beginTime,String endTime, PageSupport pagesupport) {
		Page page = smsTaskService.getToSendList(beginTime,endTime, pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}
	
	/**
	 * 分页查询
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getSendingList")
	@ResponseBody
	public Object getSendingList(String beginTime,String endTime, PageSupport pagesupport) {
		Page page = smsTaskService.getSendingList(beginTime,endTime, pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}
	
	/**
	 * 分页查询
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getCompleteList")
	@ResponseBody
	public Object getCompleteList(String beginTime,String endTime, PageSupport pagesupport) {
		Page page = smsTaskService.getCompleteList(beginTime,endTime, pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}
}
