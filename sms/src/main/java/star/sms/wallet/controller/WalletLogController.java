package star.sms.wallet.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import star.sms._frame.base.BaseController;
import star.sms._frame.base.PageSupport;
import star.sms._frame.utils.excel.ExcelExportUtil2;
import star.sms.logs.service.LogsService;
import star.sms.wallet.service.WalletLogService;
import star.sms.wallet.vo.WalletLogParams;

/**
 * 钱包日志管理
 * @author star
 */
@Controller
@RequestMapping("/walletLog")
public class WalletLogController extends BaseController {
	
	@Autowired
	private LogsService logsService;
	@Autowired
	private WalletLogService walletLogService;
	
	/**
	 * 在线充值
	 * @return
	 */
	@RequestMapping(value = "/walletLogList", method = RequestMethod.GET)
	public String walletLogList(ModelMap model) {
		return "/wallet/walletLogList";
	}
	
	/**
	 * 在线充值记录
	 * @return
	 */
	@RequestMapping(value = "/walletLogTotalList", method = RequestMethod.GET)
	public String walletLogTotalList(ModelMap model) {
		return "/wallet/walletLogTotalList";
	}
	
	/**
	 * 分页查询
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getWalletLogList")
	@ResponseBody
	public Object getWalletLogList(String keyword,PageSupport pagesupport) {
		Page page = walletLogService.findByPage(keyword,pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}
	
	/**
	 * 分页查询
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getWalletLogTotalList")
	@ResponseBody
	public Object getWalletLogTotalList(String keyword,PageSupport pagesupport) {
		Page page = walletLogService.findByPage2(keyword,pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}
	

	/**
	 * 导出
	 * 
	 * @param queryParam
	 */
	@RequestMapping("/downExcel")
	public void downExcel(String keyword, PageSupport pagesupport) throws Exception {
		pagesupport.setPageNumber(0);
		pagesupport.setPageSize(Integer.MAX_VALUE);
		Page page = walletLogService.findByPage2(keyword, pagesupport.getPage());
		List<WalletLogParams> rows = page.getContent();
		ExcelExportUtil2 excelExportUtil = new ExcelExportUtil2(WalletLogParams.class);
		excelExportUtil.export(rows, "充值记录");
		excelExportUtil.down(response, "充值记录.xlsx");
		logsService.addData("导出充值记录");
	}
	
	
}
