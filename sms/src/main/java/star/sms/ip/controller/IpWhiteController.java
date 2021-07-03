package star.sms.ip.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import star.sms._frame.base.BaseController;
import star.sms._frame.base.PageSupport;
import star.sms._frame.utils.excel.ExcelExportUtil2;
import star.sms.ip.domain.IpWhite;
import star.sms.ip.service.IpWhiteService;
import star.sms.ip.vo.IpWhiteParam;
import star.sms.logs.service.LogsService;

/**
 * ip白名单管理
 * @author star
 */
@Slf4j
@Controller
@RequestMapping("/ip/white")
public class IpWhiteController extends BaseController {
	@Resource
	private IpWhiteService ipWhiteService;
	@Resource
	private LogsService logsService;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(ModelMap model) {
		return "/ip/ipList";
	}
	
	/**
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/ipForm")
	public String ipForm(ModelMap model) {
		return "/ip/ipForm";
	}
	
	/**
	 * 分页查询
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/ipList")
	@ResponseBody
	public Object ipList(String keyword, PageSupport pagesupport) {
		Page page = ipWhiteService.findByPage(keyword,pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}
	
	/**
	 * 导出设备信息列表
	 * 
	 * @param queryParam
	 */
	@RequestMapping("/downExcel")
	public void downExcel(String keyword,PageSupport pagesupport) throws Exception {
		pagesupport.setPageNumber(0);
		pagesupport.setPageSize(Integer.MAX_VALUE);
		Page page = ipWhiteService.findByPage(keyword,pagesupport.getPage());
		List<IpWhiteParam> rows = page.getContent();
		ExcelExportUtil2 excelExportUtil = new ExcelExportUtil2(IpWhiteParam.class);
		excelExportUtil.export(rows, "ip列表");
		excelExportUtil.down(response, "ip列表.xlsx");
		logsService.addData("导出ip列表");
	}
	
	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete(String ids) {
		ipWhiteService.deleteByIds(ids);
		logsService.addData("批量删除ip白名单，ids:"+ids);
		return SUCCESS();
	}
	
	/**
	 * 保存
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	@ResponseBody
	public Object saveOrUpdate(ModelMap model, IpWhite m) throws Exception {
		m.setVisitCount(0);
		ipWhiteService.save(m);
		logsService.addData("创建IP白名单："+m.getIp());
		return SUCCESS();
	}
}
