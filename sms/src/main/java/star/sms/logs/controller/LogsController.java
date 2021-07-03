package star.sms.logs.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import star.sms._frame.base.BaseController;
import star.sms._frame.base.PageSupport;
import star.sms.logs.service.LogsService;

/**
 * 日志管理
 * @author star
 */
@Slf4j
@Controller
@RequestMapping("/logs")
public class LogsController extends BaseController {
	@Resource
	private LogsService logsService;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(ModelMap model) {
		return "/logs/logsList";
	}
	
	/**
	 * 分页查询
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/logsList")
	@ResponseBody
	public Object logsList(String keyword,String startTime,String endTime, PageSupport pagesupport) {
		Page page = logsService.findByPage(keyword,startTime,endTime, pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}
}
