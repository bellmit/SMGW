package star.sms.group.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import star.sms._frame.base.BaseController;
import star.sms._frame.base.PageSupport;
import star.sms._frame.utils.DateUtils;
import star.sms._frame.utils.excel.ExcelExportUtil2;
import star.sms.group.domain.GroupMember;
import star.sms.group.service.GroupMemberService;
import star.sms.group.vo.GroupMemberParam;
import star.sms.logs.service.LogsService;

/**
 * @author star
 */
@Controller
@RequestMapping("/groupMember")
public class GroupMemberController extends BaseController {
	
	@Autowired
	private GroupMemberService groupMemberService;
	@Autowired
	private LogsService logsService;
	
	/**
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/groupMemberForm")
	public String txlForm(ModelMap model) {
		return "/group/groupMemberForm";
	}
	
	/**
	 * 通讯录管理-通讯录列表
	 * @return
	 */
	@RequestMapping(value = "/groupMemberList", method = RequestMethod.GET)
	public String txlList(ModelMap model) {
		return "/group/groupMemberList";
	}
	
	/**
	 * 分页查询
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getTxlList")
	@ResponseBody
	public Object getTxlList(String keyword, PageSupport pagesupport) {
		Page page = groupMemberService.findByPage(keyword, pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}
	
	/**
	 * 导出
	 * 
	 * @param queryParam
	 */
	@RequestMapping("/downExcel")
	public void downExcel(String keyword, PageSupport pagesupport) throws Exception {
		pagesupport.setPageNumber(0);
		pagesupport.setPageSize(Integer.MAX_VALUE);
		Page page = groupMemberService.findByPage(keyword, pagesupport.getPage());
		List<GroupMemberParam> rows = page.getContent();
		ExcelExportUtil2 excelExportUtil = new ExcelExportUtil2(GroupMemberParam.class);
		excelExportUtil.export(rows, "通讯录");
		excelExportUtil.down(response, "通讯录.xlsx");
		logsService.addData("导出通讯录");
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
	public Object saveOrUpdate(ModelMap model, GroupMember obj) throws Exception {
		if(obj.getId() == null) {//新增
			if(StringUtils.isNotEmpty(obj.getBirthdayStr())) obj.setBirthday(new Timestamp(DateUtils.getDate(obj.getBirthdayStr()).getTime()));
			obj.setCreateTime(new Timestamp(System.currentTimeMillis()));
			obj.setCreateUserId(getLoginUser().getId());
			groupMemberService.save(obj);
			logsService.addData("创建通讯录,手机号："+obj.getPhone()+",姓名："+obj.getName());
		}else {
			GroupMember old = groupMemberService.findOne(obj.getId());
			if (old != null) {
				BeanUtils.copyProperties(obj, old,"createTime","id","createUserId");
				if(StringUtils.isNotEmpty(obj.getBirthdayStr())) {
					old.setBirthday(new Timestamp(DateUtils.getDate(obj.getBirthdayStr()).getTime()));
				}else {
					old.setBirthday(null);
				}
				groupMemberService.save(old);
			}
			logsService.addData("修改通讯录,手机号："+old.getPhone()+",姓名："+old.getName());
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
		groupMemberService.deleteByIds(ids);
		logsService.addData("批量删除通讯录");
		return SUCCESS();
	}
	
	/**
	 * 根据id查询
	 */
	@RequestMapping(value = "/findById", method = RequestMethod.POST)
	@ResponseBody
	public Object findById(ModelMap model, Integer id) throws Exception {
		GroupMember m = groupMemberService.findOne(id);
		return SUCCESS(m);
	}
}
