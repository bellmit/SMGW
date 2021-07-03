package star.sms.main.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import star.sms._frame.base.BaseController;
import star.sms.platmanager.service.PlatManagerService;
import star.sms.sms.service.SmsService;

/**
 * 首页
 * 
 * @author star
 */
@Controller
@RequestMapping("/")
public class MainController extends BaseController {
	
	@Autowired
	private SmsService smsService;
	@Autowired
	private PlatManagerService platManagerService;
	
	/**
	 * 首页信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/main", method = RequestMethod.GET)
	public String manageList(ModelMap model) {
		Integer dayCount = smsService.getDayCount();
		Integer monthCount = smsService.getMonthCount();
		Integer surplusCount = smsService.getSurplusCountCount();
		Integer userCount = platManagerService.getUserCount();
		model.addAttribute("dayCount",dayCount+"");
		model.addAttribute("monthCount",monthCount+"");
		model.addAttribute("surplusCount",surplusCount+"");
		model.addAttribute("userCount",userCount+"");
		return "/main";
	}
	
	/**
	 * 
	* @Title: manageList
	* @Description: 首页图表
	* @author: STAR
	* @param model
	* @return
	* @throws
	 */
	@RequestMapping(value = "/mainChart", method = RequestMethod.POST)
	@ResponseBody
	public Object manageChart(ModelMap model) {
		//图表统计
		Map<String, Object> mainInfoChart = new HashMap<String, Object>();
		List<String> dateList = getDateList();
		mainInfoChart.put("dateList", dateList);
		mainInfoChart.put("countList", smsService.getCountList(dateList));
		mainInfoChart.put("percentList", smsService.getPercentList(dateList));
		return mainInfoChart;
	}
	
	/**
	 * 获取最近十天的日期列表
	 * @return
	 */
	private List<String> getDateList(){
		List<String> result = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for(int i=9;i>=0;i--) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -i);
			result.add(sdf.format(calendar.getTime()));
		}
		return result;
	}

	
	/**
	 * 404
	 * 
	 * @return
	 */
	@RequestMapping(value = "/404", method = RequestMethod.GET)
	public String noFind(ModelMap model) {
		return "/404";
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test(ModelMap model) {
		return "/test";
	}

	/**
	 * 500
	 * 
	 * @return
	 */
	@RequestMapping(value = "/500", method = RequestMethod.GET)
	public String nullPoint(ModelMap model) {
		return "/500";
	}
}
