package star.sms.group.controller;

import java.sql.Timestamp;
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

import star.sms._frame.base.BaseController;
import star.sms._frame.base.PageSupport;
import star.sms.group.domain.Group;
import star.sms.group.service.GroupService;
import star.sms.logs.service.LogsService;

/**
 * @author star
 */
@Controller
@RequestMapping("/group")
public class GroupController extends BaseController {
	
	@Autowired
	private LogsService logsService;
	@Autowired
	private GroupService groupService;

	/**
	 * 通讯录管理
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String approve(ModelMap model) {
		return "/group/index";
	}
	
	/**
	 * 通讯录管理-通讯录类型列表
	 * @return
	 */
	@RequestMapping(value = "/groupList", method = RequestMethod.GET)
	public String txlTypeList(ModelMap model) {
		return "/group/groupList";
	}
	
	@RequestMapping(value = "/groupForm", method = RequestMethod.GET)
	public String txlTypeForm(ModelMap model) {
		return "/group/groupForm";
	}
	
	/**
	 * 分页查询
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getGroup")
	@ResponseBody
	public Object getGroup() {
		List<Group> list = groupService.getGroup();
		return SUCCESS(list);
	}
	
	/**
	 * 分页查询
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getGroupList")
	@ResponseBody
	public Object getGroupList(String keyword, PageSupport pagesupport) {
		Page page = groupService.findByPage(keyword, pagesupport.getPage());
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
	public Object saveOrUpdate(ModelMap model, Group obj) throws Exception {
		if(obj.getId() == null||obj.getId()==0) {//新增
			List<Group> list = groupService.findByGroupName(obj.getGroupName());
			if(list!=null && list.size()>0) return ERROR("改分组已存在");
			obj.setCreateTime(new Timestamp(System.currentTimeMillis()));
			obj.setCreateUserId(getLoginUser().getId());
			groupService.save(obj);
			logsService.addData("创建通讯录分组:"+obj.getGroupName());
		}else {
			List<Group> list = groupService.findByGroupName(obj.getGroupName());
			if(list!=null && list.size()>0) {
				for(Group group:list) {
					if(group.getId().intValue()!=obj.getId()) {
						return ERROR("该分组已存在");
					}
				}
			}
			Group old = groupService.findOne(obj.getId());
			if (old != null) {
				old.setGroupName(obj.getGroupName());
				old.setUpdateTime(new Timestamp(System.currentTimeMillis()));
				old.setUpdateUserId(getLoginUser().getId());
				groupService.save(old);
			}
			logsService.addData("修改通讯录分组:"+obj.getGroupName());
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
		groupService.deleteByIds(ids);
		logsService.addData("批量删除通讯录分组");
		return SUCCESS();
	}
	
	/**
	 * 根据id查询
	 */
	@RequestMapping(value = "/findById", method = RequestMethod.POST)
	@ResponseBody
	public Object findById(ModelMap model, Integer id) throws Exception {
		Group m = groupService.findOne(id);
		return SUCCESS(m);
	}
}
