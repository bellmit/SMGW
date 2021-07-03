package star.sms.smsmq.smstask;



import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import star.sms._frame.utils.JsonUtil;
import star.sms.sms.service.SmsService;
import star.sms.smsmq.domain.http.ReportRequest;
import star.sms.smsmq.domain.http.ReportResponse;
import star.sms.smsmq.httputils.HttpConnectionUtil;

/**
 * 状态报告接口
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
						smsService.updateSmsReport(reportResponse);
					}
				}
			}
		} catch (Exception e) {
			logger.error("请求状态报告错误",e);
		}
	}
}
