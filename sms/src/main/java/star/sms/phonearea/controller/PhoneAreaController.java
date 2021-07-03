package star.sms.phonearea.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import star.sms._frame.base.BaseController;
import star.sms._frame.base.PageSupport;
import star.sms._frame.utils.excel.ExcelExportUtil2;
import star.sms.logs.service.LogsService;
import star.sms.phonearea.domain.PhoneArea;
import star.sms.phonearea.service.PhoneAreaService;
import star.sms.phonearea.vo.PhoneAreaParams;

/**
 * 号码归属地
 * @author star
 */
@Slf4j
@Controller
@RequestMapping("/phoneArea")
public class PhoneAreaController extends BaseController {
	@Autowired
	private PhoneAreaService phoneAreaService;
	@Autowired
	private LogsService logsService;
	
	/**
	 * 号码归属地
	 * @return
	 */
	@RequestMapping(value = "/phoneAreaList", method = RequestMethod.GET)
	public String phoneAreaList(ModelMap model) {
		return "/phonearea/phoneAreaList";
	}
	
	/**
	 * 号码归属地
	 * @return
	 */
	@RequestMapping(value = "/phoneAreaForm", method = RequestMethod.GET)
	public String phoneAreaForm(ModelMap model) {
		return "/phonearea/phoneAreaForm";
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
	public Object getList(String keyword, PageSupport pagesupport) {
		Page page = phoneAreaService.findByPage(keyword, pagesupport.getPage());
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
		phoneAreaService.deleteByIds(ids);
		logsService.addData("批量删除地区号段");
		return SUCCESS();
	}
	
	/**
	 * 导出
	 * @param queryParam
	 */
	@RequestMapping("/downExcel")
	public void downExcel(String keyword, PageSupport pagesupport) throws Exception {
		pagesupport.setPageNumber(0);
		pagesupport.setPageSize(Integer.MAX_VALUE);
		Page page = phoneAreaService.findByPage(keyword, pagesupport.getPage());
		List<PhoneAreaParams> rows = page.getContent();
		ExcelExportUtil2 excelExportUtil = new ExcelExportUtil2(PhoneAreaParams.class);
		excelExportUtil.export(rows, "地区号段");
		excelExportUtil.down(response, "地区号段.xlsx");
		logsService.addData("导出地区号段");
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
	public Object saveOrUpdate(ModelMap model, PhoneArea obj) throws Exception {
		if(obj.getId() == null||obj.getId()==0) {//新增
			phoneAreaService.save(obj);
			logsService.addData("创建区域号段,号段:"+obj.getPhone());
		}else {
			PhoneArea old = phoneAreaService.findOne(obj.getId());
			if (old != null) {
				old.setPref(obj.getPref());
				old.setPhone(obj.getPhone());
				old.setProvince(obj.getProvince());
				old.setCity(obj.getCity());
				old.setIsp(obj.getIsp());
				phoneAreaService.save(old);
			}
			logsService.addData("修改区域号段,id:"+obj.getId()+",号段:"+obj.getPhone());
		}
		return SUCCESS();
	}
	
	/**
	 * 根据id查询
	 */
	@RequestMapping(value = "/findById", method = RequestMethod.POST)
	@ResponseBody
	public Object findById(ModelMap model, Integer id) throws Exception {
		PhoneArea m = phoneAreaService.findOne(id);
		return SUCCESS(m);
	}
}
