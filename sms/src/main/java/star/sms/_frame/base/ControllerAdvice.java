package star.sms._frame.base;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import star.sms.config.SystemConfig;




/**
 * @author star
 */
@Component
@Aspect
public class ControllerAdvice {

	// private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private HttpServletRequest request;
	@Resource
	private SystemConfig systemConfig;
	
	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public void initCtx(){}
	@Before("initCtx()")
	public void beforeInitCtx(){
		String path = request.getContextPath();
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		request.setAttribute("ctx", basePath);
		request.setAttribute("version", systemConfig.getVersion());
	}
}
