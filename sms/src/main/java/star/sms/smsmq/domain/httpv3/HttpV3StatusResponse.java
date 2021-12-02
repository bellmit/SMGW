package star.sms.smsmq.domain.httpv3;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * 状态报告接口
 * <?xml version="1.0" encoding="utf-8" ?>
 * <returnsms>
 * <statusbox>
 * 	  <mobile>15023239810</mobile>-------------对应的手机号码
 * 	  <taskid>1212</taskid>-------------同一批任务ID
 * 	  <status>10</status>---------状态报告----10：发送成功，20：发送失败
 * 	  <receivetime>2011-12-02 22:12:11</receivetime>-------------接收时间
 * 	  <errorcode>DELIVRD</errorcode>-上级网关返回值，不同网关返回值不同，仅作为参考
 * 	  <extno>01</extno>--子号，即自定义扩展号
 * </statusbox>
 * <statusbox>
 * 	  <mobile>15023239811</mobile>
 * 	  <taskid>1212</taskid>
 * 	  <status>20</status>
 * 	  <receivetime>2011-12-02 22:12:11</receivetime>
 * 	  <errorcode>2</errorcode>
 * 	  <extno></extno>
 * </statusbox>
 * <errorstatus>
 *	  <error>1</error>-------------错误码
 *	  <remark>用户名或密码不能为空</remark>-------------错误描述
 * </errorstatus>
 * </returnsms>
 * @author
 */

@JacksonXmlRootElement(localName = "returnsms")
public class HttpV3StatusResponse implements Serializable {
	private static final long serialVersionUID = 1L;
    @JacksonXmlElementWrapper(localName = "statusboxlist")
    private List<HttpV3Statusbox> statusboxList;
    @JacksonXmlElementWrapper(localName = "errorstatuslist")
	private List<HttpV3Errorstatus> errorstatusList;

	public List<HttpV3Statusbox> getStatusboxList() {
		return statusboxList;
	}

	public void setStatusboxList(List<HttpV3Statusbox> statusboxList) {
		this.statusboxList = statusboxList;
	}

	public List<HttpV3Errorstatus> getErrorstatusList() {
		return errorstatusList;
	}

	public void setErrorstatusList(List<HttpV3Errorstatus> errorstatusList) {
		this.errorstatusList = errorstatusList;
	}

	@Override
	public String toString() {
	    return "HttpV3SendResponse{" +
	            "statusboxList='" + statusboxList + "'" +
	            ", errorstatusList='" + errorstatusList + "'" +
	            "}";
	}
}
