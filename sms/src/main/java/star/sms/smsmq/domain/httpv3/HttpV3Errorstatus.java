package star.sms.smsmq.domain.httpv3;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * <errorstatus>
 *<error>1</error>-------------错误码
 *<remark>用户名或密码不能为空</remark>-------------错误描述
 *</errorstatus>
 * @author
 */
@JacksonXmlRootElement(localName = "errorstatus")
public class HttpV3Errorstatus implements Serializable {
	private static final long serialVersionUID = 1L;
	@JacksonXmlProperty(localName = "error")
	private String error;//错误码
						//	1：用户名或密码不能为空
						//	2：用户名或密码错误
						//	3：该用户不允许查看状态报告
						//	4：参数不正确
	@JacksonXmlProperty(localName = "remark")
	private String remark;//错误描述
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
    @Override
    public String toString() {
        return "Errorstatus{" +
                "error='" + error + "'" +
                ", remark='" + remark + "'" +
                "}";
    }
}
