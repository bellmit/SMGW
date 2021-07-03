package star.sms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author star
 */
@Component
public class SystemConfig {
	
	@Value(value = "${version}")
	private String version;
	
	@Value(value = "${tempdir}")
	private String tempdir;
	
	@Value("${isAdmin}")
    private Boolean isAdmin;

	public String getTempdir() {
		return tempdir;
	}

	public void setTempdir(String tempdir) {
		this.tempdir = tempdir;
	}
	

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
}
