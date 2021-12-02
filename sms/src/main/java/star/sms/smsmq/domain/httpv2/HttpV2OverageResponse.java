package star.sms.smsmq.domain.httpv2;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * 余额及已发送量查询接口
 * <?xml version="1.0" encoding="utf-8" ?>
 * <returnsms>
 * <returnstatus>status</returnstatus>-------返回状态值：成功返回Sucess (注意：此处是Sucess，和其它接口返回的Success不同)失败返回：Faild
 * <message>message</message>--------------返回信息提示：见下表
 * <payinfo>payinfo</payinfo>--------------返回支付方式  后付费，预付费
 * <overage>overage</overage>-------------返回余额
 * <sendTotal>sendTotal</sendTotal>----返回总点数  当支付方式为预付费是返回总充值点数
 * </returnsms>
 * @author
 */

@JacksonXmlRootElement(localName = "returnsms")
public class HttpV2OverageResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	@JacksonXmlProperty(localName = "returnstatus")
	private String returnstatus;//返回状态值：成功返回Sucess (注意：此处是Sucess，和其它接口返回的Success不同)失败返回：Faild
	@JacksonXmlProperty(localName = "message")
	private String message;//返回信息：见下表
						//	返回空：查询成功,将返回相应的支付方式、账户使用条数、总充值点数
						//	用户名或密码不能为空:提交的用户名或密码为空
						//	用户名或密码错误:表示用户名或密码错误
	@JacksonXmlProperty(localName = "payinfo")
	private String payinfo;//返回支付方式  后付费，预付费
	@JacksonXmlProperty(localName = "overage")
	private String overage;//返回余额
	@JacksonXmlProperty(localName = "sendTotal")
	private String sendTotal;//返回总点数  当支付方式为预付费是返回总充值点数
	
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

	public String getPayinfo() {
		return payinfo;
	}

	public void setPayinfo(String payinfo) {
		this.payinfo = payinfo;
	}

	public String getOverage() {
		return overage;
	}

	public void setOverage(String overage) {
		this.overage = overage;
	}

	public String getSendTotal() {
		return sendTotal;
	}

	public void setSendTotal(String sendTotal) {
		this.sendTotal = sendTotal;
	}

	@Override
	public String toString() {
	    return "HttpV2SendResponse{" +
	            "returnstatus='" + returnstatus + "'" +
	            ", message='" + message + "'" +
	            ", payinfo='" + payinfo + "'" +
	            ", overage='" + overage + "'" +
	            ", sendTotal='" + sendTotal +"'" +
	            "}";
	}
}
