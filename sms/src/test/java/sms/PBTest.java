package sms;

import org.junit.Test;
import star.sms._frame.utils.ProtostuffUtil;
import star.sms.smsmq.domain.http.SendRequest;

public class PBTest {

	 @Test
     public void test(){
		 SendRequest sendRequest = new SendRequest();
		 sendRequest.setTaskId(1);
		 byte[] data = ProtostuffUtil.serializer(sendRequest);
		 System.out.println(data.length);
		 SendRequest sendRequestD = ProtostuffUtil.deserializer(data, SendRequest.class);
		 System.out.println(sendRequestD.getTaskId());
	 }
}
