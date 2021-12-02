package star.sms.smsmq.smstask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import star.sms._frame.utils.JsonUtil;
import star.sms._frame.utils.MD5;
import star.sms.sms.service.SmsService;
import star.sms.smsmq.domain.http.ReportRequest;
import star.sms.smsmq.domain.http.ReportResponse;
import star.sms.smsmq.domain.httpv2.HttpV2StatusRequest;
import star.sms.smsmq.domain.httpv2.HttpV2StatusResponse;
import star.sms.smsmq.domain.httpv3.HttpV3StatusRequest;
import star.sms.smsmq.domain.httpv3.HttpV3StatusResponse;
import star.sms.smsmq.httputils.HttpConnectionUtil;

/**
 * 接收状态查询,并发送
 * 
 * @author star
 *
 */
@Service
public class ReportSms {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	private HttpConnectionUtil httpConnectionUtil;
	
    @Autowired
    private SmsService smsService;

	/**
	 * 请求状态报告
	 * 
	 * @param reportRequest
	 */
    @Deprecated
	public void hanlderHttp(ReportRequest reportRequest) {
		try {
			if(reportRequest!=null) {
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("action", "report");
				paramsMap.put("account", reportRequest.getAccount());
				paramsMap.put("password", reportRequest.getPassword());
				paramsMap.put("size", reportRequest.getSize()+"");
				paramsMap.put("rt", "json");
	
				String json = httpConnectionUtil.postSync(reportRequest.getIp(), paramsMap);
				if (json != null) {
					ReportResponse reportResponse = JsonUtil.string2Obj(json, ReportResponse.class);
					if(reportResponse!=null) {
						smsService.updateSmsReportHttp(reportResponse);
					}
				}
			}
		} catch (Exception e) {
			logger.error("请求状态报告错误",e);
		}
	}
	/**
	 * 请求状态报告
	 * 
	 * @param reportRequest
	 */
	public void hanlderHttpV2(HttpV2StatusRequest reportRequest) {
		try {
			if(reportRequest!=null) {
				//构造查询参数
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("userid", reportRequest.getUserid());	
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String time = sdf.format(new Date());
				paramsMap.put("timestamp", time);
				String account = reportRequest.getAccount()==null?"":reportRequest.getAccount();
				String password = reportRequest.getPassword()==null?"":reportRequest.getPassword();
				String sign= MD5.encode(account+password+time);
				paramsMap.put("sign",sign );
				paramsMap.put("action", "query");
				paramsMap.put("statusNum", reportRequest.getSize()+"");
	
				String xml = httpConnectionUtil.postSync(reportRequest.getIp()+"v2statusApi.aspx", paramsMap);
				if (xml != null) {
					ObjectMapper mapper = new XmlMapper();
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					/**格式化状态查询xml start**/
					if(xml.indexOf("<statusbox>")>0) xml = xml.substring(0,xml.indexOf("<statusbox>"))+"<statusboxlist>"+xml.substring(xml.indexOf("<statusbox>"));
					if(xml.lastIndexOf("</statusbox>")>0) xml = xml.substring(0,xml.lastIndexOf("</statusbox>")+12)+"</statusboxlist>"+xml.substring(xml.lastIndexOf("</statusbox>")+12);
					if(xml.indexOf("<errorstatus>")>0) xml = xml.substring(0,xml.indexOf("<errorstatus>"))+"<errorstatuslist>"+xml.substring(xml.indexOf("<errorstatus>"));
					if(xml.lastIndexOf("</errorstatus>")>0) xml = xml.substring(0,xml.lastIndexOf("</errorstatus>")+14)+"</errorstatuslist>"+xml.substring(xml.lastIndexOf("</errorstatus>")+14);
					/**格式化状态查询xml end**/
					HttpV2StatusResponse reportResponse = mapper.readValue(xml, HttpV2StatusResponse.class);
					if(reportResponse!=null) {
						smsService.updateSmsReportHttpV2(reportResponse);
					}
				}
			}
		} catch (Exception e) {
			logger.error("请求httpv2状态报告错误",e);
		}
	}
	/**
	 * 请求状态报告
	 * 
	 * @param reportRequest
	 */
	public void hanlderHttpV3(HttpV3StatusRequest reportRequest) {
		try {
			if(reportRequest!=null) {
				//构造查询参数
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("userid", reportRequest.getUserid());	
				String account = reportRequest.getAccount()==null?"":reportRequest.getAccount();
				paramsMap.put("account", account);
				String password = reportRequest.getPassword()==null?"":reportRequest.getPassword();
				paramsMap.put("password", password);
				paramsMap.put("action", "query");
				paramsMap.put("statusNum", reportRequest.getSize()+"");
	
				String xml = httpConnectionUtil.postSync(reportRequest.getIp()+"statusApi.aspx", paramsMap);
				if (xml != null) {
					ObjectMapper mapper = new XmlMapper();
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					/**格式化状态查询xml start**/
					if(xml.indexOf("<statusbox>")>0) xml = xml.substring(0,xml.indexOf("<statusbox>"))+"<statusboxlist>"+xml.substring(xml.indexOf("<statusbox>"));
					if(xml.lastIndexOf("</statusbox>")>0) xml = xml.substring(0,xml.lastIndexOf("</statusbox>")+12)+"</statusboxlist>"+xml.substring(xml.lastIndexOf("</statusbox>")+12);
					if(xml.indexOf("<errorstatus>")>0) xml = xml.substring(0,xml.indexOf("<errorstatus>"))+"<errorstatuslist>"+xml.substring(xml.indexOf("<errorstatus>"));
					if(xml.lastIndexOf("</errorstatus>")>0) xml = xml.substring(0,xml.lastIndexOf("</errorstatus>")+14)+"</errorstatuslist>"+xml.substring(xml.lastIndexOf("</errorstatus>")+14);
					/**格式化状态查询xml end**/
					HttpV3StatusResponse reportResponse = mapper.readValue(xml, HttpV3StatusResponse.class);
					if(reportResponse!=null) {
						smsService.updateSmsReportHttpV3(reportResponse);
					}
				}
			}
		} catch (Exception e) {
			logger.error("请求httpv3状态报告错误",e);
		}
	}
}
