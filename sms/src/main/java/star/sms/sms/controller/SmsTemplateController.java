package star.sms.sms.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import star.sms._frame.base.BaseController;
import star.sms._frame.base.PageSupport;
import star.sms.logs.service.LogsService;
import star.sms.sms.domain.SmsTemplate;
import star.sms.sms.service.SmsTemplateService;

/**
 * @author star
 */
@Controller
@RequestMapping("/smsTemplate")
public class SmsTemplateController extends BaseController {
	
	@Autowired
	private LogsService logsService;
	@Autowired
	private SmsTemplateService smsTemplateService;
	
	/**
	 * 通讯录管理-通讯录类型列表
	 * @return
	 */
	@RequestMapping(value = "/smsTemplateList", method = RequestMethod.GET)
	public String smsTemplateList(ModelMap model) {
		return "/sms/smsTemplateList";
	}
	
	/**
	 * 通讯录管理-审批列表
	 * @return
	 */
	@RequestMapping(value = "/smsTemplateApproveList", method = RequestMethod.GET)
	public String smsTemplateApproveList(ModelMap model) {
		return "/sms/smsTemplateApproveList";
	}
	
	/**
	 * 通讯录管理-审批
	 * @return
	 */
	@RequestMapping(value = "/approveForm", method = RequestMethod.GET)
	public String approveForm(ModelMap model) {
		return "/sms/approveForm";
	}
	
	@RequestMapping(value = "/smsTemplateForm", method = RequestMethod.GET)
	public String smsTemplateForm(ModelMap model) {
		return "/sms/smsTemplateForm";
	}
	
	/**
	 * 分页查询
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getSmsTemplateList")
	@ResponseBody
	public Object getSmsTemplateList(String keyword,String approveStatus, PageSupport pagesupport) {
		Page page = smsTemplateService.findByPage(keyword,approveStatus, pagesupport.getPage());
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
	@RequestMapping(value = "/getSmsTemplateApproveList")
	@ResponseBody
	public Object getSmsTemplateApproveList(String keyword,String approveStatus, PageSupport pagesupport) {
		Page page = smsTemplateService.getSmsTemplateApproveList(keyword,approveStatus, pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}
	
	/**
	 * 保存
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	@ResponseBody
	public Object saveOrUpdate(ModelMap model, SmsTemplate obj) throws Exception {
		if(obj.getId() == null||obj.getId()==0) {//新增
			obj.setSmsSize(obj.getContent().length());
			obj.setApproveStatus(0);
			obj.setCreateTime(new Timestamp(System.currentTimeMillis()));
			obj.setCreateUserId(getLoginUser().getId());
			smsTemplateService.save(obj);
			logsService.addData("创建短信模板,内容:"+obj.getContent());
		}else {
			SmsTemplate old = smsTemplateService.findOne(obj.getId());
			if (old != null) {
				old.setSmsSize(obj.getContent().length());
				old.setApproveStatus(0);
				old.setContent(obj.getContent());
				old.setUpdateTime(new Timestamp(System.currentTimeMillis()));
				old.setUpdateUserId(getLoginUser().getId());
				smsTemplateService.save(old);
			}
			logsService.addData("修改短信模板,id:"+obj.getId()+",内容:"+obj.getContent());
		}
		return SUCCESS();
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
		smsTemplateService.deleteByIds(ids);
		logsService.addData("批量删除短信模板");
		return SUCCESS();
	}
	
	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/approve")
	@ResponseBody
	public Object approve(Integer id,Integer approveStatus,String memo) {
		SmsTemplate smsTemplate = smsTemplateService.findOne(id);
		smsTemplate.setApproveStatus(approveStatus);
		smsTemplate.setMemo(memo);
		smsTemplate.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		smsTemplate.setUpdateUserId(getLoginUser().getId());
		smsTemplateService.save(smsTemplate);
		logsService.addData("审批模板，id:"+id+",审批结果："+approveStatus+",备注:"+memo);
		return SUCCESS();
	}
	
	/**
	 * 根据id查询
	 */
	@RequestMapping(value = "/findById", method = RequestMethod.POST)
	@ResponseBody
	public Object findById(ModelMap model, Integer id) throws Exception {
		SmsTemplate m = smsTemplateService.findOne(id);
		return SUCCESS(m);
	}
}
