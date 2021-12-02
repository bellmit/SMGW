package star.sms.smsmq.smshander;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import star.sms._frame.utils.ProtostuffUtil;
import star.sms._frame.utils.UUIDUtils;
import star.sms.smsmq.config.SmsmqConfig;
import star.sms.smsmq.domain.http.ReportRequest;
import star.sms.smsmq.domain.http.SendRequest;
import star.sms.smsmq.domain.httpv2.HttpV2SendRequest;
import star.sms.smsmq.domain.httpv2.HttpV2StatusRequest;
import star.sms.smsmq.domain.httpv3.HttpV3SendRequest;
import star.sms.smsmq.domain.httpv3.HttpV3StatusRequest;
import star.sms.smsmq.domain.smpp.SendRequestSmpp;

/**
 * 生产者处理，只需要存到消息队列，不需要消息队列直接返回结果
 * 
 * @author star
 *
 */
@Service
public class SmsProducerHanlder {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DefaultMQProducer defaultMQProducer;

	/**
	 * 发送http短信,以批次进行传递
	 */
	@Deprecated
	public void smsSendHttp(SendRequest sendRequest) {
		try {
			Message msg = new Message(SmsmqConfig.SMS_SEND_HTTP, SmsmqConfig.SMS_SEND_HTTP, sendRequest.getBatchId(),
					ProtostuffUtil.serializer(sendRequest));
			defaultMQProducer.send(msg, new SendCallback() {
				@Override
				public void onSuccess(SendResult sendResult) {
					logger.info(sendResult.getMsgId()+"："+sendResult.getSendStatus().name());
				}

				@Override
				public void onException(Throwable throwable) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 得到http短信状态,以批次进行传递
	 */
	@Deprecated
	public void smsStatHttp(ReportRequest reportRequest) {
		try {
			Message msg = new Message(SmsmqConfig.SMS_STAT_HTTP, SmsmqConfig.SMS_STAT_HTTP, UUIDUtils.random(),
					ProtostuffUtil.serializer(reportRequest));
			defaultMQProducer.send(msg, new SendCallback() {
				@Override
				public void onSuccess(SendResult sendResult) {
					logger.info(sendResult.getMsgId()+"："+sendResult.getSendStatus().name());
				}

				@Override
				public void onException(Throwable throwable) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 发送httpV2短信,以批次进行传递,(SMS_SEND_HTTPV2_+通道id)的方式发送
	 */
	public void smsSendHttpV2(HttpV2SendRequest sendRequest) {
		try {
			Message msg = new Message(SmsmqConfig.SMS_SEND_HTTPV2+"_"+sendRequest.getAccountId()%32, SmsmqConfig.SMS_SEND_HTTPV2+"_"+sendRequest.getAccountId()%32, sendRequest.getBatchId(),
					ProtostuffUtil.serializer(sendRequest));
			defaultMQProducer.send(msg, new SendCallback() {
				@Override
				public void onSuccess(SendResult sendResult) {
					logger.info(sendResult.getMsgId()+"："+sendResult.getSendStatus().name());
				}

				@Override
				public void onException(Throwable throwable) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 得到httpV2短信状态,以批次进行传递
	 */
	public void smsStatHttpV2(HttpV2StatusRequest reportRequest) {
		try {
			Message msg = new Message(SmsmqConfig.SMS_STAT_HTTPV2, SmsmqConfig.SMS_STAT_HTTPV2, UUIDUtils.random(),
					ProtostuffUtil.serializer(reportRequest));
			defaultMQProducer.send(msg, new SendCallback() {
				@Override
				public void onSuccess(SendResult sendResult) {
					logger.info(sendResult.getMsgId()+"："+sendResult.getSendStatus().name());
				}

				@Override
				public void onException(Throwable throwable) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 发送httpV3短信,以批次进行传递,(SMS_SEND_HTTPV3_+通道id)的方式发送
	 */
	public void smsSendHttpV3(HttpV3SendRequest sendRequest) {
		try {
			Message msg = new Message(SmsmqConfig.SMS_SEND_HTTPV3+"_"+sendRequest.getAccountId()%32, SmsmqConfig.SMS_SEND_HTTPV3+"_"+sendRequest.getAccountId()%32, sendRequest.getBatchId(),
					ProtostuffUtil.serializer(sendRequest));
			defaultMQProducer.send(msg, new SendCallback() {
				@Override
				public void onSuccess(SendResult sendResult) {
					logger.info(sendResult.getMsgId()+"："+sendResult.getSendStatus().name());
				}

				@Override
				public void onException(Throwable throwable) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 得到httpV3短信状态,以批次进行传递
	 */
	public void smsStatHttpV3(HttpV3StatusRequest reportRequest) {
		try {
			Message msg = new Message(SmsmqConfig.SMS_STAT_HTTPV3, SmsmqConfig.SMS_STAT_HTTPV3, UUIDUtils.random(),
					ProtostuffUtil.serializer(reportRequest));
			defaultMQProducer.send(msg, new SendCallback() {
				@Override
				public void onSuccess(SendResult sendResult) {
					logger.info(sendResult.getMsgId()+"："+sendResult.getSendStatus().name());
				}

				@Override
				public void onException(Throwable throwable) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 发送Smpp短信,以批次进行传递
	 */
	public void smsSendSmpp(SendRequestSmpp sendRequestSmpp) {
		try {
			Message msg = new Message(SmsmqConfig.SMS_SEND_SMPP+"_"+sendRequestSmpp.getAccountId()%32, SmsmqConfig.SMS_SEND_SMPP+"_"+sendRequestSmpp.getAccountId()%32,  UUIDUtils.random(),
					ProtostuffUtil.serializer(sendRequestSmpp));
			defaultMQProducer.send(msg, new SendCallback() {
				@Override
				public void onSuccess(SendResult sendResult) {
					logger.info(sendResult.getMsgId()+"："+sendResult.getSendStatus().name());
				}

				@Override
				public void onException(Throwable throwable) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
