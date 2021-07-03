package star.sms._frame.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {
	private String verifyCode;
    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        verifyCode = request.getParameter("code");
    }

    public String getVerifyCode() {
        return verifyCode;
    }

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

}