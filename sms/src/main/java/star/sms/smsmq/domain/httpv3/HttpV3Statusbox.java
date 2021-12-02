package star.sms.smsmq.domain.httpv3;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * <statusbox>
 * <mobile>15023239811</mobile>
 * <taskid>1212</taskid>
 * <status>20</status>
 * <receivetime>2011-12-02 22:12:11</receivetime>
 * <errorcode>2</errorcode>
 * <extno></extno>
 * </statusbox>
 * @author
 */
@JacksonXmlRootElement(localName = "statusbox")
public class HttpV3Statusbox implements Serializable {
	private static final long serialVersionUID = 1L;
	@JacksonXmlProperty(localName = "mobile")
	private String mobile;//对应的手机号码
	@JacksonXmlProperty(localName = "taskid")
	private String taskid;//同一批任务ID
	@JacksonXmlProperty(localName = "status")
	private String status;//状态报告----10：发送成功，20：发送失败
	@JacksonXmlProperty(localName = "receivetime")
	private String receivetime;//接收时间
	@JacksonXmlProperty(localName = "errorcode")
	private String errorcode;//上级网关返回值，不同网关返回值不同，仅作为参考
	@JacksonXmlProperty(localName = "extno")
	private String extno;//子号，即自定义扩展号
	
    public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReceivetime() {
		return receivetime;
	}

	public void setReceivetime(String receivetime) {
		this.receivetime = receivetime;
	}

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	public String getExtno() {
		return extno;
	}

	public void setExtno(String extno) {
		this.extno = extno;
	}

	@Override
    public String toString() {
        return "Errorstatus3{" +
                "mobile='" + mobile + "'" +
                ", taskid='" + taskid + "'" +
                ", status='" + status + "'" +
                ", receivetime='" + receivetime + "'" +
                ", errorcode='" + errorcode + "'" +
                ", extno='" + extno + "'" +
                "}";
    }
}
