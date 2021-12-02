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
import star.sms.sms.domain.SmsSignature;
import star.sms.sms.service.SmsSignatureService;

/**
 * @author star
 */
@Controller
@RequestMapping("/smsSignature")
public class SmsSignatureController extends BaseController {
	
	@Autowired
	private LogsService logsService;
	@Autowired
	private SmsSignatureService smsSignatureService;
	
	/**
	 * 通讯录管理-通讯录类型列表
	 * @return
	 */
	@RequestMapping(value = "/smsSignatureList", method = RequestMethod.GET)
	public String smsSignatureList(ModelMap model) {
		return "/sms/smsSignatureList";
	}
	
	@RequestMapping(value = "/smsSignatureForm", method = RequestMethod.GET)
	public String smsSignatureForm(ModelMap model) {
		return "/sms/smsSignatureForm";
	}
	
	/**
	 * 分页查询
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getSmsSignatureList")
	@ResponseBody
	public Object getSmsSignatureList(String keyword, PageSupport pagesupport) {
		Page page = smsSignatureService.findByPage(keyword,pagesupport.getPage());
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
	public Object saveOrUpdate(ModelMap model, SmsSignature obj) throws Exception {
		if(obj.getId() == null||obj.getId()==0) {//新增
			obj.setCreateTime(new Timestamp(System.currentTimeMillis()));
			obj.setCreateUserId(getLoginUser().getId());
			smsSignatureService.save(obj);
			logsService.addData("创建短信签名模板,内容:"+obj.getTitle());
		}else {
			SmsSignature old = smsSignatureService.findOne(obj.getId());
			if (old != null) {
				old.setTitle(obj.getTitle());
				old.setUpdateTime(new Timestamp(System.currentTimeMillis()));
				old.setUpdateUserId(getLoginUser().getId());
				smsSignatureService.save(old);
			}
			logsService.addData("修改短信签名模板,id:"+obj.getId()+",内容:"+obj.getTitle());
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
		smsSignatureService.deleteByIds(ids);
		logsService.addData("批量删除短信签名模板");
		return SUCCESS();
	}
	
	/**
	 * 根据id查询
	 */
	@RequestMapping(value = "/findById", method = RequestMethod.POST)
	@ResponseBody
	public Object findById(ModelMap model, Integer id) throws Exception {
		SmsSignature m = smsSignatureService.findOne(id);
		return SUCCESS(m);
	}
}
