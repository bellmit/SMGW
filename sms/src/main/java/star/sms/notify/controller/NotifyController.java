package star.sms.notify.controller;

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
import star.sms.notify.domain.Notify;
import star.sms.notify.service.NotifyService;
import star.sms.notify.service.NotifyUserService;

/**
 * @author star
 */
@Controller
@RequestMapping("/notify")
public class NotifyController extends BaseController {
	
	@Autowired
	private NotifyService notifyService;
	@Autowired
	private NotifyUserService notifyUserService;
	@Autowired
	private LogsService logsService;
	
	/**
	 * 系统消息
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(ModelMap model) {
		return "/notify/notifyList";
	}
	
	/**
	 * 分页查询
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getList")
	@ResponseBody
	public Object getList(String keyword,Integer isRead, PageSupport pagesupport) {
		Page page = notifyService.findByPage(keyword,isRead, pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
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
		notifyUserService.deleteByIds(ids);
		logsService.addData("批量公告");
		return SUCCESS();
	}
	
	/**
	 * 全部标记为已读
	 * @return
	 */
	@RequestMapping(value = "/readAll")
	@ResponseBody
	public Object readAll() {
		notifyUserService.readAll();
		logsService.addData("标记全部公告为已读");
		return SUCCESS();
	}
	
	/**
	 * 根据id查询
	 */
	@RequestMapping(value = "/findById", method = RequestMethod.POST)
	@ResponseBody
	public Object findById(ModelMap model, Integer id) throws Exception {
		Notify m = notifyService.findOne(id);
		return SUCCESS(m);
	}
}
