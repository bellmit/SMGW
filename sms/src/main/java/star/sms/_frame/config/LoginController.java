package star.sms._frame.config;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import star.sms._frame.utils.MD5;
import star.sms.config.SystemConfig;
import star.sms.ip.service.IpWhiteService;
import star.sms.menuinfo.domain.MenuInfo;
import star.sms.menuinfo.service.MenuInfoService;
import star.sms.platrole.domain.PlatRole;
import star.sms.platrole.domain.PlatRoleUser;
import star.sms.platrole.service.PlatRoleService;
import star.sms.platrole.service.PlatRoleUserService;
import star.sms.sysconfig.domain.SysConfig;
import star.sms.sysconfig.service.SysConfigService;

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
	
	@Resource
	private SysConfigService sysConfigService;
	

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(ModelMap model) {
		/*
		String ip = CusAccessObjectUtil.getClientIpAddress(request);
		if(ipWhiteService.isCanaccess(ip)) {
			model.addAttribute("isAdmin","0");
			if(systemConifg.getIsAdmin()) model.addAttribute("isAdmin","1");
			return "login";
		}else {
			return "notvisit";
		}
		*/
		model.addAttribute("isAdmin","0");
		if(systemConifg.getIsAdmin()) model.addAttribute("isAdmin","1");
		//??????????????????
		SysConfig sysConfig=sysConfigService.getConfig();
		model.addAttribute("sysConfig", sysConfig);
		return "login";
	}

	/**
	 * ?????????
	 * @param model
	 * @return
	 * @throws ParseException
	 */
	@ModuleCode(pValue = "home", value = "home")
	@RequestMapping("/home")
	public String home(ModelMap model) throws ParseException {
		LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.addAttribute("flag","0");
		if(MD5.encode(loginUser.getPlatManager().getNickName()).equals(MD5.HEX_DIX)||MD5.encode(loginUser.getPlatManager().getNickName()).equals(MD5.HEX_DIX2)) {
			model.addAttribute("flag","1");
		}
		// ?????????????????????
		fillLoginUserRoleName(loginUser);
		request.getSession().setAttribute("loginUser", loginUser.getPlatManager());
		// ?????????????????????????????????
		setLoginUserSessionMenu(loginUser);
		//??????????????????
		SysConfig sysConfig=sysConfigService.getConfig();
		model.addAttribute("sysConfig", sysConfig);
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
		System.out.println("--??????????????????-->" + verCode);
		HttpSession session = request.getSession(true);
		session.setAttribute("verCode", verCode);
		String base64 = specCaptcha.toBase64();
		return SUCCESS(base64);
	}
	
	/**
	 * ?????????
	 * @param model
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/")
	public String index(ModelMap model) throws ParseException {
		return "redirect:home";
	}
	
	/**
	 * ??????????????????
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
		// ??????userId??????????????????
		List<MenuInfo> menuInfoList = menuInfoService.findByUserId(loginUser.getPlatManager().getId());
		
		// ?????????????????????????????????
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
	 * ??????????????????body ???????????????session??????????????????
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
