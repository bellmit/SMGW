package star.sms.smsmq.smstask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import star.sms.smsmq.domain.http.ReportRequest;
import star.sms.smsmq.smshander.SmsProducerHanlder;

/**
 * 短信发送接口
 * 
 * @author star
 *
 */
@Service
public class ReportTask {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private SmsProducerHanlder smsProducerHanlder;

	/**
	 * 短信发送
	 * 
	 * @param sendRequest
	 * @return
	 */
	public void hanlderHttp(ReportRequest reportRequest) {
		try {
			smsProducerHanlder.smsStatHttp(reportRequest);
		} catch (Exception e) {
			logger.error("查询短信状态错误",e);
		}
	}
}
