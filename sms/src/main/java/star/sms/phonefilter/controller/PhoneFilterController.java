package star.sms.phonefilter.controller;

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

import lombok.extern.slf4j.Slf4j;
import star.sms._frame.base.BaseController;
import star.sms._frame.base.PageSupport;
import star.sms.logs.service.LogsService;
import star.sms.phonefilter.domain.PhoneFilter;
import star.sms.phonefilter.service.PhoneFilterService;

/**
 * 拦截策略配置
 * @author star
 */
@Slf4j
@Controller
@RequestMapping("/phoneFilter")
public class PhoneFilterController extends BaseController {
	@Autowired
	private PhoneFilterService phoneFilterService;
	@Autowired
	private LogsService logsService;
	
	/**
	 * 拦截策略配置
	 * @return
	 */
	@RequestMapping(value = "/phoneFilterList", method = RequestMethod.GET)
	public String phoneFilterList(ModelMap model) {
		return "/phonefilter/phoneFilterList";
	}
	
	/**
	 * 拦截策略配置
	 * @return
	 */
	@RequestMapping(value = "/phoneFilterForm", method = RequestMethod.GET)
	public String phoneFilterForm(ModelMap model) {
		return "/phonefilter/phoneFilterForm";
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
		Page page = phoneFilterService.findByPage(keyword, pagesupport.getPage());
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
		phoneFilterService.deleteByIds(ids);
		logsService.addData("批量删除拦截策略配置");
		return SUCCESS();
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
	public Object saveOrUpdate(ModelMap model, PhoneFilter obj) throws Exception {
		if(obj.getId() == null||obj.getId()==0) {//新增
			obj.setCreateTime(new Timestamp(System.currentTimeMillis()));
			obj.setCreateUserId(getLoginUser().getId());
			obj.setUpdateUserId(getLoginUser().getId());
			obj.setUpdateTime(new Timestamp(System.currentTimeMillis()));
			phoneFilterService.save(obj);
			logsService.addData("创建拦截策略,策略名称:"+obj.getTitle());
		}else {
			PhoneFilter old = phoneFilterService.findOne(obj.getId());
			if (old != null) {
				old.setKeyword(obj.getKeyword());
				old.setPhones(obj.getPhones());
				old.setAreas(obj.getAreas());
				obj.setUpdateUserId(getLoginUser().getId());
				obj.setUpdateTime(new Timestamp(System.currentTimeMillis()));
				phoneFilterService.save(old);
			}
			logsService.addData("修改拦截策略,策略名称:"+obj.getTitle());
		}
		return SUCCESS();
	}
	
	/**
	 * 根据id查询
	 */
	@RequestMapping(value = "/findById", method = RequestMethod.POST)
	@ResponseBody
	public Object findById(ModelMap model, Integer id) throws Exception {
		PhoneFilter m = phoneFilterService.findOne(id);
		return SUCCESS(m);
	}
}
