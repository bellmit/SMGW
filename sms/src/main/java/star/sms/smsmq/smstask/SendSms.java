package star.sms.smsmq.smstask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.util.concurrent.RateLimiter;
import star.sms._frame.utils.JsonUtil;
import star.sms.account.domain.AccountInfo;
import star.sms.account.service.AccountService;
import star.sms.smpp.service.SmppService;
import star.sms.sms.service.SmsService;
import star.sms.smsmq.domain.http.SendRequest;
import star.sms.smsmq.domain.http.SendResponse;
import star.sms.smsmq.domain.smpp.SendRequestSmpp;
import star.sms.smsmq.httputils.HttpConnectionUtil;

/**
 * 发送短信
 * @author star
 *
 */
@Service
public class SendSms {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    private SmsService smsService;
    
    @Autowired
    private SmppService smppService;
    
    @Autowired
    private AccountService accountService;

    @Autowired
	private HttpConnectionUtil httpConnectionUtil;
 

    /**
     * 发送http短信
     * @param sendRequest
     */
	public void hanlderHttp(SendRequest sendRequest) {
		//发送短信
		SendResponse sendResponse = null;
		try {
			if (sendRequest != null) {
				//得到http账号
				AccountInfo accountInfo = null;
				Map<String, AccountInfo> accountMap = accountService.getAccountMap();
				if (accountMap != null) {
					accountInfo = accountMap.get(sendRequest.getAccount());
				}
				//找不到账号
				if(accountInfo == null) {
					logger.info("未找到线路账号！");
					return;
				}
				//构造发送参数
				Map<String, String> paramsMap = new HashMap<String, String>();
				
				paramsMap.put("account", sendRequest.getAccount());
				paramsMap.put("password", sendRequest.getPassword());
				paramsMap.put("extno", sendRequest.getExtno());
				paramsMap.put("rt", sendRequest.getRt());

				if(sendRequest.getContentType()==1) {
					//内容固定短信
					paramsMap.put("action", "send");
					List<String> mobileList = sendRequest.getMobileList();

					StringBuilder sb = new StringBuilder("");
					if (mobileList != null) {
						int i = 0;
						for (String mobile : mobileList) {
							sb.append(mobile);
							if (i < mobileList.size() - 1) {
								sb.append(",");
							}
							i++;
						}
					}
					paramsMap.put("mobile", sb.toString());
					paramsMap.put("content", sendRequest.getTaskContent());
				} else {
					//内容可变短信
					paramsMap.put("action", "p2p");
					List<String> mobileList = sendRequest.getMobileList();
					List<String> contentList = sendRequest.getContentList();
					
					StringBuilder sb = new StringBuilder("");
					if (mobileList != null && contentList!=null && mobileList.size() == contentList.size()) {
						for (int i = 0; i < mobileList.size(); i++) {
							sb.append(mobileList.get(i)+"#"+contentList.get(i));
							if (i < mobileList.size() - 1) {
								sb.append("\r");
							}
						}
					}
					paramsMap.put("mobileContentList", sb.toString());
				}

				// 发送短信开始
				smsService.sendStart(sendRequest,sendRequest.getBatchId());
				// 账号限流，改为针对每个号码
				RateLimiter rateLimiter = accountInfo.getAccountLimiter();
				if(rateLimiter!=null) {
					rateLimiter.acquire(1);
				}

				String json = httpConnectionUtil.postSync(sendRequest.getIp(), paramsMap);
				if (json != null) {
					sendResponse = JsonUtil.string2Obj(json, SendResponse.class);
					// 处理返回结果
					smsService.sendSuccess(sendRequest,sendResponse,sendRequest.getBatchId());
				}
			}
		} catch (Exception e) {
			logger.error("执行发送http任务错误",e);
		}
	}
    /**
     * 发送smpp短信
     * @param sendRequest
     */
	public void hanlderSmpp(SendRequestSmpp sendRequestSmpp) {
		try {
			//得到http账号
			AccountInfo accountInfo = null;
			Map<String, AccountInfo> accountMap = accountService.getAccountMap();
			if (accountMap != null) {
				accountInfo = accountMap.get(sendRequestSmpp.getAccount());
			}
			//找不到账号
			if(accountInfo == null) {
				logger.info("未找到线路账号！");
				return;
			}
			// 账号限流，改为针对每个号码
			RateLimiter rateLimiter = accountInfo.getAccountLimiter();
			if(rateLimiter!=null) {
				rateLimiter.acquire(1);
			}
			// 发送短信
			smppService.sendSms(accountInfo.getId()+"", sendRequestSmpp.getSmsId(),sendRequestSmpp.getMobile(), sendRequestSmpp.getContent());
		} catch (Exception e) {
			logger.error("执行发送smpp任务错误",e);
		}
	}
}
