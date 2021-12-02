package star.sms.smsmq.smstask;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import star.sms._frame.utils.MD5;
import star.sms.account.service.AccountService;
import star.sms.smsmq.domain.http.ReportRequest;
import star.sms.smsmq.domain.httpv2.HttpV2OverageRequest;
import star.sms.smsmq.domain.httpv2.HttpV2OverageResponse;
import star.sms.smsmq.domain.httpv3.HttpV3OverageRequest;
import star.sms.smsmq.domain.httpv3.HttpV3OverageResponse;
import star.sms.smsmq.httputils.HttpConnectionUtil;

/**
 * 余额查询
 * @author star
 *
 */
@Service
public class OverageSms {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	private HttpConnectionUtil httpConnectionUtil;
	
    @Autowired
    private AccountService accountService;

	/**
	 * 请求余额http
	 * 
	 * @param reportRequest
	 */
    @Deprecated
	public void hanlderHttp(ReportRequest reportRequest) {

	}
	/**
	 * 请求余额httpV2
	 * 
	 * @param reportRequest
	 */
	public void hanlderHttpV2(HttpV2OverageRequest overageRequest) {
		try {
			if(overageRequest!=null) {
				//构造查询参数
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("userid", overageRequest.getUserid()==null?"":overageRequest.getUserid());	
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String time = sdf.format(new Date());
				paramsMap.put("timestamp", time);
				String account = overageRequest.getAccount()==null?"":overageRequest.getAccount();
				String password = overageRequest.getPassword()==null?"":overageRequest.getPassword();
				String sign= MD5.encode(account+password+time);
				paramsMap.put("sign",sign );
				paramsMap.put("action", "overage");
	
				String xml = httpConnectionUtil.postSync(overageRequest.getIp()+"v2sms.aspx", paramsMap);
				if (xml != null) {
					ObjectMapper mapper = new XmlMapper();
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					HttpV2OverageResponse overageResponse = mapper.readValue(xml, HttpV2OverageResponse.class);
					if(overageResponse!=null) {
						//成功状态
						String returnstatus =overageResponse.getReturnstatus();
						//更新余额
						String overage = overageResponse.getOverage();
						if("Sucess".equals(returnstatus)&&StringUtils.isNumeric(overage)) {
							accountService.updateAccountOverageHttp2(overageRequest.getAccount(),new BigDecimal(overage));
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("请求httpv2余额报告错误",e);
		}
	}
	/**
	 * 请求余额httpV3
	 * 
	 * @param reportRequest
	 */
	public void hanlderHttpV3(HttpV3OverageRequest overageRequest) {
		try {
			if(overageRequest!=null) {
				//构造查询参数
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("userid", overageRequest.getUserid()==null?"":overageRequest.getUserid());
				String account = overageRequest.getAccount()==null?"":overageRequest.getAccount();
				paramsMap.put("account", account);
				String password = overageRequest.getPassword()==null?"":overageRequest.getPassword();
				paramsMap.put("password", password);
				paramsMap.put("action", "overage");
	
				String xml = httpConnectionUtil.postSync(overageRequest.getIp()+"sms.aspx", paramsMap);
				if (xml != null) {
					ObjectMapper mapper = new XmlMapper();
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					HttpV3OverageResponse overageResponse = mapper.readValue(xml, HttpV3OverageResponse.class);
					if(overageResponse!=null) {
						//成功状态
						String returnstatus =overageResponse.getReturnstatus();
						//更新余额
						String overage = overageResponse.getOverage();
						if("Sucess".equals(returnstatus)&&StringUtils.isNumeric(overage)) {
							accountService.updateAccountOverageHttp3(overageRequest.getAccount(),new BigDecimal(overage));
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("请求httpv3余额报告错误",e);
		}
	}
}
