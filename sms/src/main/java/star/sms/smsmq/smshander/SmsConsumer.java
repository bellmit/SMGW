package star.sms.smsmq.smshander;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import star.sms.config.SystemConfig;
import star.sms.smsmq.config.SmsmqConfig;

/**
 * 队列消费短信
 * 
 * @author star
 *
 */
@Service
public class SmsConsumer {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${rocketmq.consumer.namesrvAddr}")
	private String consumerNamesrvAddr;

	@Value("${rocketmq.consumer.groupName}")
	private String consumerGroupName;

	@Value("${rocketmq.consumer.consumeThreadMin}")
	private int consumeThreadMin;

	@Value("${rocketmq.consumer.consumeThreadMax}")
	private int consumeThreadMax;

	@Value("${rocketmq.consumer.topics}")
	private String topics;

	@Value("${rocketmq.consumer.consumeMessageBatchMaxSize}")
	private int consumeMessageBatchMaxSize;

	@Value("${rocketmq.consumer.reConsumerTimes}")
	private int reConsumerTimes;
	
	
	@Autowired
	private SmsConsumerHanlder smsConsumerHanlder;

    @Autowired
    private SystemConfig systemConfig;
	
	@PostConstruct
	public void consumer() throws Exception {
		if(!systemConfig.getIsTest()) {
			if(!systemConfig.getIsAdmin()) return;
		}
		for(int i=0;i<1;i++) {
			String instanceName="consumer"+i;
			logger.info(" 创建消费者实例："+ instanceName);
			DefaultMQPushConsumer defaultMQPushConsumer =null;
			if (this.consumerGroupName.isEmpty()) {
				throw new Exception("组名为空！");
			}
			if (this.consumerNamesrvAddr.isEmpty()) {
				throw new Exception("IP地址为空！");
			}
			if (this.topics.isEmpty()) {
				throw new Exception("Topic为空！");
			}
			try {
				defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroupName);
				defaultMQPushConsumer.setNamesrvAddr(consumerNamesrvAddr);
				defaultMQPushConsumer.setConsumeThreadMin(consumeThreadMin);
				defaultMQPushConsumer.setVipChannelEnabled(false);
				defaultMQPushConsumer.setConsumeThreadMax(consumeThreadMax);
				defaultMQPushConsumer.setInstanceName(instanceName);
				// 消费模式 集群还是广播，默认为集群(自动负载均衡)
				// 广播消费： 消息会发给Consume
				// Group中的每一个消费者进行消费,如果设置为广播消息会导致NOT_ONLINE异常
				defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);
				// 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
				// 如果非第一次启动，那么按照上次消费的位置继续消费
				defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
				// 设置一次消费消息的条数，默认为1条
				defaultMQPushConsumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);
				// 订阅topic
				for (int topicNum = 0; topicNum < 32; topicNum++) {
					defaultMQPushConsumer.subscribe(SmsmqConfig.SMS_SEND_SMPP+"_"+topicNum, SmsmqConfig.SMS_SEND_SMPP+"_"+topicNum);
					logger.info(" 创建订阅topic:"+ SmsmqConfig.SMS_SEND_SMPP+"_"+topicNum);
				}
				//短信消费者订阅多个topic,订阅32个用来区分不同通道
				for (int topicNum = 0; topicNum < 32; topicNum++) {
					defaultMQPushConsumer.subscribe(SmsmqConfig.SMS_SEND_HTTPV2+"_"+topicNum, SmsmqConfig.SMS_SEND_HTTPV2+"_"+topicNum);
					logger.info(" 创建订阅topic:"+ SmsmqConfig.SMS_SEND_HTTPV2+"_"+topicNum);
				}
				defaultMQPushConsumer.subscribe(SmsmqConfig.SMS_STAT_HTTPV2, SmsmqConfig.SMS_STAT_HTTPV2);
				logger.info(" 创建订阅topic:"+ SmsmqConfig.SMS_STAT_HTTPV2);
				
				//短信消费者订阅多个topic,订阅32个用来区分不同通道
				for (int topicNum = 0; topicNum < 32; topicNum++) {
					defaultMQPushConsumer.subscribe(SmsmqConfig.SMS_SEND_HTTPV3+"_"+topicNum, SmsmqConfig.SMS_SEND_HTTPV3+"_"+topicNum);
					logger.info(" 创建订阅topic:"+ SmsmqConfig.SMS_SEND_HTTPV3+"_"+topicNum);
				}
				defaultMQPushConsumer.subscribe(SmsmqConfig.SMS_STAT_HTTPV3, SmsmqConfig.SMS_STAT_HTTPV3);
				logger.info(" 创建订阅topic:"+ SmsmqConfig.SMS_STAT_HTTPV3);
	
				defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
					@SuppressWarnings("deprecation")
					@Override
					public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
							ConsumeConcurrentlyContext consumeconcurrentlycontext) {
						try {
							logger.info(" 客户端接收消息："+ instanceName +msgs.get(0).getTopic());
							if (msgs == null || msgs.isEmpty()) {
								// 接受到的消息为空，不处理，直接返回成功
								return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
							}
							MessageExt msg = msgs.get(0);
							// 获取该消息重试次数
							if (msg.getReconsumeTimes() >= reConsumerTimes) {
								// 消息已经重试了3次，如果不需要再次消费，则返回成功
								logger.error("消息重试消费失败：", msg);
								return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
							}
							//业务代码
							if (msg.getTopic().equals(SmsmqConfig.SMS_SEND_HTTP)) {
								smsConsumerHanlder.smsSendHttp(msg);
							} else if (msg.getTopic().equals(SmsmqConfig.SMS_STAT_HTTP)){
								smsConsumerHanlder.smsStatHttp(msg);
							} else if (msg.getTopic().startsWith(SmsmqConfig.SMS_SEND_SMPP+"_")){
								smsConsumerHanlder.smsSendSmpp(msg);
							} else if (msg.getTopic().startsWith(SmsmqConfig.SMS_SEND_HTTPV2+"_")){
								smsConsumerHanlder.smsSendHttpV2(msg);
							} else if (msg.getTopic().equals(SmsmqConfig.SMS_STAT_HTTPV2)){
								smsConsumerHanlder.smsStatHttpV2(msg);
							} else if (msg.getTopic().startsWith(SmsmqConfig.SMS_SEND_HTTPV3+"_")){
								smsConsumerHanlder.smsSendHttpV3(msg);
							} else if (msg.getTopic().equals(SmsmqConfig.SMS_STAT_HTTPV3)){
								smsConsumerHanlder.smsStatHttpV3(msg);
							}
						} catch (Exception e) {
							// 如果失败重试次数还没到三次则继续重试
							return ConsumeConcurrentlyStatus.RECONSUME_LATER;
						}
						return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
					}
				});
				defaultMQPushConsumer.start();
			} catch (Exception e) {
				logger.error("rocketMq Consumer start fail;{}", e);
			}
		}
	}
}
