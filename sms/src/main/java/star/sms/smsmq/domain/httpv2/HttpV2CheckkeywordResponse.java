package star.sms.smsmq.domain.httpv2;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * 非法关键词查询
 * <?xml version="1.0" encoding="utf-8" ?>
 * <returnsms>
 * <message>message</message> ---------- 返回信息：见下表
 * </returnsms>
 * @author
 */

@JacksonXmlRootElement(localName = "returnsms")
public class HttpV2CheckkeywordResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	@JacksonXmlProperty(localName = "message")
	private String message;//返回信息：见下表
						//	用户名或密码不能为空:提交的用户名或密码为空
						//	用户名或密码错误:表示用户名或密码错误
						//	包含非法字符:检查出来包含非法关键词
						//	没有包含屏蔽词:未检查出非法关键词
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
	    return "HttpV2SendResponse{" +
	            "message='" + message + "'" +
	            "}";
	}
}
