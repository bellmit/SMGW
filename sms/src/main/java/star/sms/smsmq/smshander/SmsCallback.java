package star.sms.smsmq.smshander;

import org.apache.rocketmq.common.message.MessageExt;

/**
 * 短信消费者回调
 * 
 * @author star
 *
 */
public interface SmsCallback {

	public void success(MessageExt msg);
}
