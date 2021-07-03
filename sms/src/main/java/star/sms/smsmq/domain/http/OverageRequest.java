package star.sms.smsmq.domain.http;

/**
 * 余额查询请求
 * 
 * @author star
 *
 */
public class OverageRequest {
	//请求动作
	private String action;
	//account
	private String account;
	//password
	private String password;
	//响应数据类型
	private String rt;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRt() {
		return rt;
	}

	public void setRt(String rt) {
		this.rt = rt;
	}

}
