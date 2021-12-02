package star.sms.smsmq.domain.httpv3;

/**
 * 余额查询请求
 * 
 * @author star
 *
 */
public class HttpV3OverageRequest {
	//ip地址
	private String ip;
	//请求动作
	private String action;
	//account
	private String account;
	//password
	private String password;
	//响应数据类型
	private String rt;
	// userid
	private String userid;


	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

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

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	

}
