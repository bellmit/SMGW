package star.sms._frame.base;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.google.gson.Gson;

import star.sms.config.SystemConfig;
import star.sms.platgroup.service.PlatGroupUserService;
import star.sms.platmanager.domain.PlatManager;
import star.sms.platmanager.service.PlatManagerService;
import star.sms.platrole.service.PlatRoleUserService;

/**
 * @author star
 */
public class BaseController {
	private String AJAX_HEADER = "X-Requested-With";
	
	@Resource
	protected HttpServletRequest request;
	
	@Resource
	protected   HttpServletResponse response;
	
	@Resource
	private PlatRoleUserService platRoleUserService;
	
	@Resource
	private PlatGroupUserService platGroupUserService;
	
	@Resource
	private PlatManagerService platManagerService;
	
	/**
	 * 获取当前登录人
	 * @return
	 */
	protected PlatManager getLoginUser() {
		PlatManager lu = (PlatManager) request.getSession().getAttribute("loginUser");
		return lu;
	}
	
	/**
	 * 区分登录人是代理商，还是运营中心部门的人员，还是总部及下级部门 的人员
	 * @return 1:代理商角色,2:运营中心部门,3:总部或总部下级部门
	 */
	protected int diffLoginUser() {
		PlatManager loginUser = this.getLoginUser();
		
		// 判断登录人是不是总部或总部下级部门， 若是 返回result = 3 
		boolean beglongHeadQuarters = platGroupUserService.checkBelongHeadQuartersByUserId(loginUser.getId());
		if (beglongHeadQuarters) {
			return 3;
		}
		
		throw new RuntimeException(String.format("人员ID %s 既不是代理商，也不属于运营中心部门，也不属于总部", loginUser.getId()));
	}
	
	@ExceptionHandler(value = Exception.class)
	public String handleException(Exception e) throws ServletException, IOException{
		e.printStackTrace();
		String ajaxHeader = request.getHeader(AJAX_HEADER);
		if(StringUtils.isEmpty(ajaxHeader)){
			return "/error";
		}else{
			response.getWriter().write(new Gson().toJson(ERROR("服务异常")));
			response.getWriter().flush();
		}
		return null;
	}
	
	public Object SUCCESS(Object data){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("result", "success");
		result.put("data", data);
		return result;
	}
	
	public Object SUCCESS(){
		return SUCCESS(null);
	}
	
	public Object ERROR(String desc){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("result", "fail");
		result.put("data", desc);
		return result;
	}
	
}
