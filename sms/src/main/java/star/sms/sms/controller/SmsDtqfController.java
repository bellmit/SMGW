package star.sms.sms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import star.sms._frame.base.BaseController;

/**
 * 短信发送-单条群发
 * @author star
 */
@Controller
@RequestMapping("/sms/dtqf")
public class SmsDtqfController extends BaseController {
	/**
	 * 单条群发
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap model) {
		return "/sms/dtqf";
	}
}
