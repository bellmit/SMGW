package star.sms.smpp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import star.sms._frame.base.BaseController;
import star.sms.smpp.service.SmppService;

/**
 * smpp协议发送短信、闪信、长短信等
 * @author star
 */
@Slf4j
@Controller
@RequestMapping("/smpp")
public class SmppController extends BaseController {
	@Autowired
	private SmppService smppService;
	
	@RequestMapping(value = "/testSend")
	@ResponseBody
	public Object testSend(String id,String phone,String smsContent) throws Exception {
		//smppService.sendSms(id,0, phone, smsContent);
		return SUCCESS();
	}
}
