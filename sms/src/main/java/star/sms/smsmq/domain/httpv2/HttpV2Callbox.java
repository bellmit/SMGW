package star.sms.smsmq.domain.httpv2;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * <callbox>
 *   <mobile>15023239810</mobile>-------------对应的手机号码
 *   <taskid>1212</taskid>-------------同一批任务ID
 *   <content>你好，我不需要</content>---------上行内容
 *   <receivetime>2011-12-02 22:12:11</receivetime>-------------接收时间
 *   <extno>01</extno>----子号，即自定义扩展号
 * </callbox>
 * @author
 */
@JacksonXmlRootElement(localName = "statusbox")
public class HttpV2Callbox implements Serializable {
	private static final long serialVersionUID = 1L;
	@JacksonXmlProperty(localName = "mobile")
	private String mobile;//对应的手机号码
	@JacksonXmlProperty(localName = "taskid")
	private String taskid;//同一批任务ID
	@JacksonXmlProperty(localName = "content")
	private String content;//上行内容
	@JacksonXmlProperty(localName = "receivetime")
	private String receivetime;//接收时间
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


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getReceivetime() {
		return receivetime;
	}


	public void setReceivetime(String receivetime) {
		this.receivetime = receivetime;
	}


	public String getExtno() {
		return extno;
	}


	public void setExtno(String extno) {
		this.extno = extno;
	}


	@Override
    public String toString() {
        return "Callbox{" +
                "mobile='" + mobile + "'" +
                ", taskid='" + taskid + "'" +
                ", content='" + content + "'" +
                ", receivetime='" + receivetime + "'" +
                ", extno='" + extno + "'" +
                "}";
    }
}
