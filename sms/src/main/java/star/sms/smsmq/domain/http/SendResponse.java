package star.sms.smsmq.domain.http;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 发送短信响应
 * 
 * @author star
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendResponse {
	// 参见statusMap
	@JsonProperty("status")
	private String status;
	// 当前账户余额，单位厘
	@JsonProperty("balance")
	private BigDecimal balance;
	// 电话列表信息
	@JsonProperty("list")
	private List<SendResponseMobile> list;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public List<SendResponseMobile> getList() {
		return list;
	}

	public void setList(List<SendResponseMobile> list) {
		this.list = list;
	}
}
