package star.sms.smsmq.domain.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 发送短信响应
 * 
 * @author star
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendResponseMobile {
	// 消息ID
	@JsonProperty("mid")
	private String mid;
	// 手机号
	@JsonProperty("mobile")
	private String mobile;
	// 参见resultMap
	@JsonProperty("result")
	private int result;

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
