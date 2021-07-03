package star.sms.operation_log.controller;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import star.sms._frame.base.PageSupport;
import star.sms.operation_log.pojo.DataOperationLog;
import star.sms.operation_log.service.DataOperationLogService;

/**
 * @author star
 */
@Controller
@CrossOrigin
@RequestMapping("/dataOperationLog")
public class DataOperationLogController {

	@Autowired
	private DataOperationLogService dataOperationLogService;

	/**
	 * 查询全部数据
	 * @return
	 */
	@RequestMapping(method= RequestMethod.GET,value = "list")
	public Object findAll(){
		return "/datalog/list";
	}

	/**
	 * 分页+多条件查询
	 * @return 分页结果
	 */
	@RequestMapping(value="/listS",method=RequestMethod.GET)
	@ResponseBody
	public Object list(PageSupport pagesupport,String actionType,String startTime,String endTime){
		Page<DataOperationLog> page = dataOperationLogService.findByPage(pagesupport.getPage(),actionType,startTime,endTime);
		Map<String, Object> result = new HashMap<String,Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}

}
