package star.sms.sysconfig.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import star.sms._frame.base.BaseController;
import star.sms.logs.service.LogsService;
import star.sms.sysconfig.domain.SysConfig;
import star.sms.sysconfig.service.SysConfigService;

/**
 * 系统配置表
 * @author star
 */
@Slf4j
@Controller
@RequestMapping("/sysConfig")
public class SysConfigController extends BaseController {
	@Autowired
	private SysConfigService sysConfigService;
	@Autowired
	private LogsService logsService;
	
	/**
	 * 号码归属地
	 * @return
	 */
	@RequestMapping(value = "/sysConfigForm", method = RequestMethod.GET)
	public String sysConfigForm(ModelMap model) {
		return "/sysconfig/sysConfigForm";
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
	public Object saveOrUpdate(ModelMap model, Integer taskCount) throws Exception {
		SysConfig m = sysConfigService.getConfig();
		Integer oldCount = m.getTaskCount();
		m.setTaskCount(taskCount);
		sysConfigService.save(m);
		logsService.addData("修改每个用户最大任务个数,旧个数:"+oldCount+",新个数:"+taskCount);
		return SUCCESS();
	}
	
	/**
	 * 获取系统配置
	 */
	@RequestMapping(value = "/getConfig", method = RequestMethod.POST)
	@ResponseBody
	public Object getConfig(ModelMap model) throws Exception {
		SysConfig m = sysConfigService.getConfig();
		return SUCCESS(m);
	}
}
