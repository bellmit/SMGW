package star.sms.smsmq.smstask;

import java.util.List;

import javax.annotation.Resource;

import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import star.sms._frame.utils.UUIDUtils;
import star.sms.account.domain.AccountInfo;
import star.sms.sms.service.SmsService;
import star.sms.smsmq.domain.http.SendRequest;
import star.sms.smsmq.domain.smpp.SendRequestSmpp;
import star.sms.smsmq.httputils.HttpConnectionUtil;
import star.sms.smsmq.smshander.SmsProducerHanlder;

/**
 * 短信发送接口
 * 
 * @author star
 *
 */
@Service
public class SendTask {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private HttpConnectionUtil httpConnectionUtil;
	
    @Autowired
    private RedissonClient redissonClient;
    
    @Autowired
    private SmsProducerHanlder smsProducerHanlder;

	/**
	 * 短信发送
	 * 
	 * @param sendRequest
	 * @return
	 */
	public void hanlderHttp(SendRequest sendRequest) {
		try {
			// 200为单位分割
			int batchNumber=200;
			List<String> mobileList = sendRequest.getMobileList();
			List<Integer> mobileIdList = sendRequest.getMobileIdList();
			List<String> contentList = sendRequest.getContentList();
			int num = 0;
			if (mobileList != null) {
				num = mobileList.size() % batchNumber == 0 ? mobileList.size() / batchNumber : mobileList.size() / batchNumber + 1;
			}
			for (int i = 0; i < num; i++) {
				//产生批次
				String batchId=UUIDUtils.random();
				//构造发送
				SendRequest sendRequestBatch = new SendRequest();
				sendRequestBatch.setTaskId(sendRequest.getTaskId());
				sendRequestBatch.setBatchId(batchId);
				sendRequestBatch.setAction(sendRequest.getAction());
				
				int start = i*batchNumber;
				int end = start + batchNumber;
				if (end >= mobileList.size()) {
					end = mobileList.size();
				}
				sendRequestBatch.setMobileList(mobileList.subList(start, end));
				sendRequestBatch.setMobileIdList(mobileIdList.subList(start, end));
				sendRequestBatch.setContentList(contentList.subList(start, end));
				sendRequestBatch.setTaskContent(sendRequest.getTaskContent());
				sendRequestBatch.setContentType(sendRequest.getContentType());
				sendRequestBatch.setRt(sendRequest.getRt());
				// 存放mq
				smsProducerHanlder.smsSendHttp(sendRequestBatch);
			}
		} catch (Exception e) {
			logger.error("发送短信错误",e);
		}
	}
	/**
	 * 短信发送
	 * 
	 * @param sendRequest
	 * @return
	 */
	public void hanlderSmpp(SendRequestSmpp sendRequestSmpp) {
		try {
			// 存放mq
			smsProducerHanlder.smsSendSmpp(sendRequestSmpp);
		} catch (Exception e) {
			logger.error("发送短信错误",e);
		}
	}
}
