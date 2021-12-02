package star.sms.smsmq.domain.httpv2;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * 发送接口
 * <?xml version="1.0" encoding="utf-8" ?>
 * <returnsms>
 * <returnstatus>status</returnstatus> ---------- 返回状态值：成功返回Success 失败返回：Faild
 * <message>message</message> ---------- 返回信息：见下表
 * <remainpoint> remainpoint</remainpoint> ---------- 返回余额
 * 
 * <taskID>taskID</taskID>  -----------  返回本次任务的序列ID
 * <successCounts>successCounts</successCounts> --成功短信数：当成功后返回提交成功短信数
 * </returnsms>
 * @author
 */

@JacksonXmlRootElement(localName = "returnsms")
public class HttpV2SendResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	@JacksonXmlProperty(localName = "returnstatus")
	private String returnstatus;//返回状态值：成功返回Success 失败返回：Faild
	@JacksonXmlProperty(localName = "message")
	private String message;//返回信息：见下表
						//	ok：提交成功
						//	用户名或密码不能为空:提交的用户名或密码为空
						//	发送内容包含sql注入字符:包含sql注入字符
						//	用户名或密码错误:表示用户名或密码错误
						//	短信号码不能为空:提交的被叫号码为空
						//	短信内容不能为空:发送内容为空
						//	包含非法字符：表示检查到不允许发送的非法字符
						//	对不起，您当前要发送的量大于您当前余额:当支付方式为预付费是，检查到账户余额不足
						//	其他错误:其他数据库操作方面的错误
	@JacksonXmlProperty(localName = "remainpoint")
	private String remainpoint;//返回余额
	@JacksonXmlProperty(localName = "taskID")
	private String taskID;//返回本次任务的序列ID
	@JacksonXmlProperty(localName = "successCounts")
	private String successCounts;//成功短信数：当成功后返回提交成功短信数
	
	public String getReturnstatus() {
		return returnstatus;
	}
	public void setReturnstatus(String returnstatus) {
		this.returnstatus = returnstatus;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getRemainpoint() {
		return remainpoint;
	}
	public void setRemainpoint(String remainpoint) {
		this.remainpoint = remainpoint;
	}
	public String getTaskID() {
		return taskID;
	}
	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}
	public String getSuccessCounts() {
		return successCounts;
	}
	public void setSuccessCounts(String successCounts) {
		this.successCounts = successCounts;
	}
	@Override
	public String toString() {
	    return "HttpV2SendResponse{" +
	            "returnstatus='" + returnstatus + "'" +
	            ", message='" + message + "'" +
	            ", remainpoint='" + remainpoint + "'" +
	            ", taskID='" + taskID + "'" +
	            ", successCounts='" + successCounts +"'" +
	            "}";
	}
}
