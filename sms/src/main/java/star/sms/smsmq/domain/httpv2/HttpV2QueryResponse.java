package star.sms.smsmq.domain.httpv2;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * 上行接口
 * <?xml version="1.0" encoding="utf-8" ?>
 * <returnsms>
 * <callbox>
 *   <mobile>15023239810</mobile>-------------对应的手机号码
 *   <taskid>1212</taskid>-------------同一批任务ID
 *   <content>你好，我不需要</content>---------上行内容
 *   <receivetime>2011-12-02 22:12:11</receivetime>-------------接收时间
 *   <extno>01</extno>----子号，即自定义扩展号
 * </callbox>
 * <callbox>
 *   <mobile>15023239811</mobile>
 *   <taskid>1212</taskid>
 *   <content>你好，本次活动路线是怎么的</content>
 *   <receivetime>2011-12-02 22:12:11</receivetime>
 *   <extno>01</extno>
 * </callbox>
 * <errorstatus>
 *	  <error>1</error>-------------错误码
 *	  <remark>用户名或密码不能为空</remark>-------------错误描述
 * </errorstatus>
 * </returnsms>
 * @author
 */

@JacksonXmlRootElement(localName = "returnsms")
public class HttpV2QueryResponse implements Serializable {
	private static final long serialVersionUID = 1L;
    @JacksonXmlElementWrapper(localName = "callboxlist")
    private List<HttpV2Callbox> callboxList;
    @JacksonXmlElementWrapper(localName = "errorstatuslist")
	private List<HttpV2Errorstatus> errorstatusList;

	public List<HttpV2Callbox> getCallboxList() {
		return callboxList;
	}


	public void setCallboxList(List<HttpV2Callbox> callboxList) {
		this.callboxList = callboxList;
	}


	public List<HttpV2Errorstatus> getErrorstatusList() {
		return errorstatusList;
	}


	public void setErrorstatusList(List<HttpV2Errorstatus> errorstatusList) {
		this.errorstatusList = errorstatusList;
	}


	@Override
	public String toString() {
	    return "HttpV2SendResponse{" +
	            "callboxList='" + callboxList + "'" +
	            ", errorstatusList='" + errorstatusList + "'" +
	            "}";
	}
}
