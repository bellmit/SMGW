package star.sms._frame.config;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wf.captcha.SpecCaptcha;

import lombok.extern.slf4j.Slf4j;
import star.sms._frame.base.BaseController;
import star.sms._frame.base.ModuleCode;
import star.sms.config.SystemConfig;
import star.sms.ip.service.IpWhiteService;
import star.sms.menuinfo.domain.MenuInfo;
import star.sms.menuinfo.service.MenuInfoService;
import star.sms.platrole.domain.PlatRole;
import star.sms.platrole.domain.PlatRoleUser;
import star.sms.platrole.service.PlatRoleService;
import star.sms.platrole.service.PlatRoleUserService;

/**
 * @author star
 */
@Slf4j
@Controller
public class LoginController extends BaseController {
	@Resource
	private PlatRoleService platRoleService;
	@Resource
	private IpWhiteService ipWhiteService;
	
	@Resource
	private PlatRoleUserService platRoleUserService;

	@Resource
	private MenuInfoService menuInfoService;
	@Autowired
	private SystemConfig systemConifg;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(ModelMap model) {
		String ip = CusAccessObjectUtil.getClientIpAddress(request);
		if(ipWhiteService.isCanaccess(ip)) {
			model.addAttribute("isAdmin","0");
			if(systemConifg.getIsAdmin()) model.addAttribute("isAdmin","1");
			return "login";
		}else {
			return "notvisit";
		}
	}

	/**
	 * 到首页
	 * @param model
	 * @return
	 * @throws ParseException
	 */
	@ModuleCode(pValue = "home", value = "home")
	@RequestMapping("/home")
	public String home(ModelMap model) throws ParseException {
		LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// 查询用户的角色
		fillLoginUserRoleName(loginUser);
		request.getSession().setAttribute("loginUser", loginUser.getPlatManager());
		// 设置登录用户的菜单权限
		setLoginUserSessionMenu(loginUser);
		return "home";
	}
	
	@RequestMapping(value = "/notvisit", method = RequestMethod.GET)
	public String notvisit(ModelMap model) throws Exception{
			return "notvisit";
	}
	
	@ResponseBody
	@RequestMapping("/login/captcha")
	public Object captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SpecCaptcha specCaptcha = new SpecCaptcha(120, 60, 4);
		specCaptcha.setCharType(specCaptcha.TYPE_ONLY_NUMBER);
		String verCode = specCaptcha.text().toLowerCase();
		System.out.println("--生成的验证码-->" + verCode);
		HttpSession session = request.getSession(true);
		session.setAttribute("verCode", verCode);
		String base64 = specCaptcha.toBase64();
		return SUCCESS(base64);
	}
	
	/**
	 * 到首页
	 * @param model
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/")
	public String index(ModelMap model) throws ParseException {
		return "redirect:home";
	}
	
	/**
	 * 信息安全告知
	 * @param model
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/publicNotify")
	public String publicNotify(ModelMap model) throws ParseException {
		return "publicNotify";
	}
	
	private void fillLoginUserRoleName(LoginUser loginUser) {
		List<PlatRoleUser> platRoleUserList = platRoleUserService.findByUserId(loginUser.getPlatManager().getId());
		if (platRoleUserList != null && !platRoleUserList.isEmpty()) {
			PlatRole platRole = platRoleService.findOne(platRoleUserList.get(0).getRoleId());
			if (platRole != null) {
				loginUser.getPlatManager().setRoleName(platRole.getRoleName());
				loginUser.getPlatManager().setRoleCode(platRole.getRoleCode());
			}
		}
	}

	private void setLoginUserSessionMenu(LoginUser loginUser) {
		// 通过userId查询菜单权限
		List<MenuInfo> menuInfoList = menuInfoService.findByUserId(loginUser.getPlatManager().getId());
		
		// 整理出菜单层级（二级）
		List<MenuInfo> firstLevelMenuList = new LinkedList<MenuInfo>();
		Map<String, List<MenuInfo>> secondLevelMenuMap = new LinkedHashMap<String, List<MenuInfo>>();
		Map<String, List<MenuInfo>> thirdLevelMenuMap = new LinkedHashMap<String, List<MenuInfo>>();
		Map<String, Integer> code_menuIdMap = new HashMap<String, Integer>();
		Map<String, String> parentMenuCodeMap = new HashMap<String,String>();
		for (MenuInfo menuInfo: menuInfoList) {
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
			} else if (menuInfo.getMenuLevel() == 3) {
				List<MenuInfo> thirdLevelMenuList = thirdLevelMenuMap.get(menuInfo.getMenuParentId().toString());
				if (thirdLevelMenuList == null) {
					thirdLevelMenuList = new LinkedList<MenuInfo>();
					thirdLevelMenuList.add(menuInfo);
				} else {
					thirdLevelMenuList.add(menuInfo);
				}
				thirdLevelMenuMap.put(menuInfo.getMenuParentId().toString(), thirdLevelMenuList);
			}
			
			String menuCode = menuInfo.getMenuCode();
			String parentMenuCode = getParentMenuCode(menuInfoList, menuInfo.getMenuParentId());
			if(org.apache.commons.lang3.StringUtils.isNotEmpty(parentMenuCode)){
				parentMenuCodeMap.put(menuCode, parentMenuCode);
			}
			code_menuIdMap.put(menuCode, menuInfo.getMenuId());
		}
		
		
		request.getSession().setAttribute("firstLevelMenuList", firstLevelMenuList);
		request.getSession().setAttribute("secondLevelMenuMap", secondLevelMenuMap);
		request.getSession().setAttribute("thirdLevelMenuMap", thirdLevelMenuMap);
		request.getSession().setAttribute("parentMenuCodeMap", parentMenuCodeMap);
		request.getSession().setAttribute("code_menuIdMap", code_menuIdMap);
	}

	private String getParentMenuCode(List<MenuInfo> list,int pid) {
		String menuCode="";
		for(MenuInfo m:list) {
			if(m.getMenuId()==pid) {
				menuCode = m.getMenuCode();
				break;
			}
		}
		return menuCode;
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(ModelMap model,HttpServletRequest request) throws Exception{
		request.getSession().removeAttribute("loginUser");
		request.getSession().removeAttribute("firstLevelMenuList");
		request.getSession().removeAttribute("secondLevelMenuMap");
		request.getSession().removeAttribute("thirdLevelMenuMap");
		request.getSession().removeAttribute("parentMenuCodeMap");
		return "redirect:/login";
	}

	/**
	 * 设置左侧菜单body 折叠样式在session中存放、删除
	 * 
	 * @param request
	 * @param operation
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/setSessionSidebarCollapse", method = RequestMethod.POST)
	public Object setSessionSidebarCollapse(HttpSession session, String operation) {
		if (StringUtils.isNotEmpty(operation) && operation.equals("addClass")) {
			session.setAttribute("sidebar-collapse", "1");
		} else {
			session.removeAttribute("sidebar-collapse");
		}
		return SUCCESS();
	}
}
