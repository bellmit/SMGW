package sms;

import java.util.ArrayList;
import java.util.List;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import star.sms.SmsApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmsApplication.class)
public class MQTest {
	
	@Autowired
	private DefaultMQProducer defaultMQProducer;
 
    @Test
     public void test(){
    	try {
    		List<Message> listMessage = new ArrayList<Message>();
    		Message msg = new Message("sms_topic",
    				"sms_tag",
    				"OrderID188",
    				"Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
    		listMessage.add(msg);
    		listMessage.add(msg);
    		listMessage.add(msg);
    		defaultMQProducer.send(listMessage,new SendCallback() {
    			@Override
    			public void onSuccess(SendResult sendResult) {
    				System.out.printf("%s%n", sendResult.getSendStatus());
    			}
    			
    			@Override
    			public void onException(Throwable throwable) {
    			}
    		});
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	try {
			//Thread.currentThread();
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
     }
}
