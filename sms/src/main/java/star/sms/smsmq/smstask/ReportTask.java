package star.sms.smsmq.smstask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import star.sms.smsmq.domain.http.ReportRequest;
import star.sms.smsmq.domain.httpv2.HttpV2StatusRequest;
import star.sms.smsmq.domain.httpv3.HttpV3StatusRequest;
import star.sms.smsmq.smshander.SmsProducerHanlder;

/**
 * 短信状态接口，存放mq
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
	 * 短信发送http
	 * 
	 * @param reportRequest
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public void hanlderHttp(ReportRequest reportRequest) {
		try {
			smsProducerHanlder.smsStatHttp(reportRequest);
		} catch (Exception e) {
			logger.error("查询短信状态错误",e);
		}
	}
	/**
	 * 短信发送httpv2
	 * 
	 * @param sendRequest
	 * @return
	 */
	public void hanlderHttpV2(HttpV2StatusRequest reportRequest) {
		try {
			smsProducerHanlder.smsStatHttpV2(reportRequest);
		} catch (Exception e) {
			logger.error("查询短信状态错误",e);
		}
	}
	/**
	 * 短信发送httpv3
	 * 
	 * @param sendRequest
	 * @return
	 */
	public void hanlderHttpV3(HttpV3StatusRequest reportRequest) {
		try {
			smsProducerHanlder.smsStatHttpV3(reportRequest);
		} catch (Exception e) {
			logger.error("查询短信状态错误",e);
		}
	}
}
