package star.sms.smsmq.domain.smpp;

import java.util.Map;
import com.google.common.util.concurrent.RateLimiter;

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
	//账号
	private String account;
	//账号id
	private String accountId;
	//短信id
	private Integer smsId;

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
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public Integer getSmsId() {
		return smsId;
	}
	public void setSmsId(Integer smsId) {
		this.smsId = smsId;
	}
}
