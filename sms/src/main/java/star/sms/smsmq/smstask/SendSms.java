package star.sms.smsmq.smstask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.util.concurrent.RateLimiter;

import star.sms._frame.utils.JsonUtil;
import star.sms._frame.utils.MD5;
import star.sms.account.domain.AccountInfo;
import star.sms.account.service.AccountService;
import star.sms.smpp.service.SmppService;
import star.sms.sms.service.SmsService;
import star.sms.smsmq.domain.http.SendRequest;
import star.sms.smsmq.domain.http.SendResponse;
import star.sms.smsmq.domain.httpv2.HttpV2SendRequest;
import star.sms.smsmq.domain.httpv2.HttpV2SendResponse;
import star.sms.smsmq.domain.httpv3.HttpV3SendRequest;
import star.sms.smsmq.domain.httpv3.HttpV3SendResponse;
import star.sms.smsmq.domain.smpp.SendRequestSmpp;
import star.sms.smsmq.httputils.HttpConnectionUtil;

/**
 * 接收短信,并发送
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
    @Deprecated
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
					logger.info("未找到线路账号,开始重新缓存,账号:"+ sendRequest.getAccount());
					//重新缓存
					accountService.accountCache();
					//再次得到账号
					accountMap = accountService.getAccountMap();
					if (accountMap != null) {
						accountInfo = accountMap.get(sendRequest.getAccount());
					}
					if(accountInfo == null) {
						logger.info("最终未找到线路账号,账号:"+ sendRequest.getAccount());
						return;
					}
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
     * 发送httpV2短信
     * @param sendRequest
     */
	public void hanlderHttpV2(HttpV2SendRequest sendRequest) {
		//发送短信
		HttpV2SendResponse sendResponse = null;
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
					logger.info("未找到线路账号,开始重新缓存,账号:"+ sendRequest.getAccount());
					//重新缓存
					accountService.accountCache();
					//再次得到账号
					accountMap = accountService.getAccountMap();
					if (accountMap != null) {
						accountInfo = accountMap.get(sendRequest.getAccount());
					}
					if(accountInfo == null) {
						logger.info("最终未找到线路账号,账号:"+ sendRequest.getAccount());
						return;
					}
				}
				//构造发送参数
				Map<String, String> paramsMap = new HashMap<String, String>();
				
				paramsMap.put("userid", accountInfo.getUserid());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String time = sdf.format(new Date());
				paramsMap.put("timestamp", time);
				String account = sendRequest.getAccount()==null?"":sendRequest.getAccount();
				String password = accountInfo.getPassword()==null?"":accountInfo.getPassword();
				String sign= MD5.encode(account+password+time);
				paramsMap.put("sign",sign );
				//定时系统已经实现
				paramsMap.put("sendTime", "");
				paramsMap.put("extno", "0");

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
					paramsMap.put("content", sendRequest.getContent());
				}

				// 发送短信开始
				smsService.sendStartV2(sendRequest,sendRequest.getBatchId());
				// 账号限流，改为针对每个号码
				RateLimiter rateLimiter = accountInfo.getAccountLimiter();
				if(rateLimiter!=null) {
					rateLimiter.acquire(1);
				}

				String xml = httpConnectionUtil.postSync(accountInfo.getIp()+"v2sms.aspx", paramsMap);
				if (xml != null) {
					ObjectMapper mapper = new XmlMapper();
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					sendResponse = mapper.readValue(xml, HttpV2SendResponse.class);
					// 处理返回结果
					smsService.sendSuccessV2(sendRequest,sendResponse,sendRequest.getBatchId());
					
				}
			}
		} catch (Exception e) {
			logger.error("执行发送httpv2任务错误",e);
		}
	}
    /**
     * 发送httpV3短信
     * @param sendRequest
     */
	public void hanlderHttpV3(HttpV3SendRequest sendRequest) {
		//发送短信
		HttpV3SendResponse sendResponse = null;
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
					logger.info("未找到线路账号,开始重新缓存,账号:"+ sendRequest.getAccount());
					//重新缓存
					accountService.accountCache();
					//再次得到账号
					accountMap = accountService.getAccountMap();
					if (accountMap != null) {
						accountInfo = accountMap.get(sendRequest.getAccount());
					}
					if(accountInfo == null) {
						logger.info("最终未找到线路账号,账号:"+ sendRequest.getAccount());
						return;
					}
				}
				//构造发送参数
				Map<String, String> paramsMap = new HashMap<String, String>();
				
				paramsMap.put("userid", accountInfo.getUserid());
				String account = sendRequest.getAccount()==null?"":sendRequest.getAccount();
				paramsMap.put("account", account);
				String password = accountInfo.getPassword()==null?"":accountInfo.getPassword();
				paramsMap.put("password", password);
				//定时系统已经实现
				paramsMap.put("sendTime", "");
				paramsMap.put("extno", "0");

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
					paramsMap.put("content", sendRequest.getContent());
				}

				// 发送短信开始
				smsService.sendStartV3(sendRequest,sendRequest.getBatchId());
				// 账号限流，改为针对每个号码
				RateLimiter rateLimiter = accountInfo.getAccountLimiter();
				if(rateLimiter!=null) {
					rateLimiter.acquire(1);
				}

				String xml = httpConnectionUtil.postSync(accountInfo.getIp()+"sms.aspx", paramsMap);
				if (xml != null) {
					ObjectMapper mapper = new XmlMapper();
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					sendResponse = mapper.readValue(xml, HttpV3SendResponse.class);
					// 处理返回结果
					smsService.sendSuccessV3(sendRequest,sendResponse,sendRequest.getBatchId());
					
				}
			}
		} catch (Exception e) {
			logger.error("执行发送httpv3任务错误",e);
		}
	}
    /**
     * 发送smpp短信
     * @param sendRequest
     */
	public void hanlderSmpp(SendRequestSmpp sendRequestSmpp) {
		try {
			logger.info("收到信息:"+ sendRequestSmpp.getAccount()+":"+sendRequestSmpp.getSmsId());
			//得到smpp账号
			AccountInfo accountInfo = null;
			Map<String, AccountInfo> accountMap = accountService.getAccountMap();
			if (accountMap != null) {
				accountInfo = accountMap.get(sendRequestSmpp.getAccount());
			}
			//找不到账号
			if(accountInfo == null) {
				logger.info("未找到线路账号,开始重新缓存,账号:"+ sendRequestSmpp.getAccount());
				//重新缓存
				accountService.accountCache();
				//再次得到账号
				accountMap = accountService.getAccountMap();
				if (accountMap != null) {
					accountInfo = accountMap.get(sendRequestSmpp.getAccount());
				}
				if(accountInfo == null) {
					logger.info("最终未找到线路账号,账号:"+ sendRequestSmpp.getAccount());
					return;
				}
			}
			// 账号限流，改为针对每个号码
			RateLimiter rateLimiter = accountInfo.getAccountLimiter();
			if(rateLimiter!=null) {
				rateLimiter.acquire(1);
			}
			// 发送短信
			smppService.sendSms(sendRequestSmpp.getCreateUserId(),accountInfo.getId()+"", sendRequestSmpp.getSmsId(),sendRequestSmpp.getMobile(), sendRequestSmpp.getContent());
		} catch (Exception e) {
			logger.error("执行发送smpp任务错误",e);
		}
	}
}
