package star.sms.smsmq.domain.httpv2;

import java.sql.Timestamp;
import java.util.List;


/**
 * HttpV2发送短信请求
 * @author star
 *
 */
public class HttpV2SendRequest {
	//任务id
	private Integer taskId;
	//批次id
	private String batchId;
	//接口account
	private Integer accountId;
	//接口account
	private String account;
	//号码系统标识
	private List<Integer> mobileIdList;
	//全部被叫号码
	private List<String> mobileList;
	//单条内容
	private String content;
	//内容或模板，和任务内容一样
	private String taskContent;
	//内容类型1单条群发，2点对点
	private Integer contentType;
	//定时发送格式(2010-10-24 09:08:10)
	private Timestamp sendTime;
	//响应数据类型
	private String rt;
	//操作人
	private Integer createUserId;
	//每次数量
	private Integer number;
	
	
	public Integer getTaskId() {
		return taskId;
	}
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public List<Integer> getMobileIdList() {
		return mobileIdList;
	}
	public void setMobileIdList(List<Integer> mobileIdList) {
		this.mobileIdList = mobileIdList;
	}
	public List<String> getMobileList() {
		return mobileList;
	}
	public void setMobileList(List<String> mobileList) {
		this.mobileList = mobileList;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTaskContent() {
		return taskContent;
	}
	public void setTaskContent(String taskContent) {
		this.taskContent = taskContent;
	}
	public Integer getContentType() {
		return contentType;
	}
	public void setContentType(Integer contentType) {
		this.contentType = contentType;
	}
	public Timestamp getSendTime() {
		return sendTime;
	}
	public void setSendTime(Timestamp sendTime) {
		this.sendTime = sendTime;
	}
	public String getRt() {
		return rt;
	}
	public void setRt(String rt) {
		this.rt = rt;
	}
	public Integer getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
}
