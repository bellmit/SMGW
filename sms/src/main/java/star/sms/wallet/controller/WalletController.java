package star.sms.wallet.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import star.sms._frame.base.BaseController;
import star.sms.platmanager.domain.PlatManager;
import star.sms.platmanager.service.PlatManagerService;
import star.sms.wallet.domain.Wallet;
import star.sms.wallet.domain.WalletLog;
import star.sms.wallet.service.WalletLogService;
import star.sms.wallet.service.WalletService;

/**
 * 钱包管理
 * @author star
 */
@Controller
@RequestMapping("/wallet")
public class WalletController extends BaseController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private WalletService walletService;
	@Autowired
	private WalletLogService walletLogService;
	@Autowired
	private PlatManagerService platManagerService;
	
	/**
	 * 在线充值
	 * @return
	 */
	@RequestMapping(value = "/pay", method = RequestMethod.GET)
	public String pay(ModelMap model) {
		return "/wallet/pay";
	}
	
	/**
	 * 在线充值
	 * @return
	 */
	@RequestMapping(value = "/payDialog", method = RequestMethod.GET)
	public String payDialog(ModelMap model) {
		return "/wallet/payDialog";
	}

	/**
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/payMoney")
	@ResponseBody
	public Object payMoney(BigDecimal money) {
		Wallet wallet = walletService.findIfExist();
		BigDecimal oldMoney = wallet.getMoney();
		wallet.setMoney(wallet.getMoney().add(money));
		walletService.save(wallet);
		
		WalletLog walletLog = new WalletLog();
		walletLog.setUserId(getLoginUser().getId());
		walletLog.setWalletId(wallet.getId());
		walletLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
		walletLog.setMemo(getLoginUser().getNickName()+"充值了"+money+"元");
		walletLog.setMoney(money);
		walletLog.setOldMoney(oldMoney);
		walletLogService.save(walletLog);
		logger.info(getLoginUser().getNickName()+"充值了"+money+"元");
		return SUCCESS();
	}
	
	/**
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/payMoney2")
	@ResponseBody
	public Object payMoney2(BigDecimal money,Integer id) {
		PlatManager pm = platManagerService.findOne(id);
		Wallet wallet = walletService.findIfExist(id);
		BigDecimal oldMoney = wallet.getMoney();
		wallet.setMoney(wallet.getMoney().add(money));
		walletService.save(wallet);
		
		WalletLog walletLog = new WalletLog();
		walletLog.setUserId(id);
		walletLog.setWalletId(wallet.getId());
		walletLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
		walletLog.setMemo(pm.getNickName()+"充值了"+money+"元");
		walletLog.setMoney(money);
		walletLog.setOldMoney(oldMoney);
		walletLogService.save(walletLog);
		logger.info(pm.getNickName()+"充值了"+money+"元");
		return SUCCESS();
	}
	
	/**
	 * 根据id查询
	 */
	@RequestMapping(value = "/findById", method = RequestMethod.POST)
	@ResponseBody
	public Object findById(ModelMap model, Integer id) throws Exception {
		Wallet wallet = walletService.findIfExist(id);
		return SUCCESS(wallet);
	}
	
}
