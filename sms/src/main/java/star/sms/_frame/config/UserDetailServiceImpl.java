package star.sms._frame.config;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import star.sms.config.SystemConfig;
import star.sms.operation_log.service.DataOperationLogService;
import star.sms.platmanager.domain.PlatManager;
import star.sms.platmanager.service.PlatManagerService;
import star.sms.platrole.domain.PlatRole;
import star.sms.platrole.domain.PlatRoleUser;
import star.sms.platrole.service.PlatRoleService;
import star.sms.platrole.service.PlatRoleUserService;

/**
 * @author star
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	private PlatManagerService platManagerService;
	@Resource
	private DataOperationLogService dataOperationLogService;
	@Resource
	private PlatRoleUserService platRoleUserService;
	@Resource
	private PlatRoleService platRoleService;
	@Resource
	private SystemConfig systemConfig;
	
	@Override
	public UserDetails loadUserByUsername(String arg0) throws UsernameNotFoundException {
		PlatManager user = platManagerService.findByLoginName(arg0.trim());
		if (user == null)
			throw new UsernameNotFoundException("username not found");
		List<PlatRoleUser> platRoleUserList = platRoleUserService.findByUserId(user.getId());
		if (platRoleUserList != null && !platRoleUserList.isEmpty()) {
			PlatRole platRole = platRoleService.findOne(platRoleUserList.get(0).getRoleId());
			if (platRole != null) {
				if((systemConfig.getIsAdmin()&&platRole.getRoleCode().equals("ADMIN")) || (!systemConfig.getIsAdmin()&&!platRole.getRoleCode().equals("ADMIN"))) {
					//更新最后登录时间
					user.setLastLoginTime(new Date());
					platManagerService.save(user);
					LoginUser loginUser = new LoginUser(user);
					return loginUser;
				}
			}
		}
		throw new UsernameNotFoundException("username not found");
	}

}
