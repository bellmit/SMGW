package star.sms.platrole.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import star.sms._frame.base.BaseController;
import star.sms._frame.base.PageSupport;
import star.sms.menuinfo.domain.MenuInfo;
import star.sms.menuinfo.service.MenuInfoService;
import star.sms.operation_log.service.DataOperationLogService;
import star.sms.platrole.domain.MenuRole;
import star.sms.platrole.domain.PlatRole;
import star.sms.platrole.service.MenuRoleService;
import star.sms.platrole.service.PlatRoleService;

/**
 * 平台用户角色Controller
 * @author star
 */
@Controller
@RequestMapping("/platrole")
public class PlatRoleController extends BaseController {

	@Resource
	private PlatRoleService platRoleService;

	@Resource
	private MenuInfoService menuInfoService;
	
	@Resource
	private MenuRoleService menuRoleService;

	@Resource
	private DataOperationLogService dataOperationLogService;

	/**
	 * 列表查询
	 * 
	 * @param model
	 * @param roleCode
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping("/index")
	public Object index(ModelMap model) {
		return "/platrole/list";
	}

	@RequestMapping(value = "/list")
	@ResponseBody
	public Object list(ModelMap model,String keyword,PageSupport pagesupport) {
		Page<PlatRole> page = platRoleService.findByRoleCodeContaining(keyword, pagesupport.getPage());
		Map<String, Object> result = new HashMap<String,Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}
	
	
	/**
	 * 验证RoleCode唯一
	 * @param roleId
	 * @param RoleCode
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/validateRoleCode", method=RequestMethod.POST)
	public Object validateRoleCode(Integer roleId, String roleCode) {
		if (StringUtils.isEmpty(roleCode)) {
			return ERROR("empty");
		} else {
			PlatRole oldPlatRole = platRoleService.verifyRepeatByRoleIdAndRoleCode(roleId, roleCode.trim());
			
			if (oldPlatRole == null) {
				return SUCCESS();
			} else {
				return ERROR("repeat");
			}
		}
	}
	
	/**
	 * 验证roleName唯一
	 * @param roleId
	 * @param roleName
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/validateRoleName", method=RequestMethod.POST)
	public Object validateRoleName(Integer roleId, String roleName) {
		if (StringUtils.isEmpty(roleName)) {
			return ERROR("empty");
		} else {
			PlatRole oldPlatRole = platRoleService.verifyRepeatByRoleIdAndRoleName(roleId, roleName.trim());
			
			if (oldPlatRole == null) {
				return SUCCESS();
			} else {
				return ERROR("repeat");
			}
		}
	}

	/**
	 * 跳转-新增或修改
	 * 
	 * @param model
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public Object form(ModelMap model, Integer roleId) {
		PlatRole platRole = null;
		if (roleId != null) {
			platRole = platRoleService.findOne(roleId);
		} else {
			platRole = new PlatRole();
		}
		model.addAttribute(platRole);
		return "/platrole/form";
	}

	/**
	 * 新增保存或修改保存的方法
	 * 
	 * @param model
	 * @param platRole
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Object save(ModelMap model, PlatRole platRole) {
		platRole.setRoleCode(platRole.getRoleCode().trim());
		platRole.setRoleName(platRole.getRoleName().trim());
		if (platRole.getRoleId() == null) {
			platRoleService.save(platRole);
			//添加新增日志
			dataOperationLogService.add(0,0,"新增角色："+platRole.getRoleName(),super.getLoginUser());
		} else {
			platRoleService.updatePlatRole(platRole);
			//添加修改日志
			dataOperationLogService.add(0,1,"修改角色："+platRole.getRoleName(),super.getLoginUser());
		}
		return SUCCESS();
	} 
	
	/** 
	 * 删除角色
	 * @param model
	 * @param platRole
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Object delete(ModelMap model, PlatRole platRole) {
		platRoleService.delete(platRole);
		return SUCCESS();
	}

	/**
	 * 跳转-授权页面
	 * 
	 * @param model
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value = "/authorizationForm", method = RequestMethod.GET)
	public Object authorizationForm(ModelMap model, Integer roleId) {
		// 查询出角色拥有权限的所有的菜单
		List<MenuRole> menuRoleList = menuRoleService.findByRoleId(roleId);
		Set<Integer> authorityMenuIdSet = menuRoleList.stream().map(r->r.getMenuId()).collect(Collectors.toSet());
		
		// 查询出所有的菜单
		Iterable<MenuInfo> menuInfoList = menuInfoService.findAll();
		List<MenuInfo> superiorMenuInfoList = IterableUtils.toList(menuInfoList);
		if (superiorMenuInfoList.size()>0){
			//给菜单排序
			Collections.sort(superiorMenuInfoList, new Comparator<MenuInfo>() {
				@Override
				public int compare(MenuInfo o1, MenuInfo o2) {
					//升序
					return o1.getMenuIndex().compareTo(o2.getMenuIndex());
				}
			});
		}
		// 整理菜单层级（二级）
		List<MenuInfo> firstLevelMenuList = new LinkedList<MenuInfo>();
		Map<String, List<MenuInfo>> secondLevelMenuMap = new LinkedHashMap<String, List<MenuInfo>>();
		Map<String, List<MenuInfo>> thirdLevelMenuMap = new LinkedHashMap<String, List<MenuInfo>>();
		for (MenuInfo menuInfo: superiorMenuInfoList) {
			if (menuInfo.getMenuLevel() == 1) {
				firstLevelMenuList.add(menuInfo);
			} else if (menuInfo.getMenuLevel() == 2) {
				List<MenuInfo> secondLevelMenuList = secondLevelMenuMap.get(menuInfo.getMenuParentId().toString());
				if (secondLevelMenuList == null) {
					secondLevelMenuList = new LinkedList<MenuInfo>();
					secondLevelMenuList.add(menuInfo);
				} else {
					secondLevelMenuList.add(menuInfo);
				}
				secondLevelMenuMap.put(menuInfo.getMenuParentId().toString(), secondLevelMenuList);
			}
			else if (menuInfo.getMenuLevel() == 3) {
				List<MenuInfo> thirdLevelMenuList = thirdLevelMenuMap.get(menuInfo.getMenuParentId().toString());
				if (thirdLevelMenuList == null) {
					thirdLevelMenuList = new LinkedList<MenuInfo>();
					thirdLevelMenuList.add(menuInfo);
				} else {
					thirdLevelMenuList.add(menuInfo);
				}
				thirdLevelMenuMap.put(menuInfo.getMenuParentId().toString(), thirdLevelMenuList);
			}
		}
		
		// 返回
		model.addAttribute("roleId", roleId);
		model.addAttribute("authorityMenuIdSet", authorityMenuIdSet);
		model.addAttribute("firstLevelMenuList", firstLevelMenuList);
		model.addAttribute("secondLevelMenuMap", secondLevelMenuMap);
		model.addAttribute("thirdLevelMenuMap", thirdLevelMenuMap);
		return "/platrole/authorityForm";
	}

	/**
	 * 角色授权
	 * 
	 * @param model
	 * @param roleId
	 * @param menuId
	 * @return
	 */

	@RequestMapping(value = "authorization", method = RequestMethod.POST)
	@ResponseBody
	public Object authorization(ModelMap model, Integer roleId, Integer[] menuId) {
		if (roleId != null && roleId > 0) {
			menuRoleService.authorityMenuRole(roleId, menuId);
		}
		//添加修改日志
		dataOperationLogService.add(0,5,"角色授权",super.getLoginUser());
		return SUCCESS();
	}
}
