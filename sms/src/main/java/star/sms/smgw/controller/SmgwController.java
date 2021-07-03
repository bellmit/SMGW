package star.sms.smgw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;
import star.sms._frame.base.BaseController;

/**
 * @author star
 */
@Slf4j
@Controller
@RequestMapping("/smgw")
public class SmgwController extends BaseController {

	@RequestMapping(value = "/sms57", method = RequestMethod.GET)
	public String login(ModelMap model) {
		return "smgw/sms57";
	}
}
