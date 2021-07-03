package star.sms._frame.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import star.sms._frame.base.Constant;
import star.sms.platmanager.domain.PlatManager;

/**
 * @author star
 */
public class LoginUser implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7679225667955985734L;
	
	private PlatManager platManager;
	
	
	public LoginUser(PlatManager platManager) {
		super();
		this.platManager = platManager;
	}

	public PlatManager getPlatManager() {
		return platManager;
	}

	public void setPlatManager(PlatManager platManager) {
		this.platManager = platManager;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getPassword() {
		return platManager==null?null:platManager.getPassword();
	}

	@Override
	public String getUsername() {
		return platManager==null?null:platManager.getLoginName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return platManager.getState() == Constant.PLAT_MANAGER_STATUS_ENABLE;
	}

}
