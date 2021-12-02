package star.sms.smsmq.smshander;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import star.sms.config.SystemConfig;

/**
 * 短信发送生产者
 * star
 */
@Configuration
public class SmsProducer {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 组名
     */
    @Value("${rocketmq.producer.groupName}")
    private String producerGroupName;
    /**
     * 服务器地址
     */
    @Value("${rocketmq.producer.namesrvAddr}")
    private String producerNamesrvAddr;
    /**
     * 消息最大大小，默认4M
     */
    @Value("${rocketmq.producer.maxMessageSize}")
    private int maxMessageSize;
    /**
     * 消息发送超时时间，默认3秒
     */
    @Value("${rocketmq.producer.sendMsgTimeout}")
    private int sendMsgTimeout;
    /**
     * 消息发送失败重试次数，默认2次
     */
    @Value("${rocketmq.producer.retryTimesWhenSendFailed}")
    private int retryTimesWhenSendFailed;
    

    @Autowired
    private SystemConfig systemConfig;


    /**
     * 生产者Bean
     * @throws Exception 
     */
    @Bean(name = "defaultMQProducer")
    public  DefaultMQProducer producer() throws Exception {
    	if(!systemConfig.getIsTest()) {
    		if(systemConfig.getIsAdmin()) return null;
    	}
    	
        if (this.producerGroupName.isEmpty()) {
            throw new Exception("组名为空！");
        }
        if (this.producerNamesrvAddr.isEmpty()) {
            throw new Exception("IP地址为空！");
        }
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer(producerGroupName);
        defaultMQProducer.setNamesrvAddr(producerNamesrvAddr);
        defaultMQProducer.setMaxMessageSize(maxMessageSize);
        defaultMQProducer.setSendMsgTimeout(sendMsgTimeout);
        defaultMQProducer.setVipChannelEnabled(false);
        //消息发送到mq服务器失败重试次数
        defaultMQProducer.setRetryTimesWhenSendFailed(retryTimesWhenSendFailed);
        try {
            defaultMQProducer.start();
            logger.info("rocketMq Producer start success; nameServer:{},producerGroupName:{}", producerNamesrvAddr, producerGroupName);
        } catch ( Exception e) {
            logger.error("rocketMq Producer start fail;{}", e);
        }
        return defaultMQProducer;
    }
}