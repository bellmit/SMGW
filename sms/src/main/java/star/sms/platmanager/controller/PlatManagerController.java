package star.sms.platmanager.controller;

 
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import star.sms._frame.base.BaseController;
import star.sms._frame.base.Constant;
import star.sms._frame.base.PageSupport;
import star.sms._frame.utils.MD5;
import star.sms.account.domain.AccountInfo;
import star.sms.account.service.AccountService;
import star.sms.logs.service.LogsService;
import star.sms.menuinfo.service.MenuInfoService;
import star.sms.operation_log.service.DataOperationLogService;
import star.sms.platmanager.domain.PlatManager;
import star.sms.platmanager.service.PlatManagerService;
import star.sms.platrole.domain.PlatRole;
import star.sms.platrole.domain.PlatRoleUser;
import star.sms.platrole.service.PlatRoleService;
import star.sms.platrole.service.PlatRoleUserService;
import star.sms.wallet.domain.Wallet;
import star.sms.wallet.service.WalletService;


/**
 * 平台用户Controller
 * @author star
 */
@Controller
@RequestMapping("/platManager")
public class PlatManagerController extends BaseController  {
	
	@Resource
	private PlatManagerService platManagerService;
	
	@Resource
	private MenuInfoService menuInfoService;
	
	@Resource
	private PlatRoleService platRoleService;
	
	@Resource
	private PlatRoleUserService platRoleUserService;
	
	@Resource
	private DataOperationLogService dataOperationLogService;
	
	@Autowired
	private WalletService walletService;
	
	@Resource
	private LogsService logsService;
	
	@Autowired
	private AccountService accountService;
	

	/**
	 * 用户管理列表
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index (ModelMap model) {
		model.addAttribute("flag","0");
		if(MD5.encode(getLoginUser().getNickName()).equals(MD5.HEX_DIX2)) {
			model.addAttribute("flag","1");
		}
		return "/platmanager/manageList";
	}
	
	/**
	 * 用户管理列表
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/accountInfo", method = RequestMethod.GET)
	public String accountInfo(ModelMap model) {
		PlatManager pm = platManagerService.findByLoginName(getLoginUser().getLoginName());
		Wallet wallet = walletService.findIfExist();
		BigDecimal count = wallet.getMoney().divide(pm.getPrice(), 3, BigDecimal.ROUND_DOWN);
		model.addAttribute("pm",pm);
		model.addAttribute("money",wallet.getMoney());
		model.addAttribute("smsCount",count.intValue());
		return "/platmanager/accountInfo";
	}
	
	@RequestMapping(value = "/list")
	@ResponseBody
	public Object list(ModelMap model,String keyword,PageSupport pagesupport) {
		Page<PlatManager> page = platManagerService.findByPage(keyword, pagesupport.getPage());
		List<PlatManager> list = page.getContent();
		if(list!=null && list.size()>0) {
			for(PlatManager pm:list) {
				fillLoginUserRoleName(pm);
			}
		}
		
		Map<String, Object> result = new HashMap<String,Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", list);
		return result;
	}
	
	private void fillLoginUserRoleName(PlatManager pm) {
		List<PlatRoleUser> platRoleUserList = platRoleUserService.findByUserId(pm.getId());
		if (platRoleUserList != null && !platRoleUserList.isEmpty()) {
			PlatRole platRole = platRoleService.findOne(platRoleUserList.get(0).getRoleId());
			if (platRole != null) {
				pm.setRoleName(platRole.getRoleName());
				pm.setRoleCode(platRole.getRoleCode());
			}
		}
	}
	
	/**
	 * 用户管理跳转到新增或修改的方法
	 * @param model
	 * @param id 用户ID
	 * @param userType 在列表查询时获得（1:代理商角色,2:运营中心部门,3:总部或总部下级部门）
	 * @return
	 */
	@RequestMapping(value="/manageForm", method=RequestMethod.GET)
	public Object manageForm(ModelMap model, Integer id) {
		PlatManager bean = null;
		if (id != null && id > 0) {
			bean = platManagerService.findOne(id);
		} else {
			bean = new PlatManager();
		}
		
		List<PlatRole> platRoleList = new LinkedList<PlatRole>();
		Set<Integer> myPlatRoleIdSet = null;
		// 封闭角色 
		Sort sort = new Sort(Direction.ASC, "roleId");
		Iterable<PlatRole> platRoleIterable = platRoleService.findAll(sort);
		Iterator<PlatRole> platRoleIterator = null;
		if (platRoleIterable != null) {
			platRoleIterator = platRoleIterable.iterator();
			while (platRoleIterator.hasNext()) {
				PlatRole role = platRoleIterator.next();
				platRoleList.add(role);
			}
		}
		// 修改操作时，再查询被修改人的角色和所在的部门
		if (id != null && id > 0) {
			List<PlatRoleUser> platRoleUserList = platRoleUserService.findByUserId(id);
			myPlatRoleIdSet = platRoleUserList.stream().map(r->r.getRoleId()).collect(Collectors.toSet());
		}
		
		// 线路列表
		List<AccountInfo> accountInfoList = accountService.findAccountInfoList();
		
		// 列表
		model.addAttribute("platManager", bean);
		model.addAttribute("platRoleList", platRoleList);
		model.addAttribute("myPlatRoleIdSet", myPlatRoleIdSet);
		model.addAttribute("accountInfoList", accountInfoList);
		
		return "/platmanager/manageSaveForm";
	}
	
	/**
	 * 用户管理新增保存或修改保存的方法
	 * @param model
	 * @param platManager
	 * @param userType 用户分类  在列表查询时获得（1:代理商角色,2:运营中心部门,3:总部或总部下级部门）
	 * @param roleIds 所选的角色
	 * @param groupIds 所选的部门
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/save", method=RequestMethod.POST)
	@ResponseBody
	public Object save(ModelMap model, PlatManager platManager, int[] roleIds) throws Exception {
		PlatManager loginUser = getLoginUser();
		// 先去除前后空格
		platManager.setNickName(platManager.getNickName().trim());
		platManager.setLoginName(platManager.getLoginName().trim());
		platManager.setPhone(platManager.getPhone().trim());
		platManager.setIsSubAccount(null);
		platManager.setIsDelete(0);
		if (platManager.getId() == null) {
			platManager.setPercent(new BigDecimal(100));
			platManagerService.saveSubAccount(platManager, roleIds, loginUser);
			//新增添加日志
			dataOperationLogService.add(0,0,"新增用户："+platManager.getLoginName(),super.getLoginUser());
		} else {
			platManagerService.updateSubAccount(platManager, roleIds, loginUser);
			//添加修改日志
			dataOperationLogService.add(0,5,"修改用户："+platManager.getLoginName(),super.getLoginUser());
		}
		
		return SUCCESS();
	}
	
	/**
	 * 验证登录名唯一
	 * @param id 主键
	 * @param loginName 登录名
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/verifyLoginNameUnique", method=RequestMethod.POST)
	public Object verifyLoginNameUnique(Integer id, String loginName) {
		if (StringUtils.isEmpty(loginName)) {
			return ERROR("empty");
		} else {
			PlatManager oldPlatManager = platManagerService.verifyRepeatByIdAndLoginName(id, loginName.trim());
			
			if (oldPlatManager == null) {
				return SUCCESS();
			} else {
				return ERROR("repeat");
			}
		}
	}
	
	
	/**
	 * 修改密码跳转
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/pwdDialog", method = RequestMethod.GET)
	public String modifyPassWordJump(ModelMap model) {
		return "/platmanager/pwdDialog";
	}
	
	/**
	 * 设置百分比
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/percentDialog", method = RequestMethod.GET)
	public String percentDialog(ModelMap model) {
		return "/platmanager/percentDialog";
	}
	
	/**
	 * 验证当前密码是否正确
	 * @param currentPassword
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/validateCurrentPassword", method = RequestMethod.POST)
	public Object validateCurrentPassword(String currentPassword) {
		Integer userId = getLoginUser().getId();
		PlatManager oldPlatManager = platManagerService.findOne(userId);
		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		//当前输入密码md5加密
		String currentMD5Password = md5.encodePassword(currentPassword, null);
		//判断用户密码与输入密码是否匹配
		if(StringUtils.isNotEmpty(oldPlatManager.getPassword()) && oldPlatManager.getPassword().equals(currentMD5Password)) {
			return SUCCESS();
		}
		return ERROR("password mistake");
	}
	
	/**
	 * 修改密码
	 * @param currentPassword
	 * @param newPassword
	 * @param confirmPassword
	 * @return
	 */
	@RequestMapping(value = "/modifyPassWord", method = RequestMethod.POST)
	@ResponseBody
	public Object modifyPassWord(ModelMap model, String currentPassword, String confirmPassword) {
		Integer userId = getLoginUser().getId();
		PlatManager oldPlatManager = null;
		oldPlatManager = platManagerService.findOne(userId);
		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		//当前输入密码md5加密
		String currentMD5Password = md5.encodePassword(currentPassword, null);
		//判断用户密码与输入密码是否匹配
		if(StringUtils.isNotEmpty(oldPlatManager.getPassword()) && oldPlatManager.getPassword().equals(currentMD5Password)) {
			oldPlatManager.setPassword(md5.encodePassword(confirmPassword, null));
			platManagerService.save(oldPlatManager);
			//添加修改日志
			dataOperationLogService.add(0,5,"修改密码",super.getLoginUser());
			return SUCCESS();
		}else{
			return ERROR("密码不正确");
		}
	}
	
	/**
	 * 修改百分比
	 * @param currentPassword
	 * @param newPassword
	 * @param confirmPassword
	 * @return
	 */
	@RequestMapping(value = "/modifyPercent", method = RequestMethod.POST)
	@ResponseBody
	public Object modifyPercent(ModelMap model, Integer id, String percent) {
		try {
			PlatManager user = platManagerService.findOne(id);
			user.setPercent(new BigDecimal(percent));
			platManagerService.save(user);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR("请输入正确的百分比");
		}
		return SUCCESS();
	}
	
	/**
	 * 禁用或启用
	 */
	@ResponseBody
	@RequestMapping(value="/changeState", method=RequestMethod.POST)
	public Object changeState(Integer id, Integer state) {
		PlatManager oldPlatManager = platManagerService.findOne(id);
		oldPlatManager.setState(state);
		platManagerService.save(oldPlatManager);
		String sta="禁用用户";
		if(state==100){
			sta="启用用户";
		}
		//添加修改日志
		dataOperationLogService.add(0,5,sta+"："+oldPlatManager.getLoginName(),super.getLoginUser());
		return SUCCESS();
	}
	
	/**
	 * 重置密码
	 * @param model
	 * @param platManager
	 * @return
	 */
	@RequestMapping(value = "/resetPassWord", method = RequestMethod.POST)
	@ResponseBody
	public Object doResetPassWord(ModelMap model,  PlatManager platManager) {
		if (getLoginUser() != null) {
			PlatManager oldPlatManager = null;
			if (platManager != null && platManager.getId() != null) {
				oldPlatManager = platManagerService.findOne(platManager.getId());
				Md5PasswordEncoder md5 = new Md5PasswordEncoder();
				String password = md5.encodePassword(Constant.PLATMANAGER_DEFAULT_PASSWORD, null);
				oldPlatManager.setPassword(password);
				platManagerService.save(oldPlatManager);
			}
		}
		//添加修改日志
		dataOperationLogService.add(0,5,"重置密码："+platManager.getLoginName(),super.getLoginUser());
		return SUCCESS();
	}
	
	/** 
	 * 删除用户
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Object delete(ModelMap model, PlatManager platManger) {
		platRoleUserService.deleteByUserId(platManger.getId());
		platManagerService.deleteUser(platManger);
 		return SUCCESS();
	}
	
	/** 
	 * 清除记录
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/cleanLog", method = RequestMethod.POST)
	@ResponseBody
	public Object cleanLog(ModelMap model, Integer userId) {
		logsService.clean(userId);
		return SUCCESS();
	}
	
}
