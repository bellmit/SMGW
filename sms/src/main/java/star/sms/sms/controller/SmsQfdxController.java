package star.sms.sms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import star.sms._frame.base.BaseController;

/**
 * 短信发送-群发短信
 * 
 * @author star
 */
@Controller
@RequestMapping("/sms/qfdx")
public class SmsQfdxController extends BaseController {
	/**
	 * 群发短信
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap model) {
		return "/sms/qfdx";
	}
}
