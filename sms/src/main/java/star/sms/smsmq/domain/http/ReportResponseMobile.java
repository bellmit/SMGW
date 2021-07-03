package star.sms.smsmq.domain.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 状态报告响应
 * 
 * @author star
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportResponseMobile {
	// 0=手机用户上行 1=状态报告
	@JsonProperty("flag")
	private Integer flag;
	// 消息ID
	@JsonProperty("mid")
	private String mid;
	// 账号
	@JsonProperty("spid")
	private String spid;
	// 接入码
	@JsonProperty("accessCode")
	private String accessCode;
	// 手机号
	@JsonProperty("mobile")
	private String mobile;
	// 参见statMap状态报告代码,1的情况下起作用
	@JsonProperty("stat")
	private String stat;
	
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getSpid() {
		return spid;
	}
	public void setSpid(String spid) {
		this.spid = spid;
	}
	public String getAccessCode() {
		return accessCode;
	}
	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
}
