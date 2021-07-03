package star.sms.smsmq.domain.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 余额查询响应
 * 
 * @author star
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OverageResponse {
	// 请求结果
	@JsonProperty("status")
	private String status;
	// 当前账户余额，单位厘
	@JsonProperty("balance")
	private String balance;
	// 付费类型
	@JsonProperty("chargeType")
	private String chargeType;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getChargeType() {
		return chargeType;
	}

	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}

}
