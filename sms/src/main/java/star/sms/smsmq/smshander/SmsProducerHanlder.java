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
	 * 得到短信状态,以批次进行传递
	 */
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
	 * 发送Smpp短信,以批次进行传递
	 */
	public void smsSendSmpp(SendRequestSmpp sendRequestSmpp) {
		try {
			Message msg = new Message(SmsmqConfig.SMS_SEND_SMPP, SmsmqConfig.SMS_SEND_SMPP,  UUIDUtils.random(),
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
