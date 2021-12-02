package star.sms.smsmq.domain.smpp;

/**
 * 发送短信请求
 * @author star
 *
 */
public class SendRequestSmpp {
	//手机号码
	private String mobile;
	//内容
	private String content;
	//account
	private String account;
	//账号id
	private Integer accountId;
	//短信id
	private Integer smsId;
	//操作人,发送用户
	private Integer createUserId;
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public Integer getSmsId() {
		return smsId;
	}
	public void setSmsId(Integer smsId) {
		this.smsId = smsId;
	}
	public Integer getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}
}
