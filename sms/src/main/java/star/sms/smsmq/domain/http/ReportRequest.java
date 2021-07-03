package star.sms.smsmq.domain.http;

/**
 * 状态报告
 * 
 * @author star
 *
 */
public class ReportRequest {
	// 请求动作
	private String action;
	// ip地址
	private String ip;
	// account
	private String account;
	// password
	private String password;
	// size
	private Integer size;
	// 响应数据类型
	private String rt;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
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

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getRt() {
		return rt;
	}

	public void setRt(String rt) {
		this.rt = rt;
	}

}
