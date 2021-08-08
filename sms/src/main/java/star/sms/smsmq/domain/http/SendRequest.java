package star.sms.smsmq.domain.http;

import java.util.List;

/**
 * 发送短信请求
 * @author star
 *
 */
public class SendRequest {
	//任务id
	private Integer taskId;
	//批次id
	private String batchId;
	//请求动作
	private String action;
	//ip地址
	private String ip;
	//account
	private String account;
	//password
	private String password;
	//号码系统标识
	private List<Integer> mobileIdList;
	//全部被叫号码
	private List<String> mobileList;
	//单条内容
	private List<String> contentList;
	//内容或模板，和任务内容一样
	private String taskContent;
	//内容或模板，和任务内容一样
	private Integer contentType;
	//接入号
	private String extno;
	//响应数据类型
	private String rt;
	//操作人
	private Integer createUserId;
	
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
	public List<String> getContentList() {
		return contentList;
	}
	public void setContentList(List<String> contentList) {
		this.contentList = contentList;
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
	public String getExtno() {
		return extno;
	}
	public void setExtno(String extno) {
		this.extno = extno;
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
}
