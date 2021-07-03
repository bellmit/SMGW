package star.sms.smsmq.domain.http;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 状态报告响应
 * 
 * @author star
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportResponse {
	// 请求结果
	@JsonProperty("status")
	private String status;
	// 当前账户余额，单位厘
	@JsonProperty("balance")
	private String balance;
	// 电话列表信息
	@JsonProperty("list")
	private List<ReportResponseMobile> list;

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

	public List<ReportResponseMobile> getList() {
		return list;
	}

	public void setList(List<ReportResponseMobile> list) {
		this.list = list;
	}
}
