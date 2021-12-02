package star.sms.smsmq.smshander;

import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import star.sms._frame.utils.ProtostuffUtil;
import star.sms.smsmq.domain.http.ReportRequest;
import star.sms.smsmq.domain.http.SendRequest;
import star.sms.smsmq.domain.httpv2.HttpV2SendRequest;
import star.sms.smsmq.domain.httpv2.HttpV2StatusRequest;
import star.sms.smsmq.domain.httpv3.HttpV3SendRequest;
import star.sms.smsmq.domain.httpv3.HttpV3StatusRequest;
import star.sms.smsmq.domain.smpp.SendRequestSmpp;
import star.sms.smsmq.smstask.ReportSms;
import star.sms.smsmq.smstask.SendSms;

/**
 * 消费者处理，得到消息队列数据进行处理
 * @author star
 *
 */
@Service
public class SmsConsumerHanlder {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SendSms sendSms;
	
	@Autowired
	private ReportSms reportSms;
	
	/**
	 * 接受http发送消息,声明多个消费者
	 */
	@Deprecated
	public void smsSendHttp(MessageExt msg) {
		try {
			// 消息转换
			SendRequest sendRequest = ProtostuffUtil.deserializer(msg.getBody(), SendRequest.class);
			if (sendRequest != null) {
				// 发送短信
				sendSms.hanlderHttp(sendRequest);
			}
		} catch (Exception e) {
			logger.error("接受发送消费异常", e);
		}
	}
	/**
	 * 接受http状态
	 */
	@Deprecated
	public void smsStatHttp(MessageExt msg) {
		try {
			// 消息转换
			ReportRequest reportRequest = ProtostuffUtil.deserializer(msg.getBody(), ReportRequest.class);
			if (reportRequest != null) {
				// 得到状态
				reportSms.hanlderHttp(reportRequest);
			}
		} catch (Exception e) {
			logger.error("接受发送消费异常", e);
		}
	}
	/**
	 * 接受httpV2发送消息,声明多个消费者
	 */
	public void smsSendHttpV2(MessageExt msg) {
		try {
			// 消息转换
			HttpV2SendRequest sendRequest = ProtostuffUtil.deserializer(msg.getBody(), HttpV2SendRequest.class);
			if (sendRequest != null) {
				// 发送短信
				sendSms.hanlderHttpV2(sendRequest);
			}
		} catch (Exception e) {
			logger.error("接受发送消费异常", e);
		}
	}
	/**
	 * 接受httpV2状态
	 */
	public void smsStatHttpV2(MessageExt msg) {
		try {
			// 消息转换
			HttpV2StatusRequest reportRequest = ProtostuffUtil.deserializer(msg.getBody(), HttpV2StatusRequest.class);
			if (reportRequest != null) {
				// 得到状态
				reportSms.hanlderHttpV2(reportRequest);
			}
		} catch (Exception e) {
			logger.error("接受发送消费异常", e);
		}
	}
	/**
	 * 接受httpV3发送消息,声明多个消费者
	 */
	public void smsSendHttpV3(MessageExt msg) {
		try {
			// 消息转换
			HttpV3SendRequest sendRequest = ProtostuffUtil.deserializer(msg.getBody(), HttpV3SendRequest.class);
			if (sendRequest != null) {
				// 发送短信
				sendSms.hanlderHttpV3(sendRequest);
			}
		} catch (Exception e) {
			logger.error("接受发送消费异常", e);
		}
	}
	/**
	 * 接受httpV3状态
	 */
	public void smsStatHttpV3(MessageExt msg) {
		try {
			// 消息转换
			HttpV3StatusRequest reportRequest = ProtostuffUtil.deserializer(msg.getBody(), HttpV3StatusRequest.class);
			if (reportRequest != null) {
				// 得到状态
				reportSms.hanlderHttpV3(reportRequest);
			}
		} catch (Exception e) {
			logger.error("接受发送消费异常", e);
		}
	}
	/**
	 * 接受smpp发送消息
	 */
	public void smsSendSmpp(MessageExt msg) {
		try {
			SendRequestSmpp sendRequestSmpp = ProtostuffUtil.deserializer(msg.getBody(), SendRequestSmpp.class);
			if (sendRequestSmpp != null) {
				// 得到状态
				sendSms.hanlderSmpp(sendRequestSmpp);
			}
		} catch (Exception e) {
			logger.error("接受发送消费异常", e);
		}
	}
}
