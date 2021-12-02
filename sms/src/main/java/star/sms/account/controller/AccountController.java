package star.sms.account.controller;

import java.sql.Timestamp;
import java.util.HashMap;
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
import star.sms.account.domain.AccountInfo;
import star.sms.account.service.AccountService;
import star.sms.logs.service.LogsService;
import star.sms.smpp.service.SmppService;

/**
 * @author star
 */
@Controller
@RequestMapping("/account")
public class AccountController extends BaseController {
	
	@Autowired
	private LogsService logsService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private SmppService smppService;
	
	/**
	 * 通讯录管理-通讯录类型列表
	 * @return
	 */
	@RequestMapping(value = "/accountList", method = RequestMethod.GET)
	public String accountList(ModelMap model) {
		return "/account/accountList";
	}
	
	@RequestMapping(value = "/accountForm", method = RequestMethod.GET)
	public String accountForm(ModelMap model) {
		return "/account/accountForm";
	}
	
	/**
	 * 分页查询
	 * 
	 * @param keyword
	 * @param pagesupport
	 * @return
	 */
	@RequestMapping(value = "/getAccountList")
	@ResponseBody
	public Object getAccountList(String keyword, PageSupport pagesupport) {
		Page page = accountService.findByPage(keyword, pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
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
	public Object saveOrUpdate(ModelMap model, AccountInfo obj) throws Exception {
		if(obj.getId() == null||obj.getId()==0) {//新增
			obj.setCreateTime(new Timestamp(System.currentTimeMillis()));
			obj.setCreateUserId(getLoginUser().getId());
			obj.setIsDelete(0);
			accountService.save(obj);
			logsService.addData("创建账号信息:{title:"+obj.getTitle()+",account:"+obj.getAccount()+",password:"+obj.getPassword()+",extno:"+obj.getExtno()+",price:"+obj.getPrice()+",balance:"+obj.getBalance()+"}");
			//启动smpp线路
			if(obj.getChannelType()==2) smppService.enableChannel(obj);
		}else {
			AccountInfo old = accountService.findOne(obj.getId());
			String log = "修改账号信息，";
			if (old != null) {
				log=log+"修改前：{title:"+old.getTitle()+",account:"+old.getAccount()+",password:"+old.getPassword()+",extno:"+old.getExtno()+",price:"+old.getPrice()+",balance:"+old.getBalance()+"}";
				old.setIp(obj.getIp());
				old.setUserid(obj.getUserid());
				old.setTitle(obj.getTitle());
				old.setAccount(obj.getAccount());
				old.setPassword(obj.getPassword());
				old.setAlertBalance(obj.getAlertBalance());
				old.setAlertPhone(obj.getAlertPhone());
				old.setExtno(obj.getExtno());
				old.setAccountStatus(obj.getAccountStatus());
				old.setUpdateTime(new Timestamp(System.currentTimeMillis()));
				old.setUpdateUserId(getLoginUser().getId());
				old.setLimiter(obj.getLimiter());
				//保存
				accountService.save(old);
				log=log+"修改后：{title:"+old.getTitle()+",account:"+old.getAccount()+",password:"+old.getPassword()+",extno:"+old.getExtno()+",price:"+old.getPrice()+",balance:"+old.getBalance()+"}";

				//启动smpp线路
				if(obj.getChannelType()==2) {
					smppService.disableChannel(obj.getId()+"");
					if(obj.getAccountStatus()==1) smppService.enableChannel(obj);
				}
			}
			logsService.addData(log);
		}
		return SUCCESS();
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
		accountService.deleteByIds(ids);
		logsService.addData("批量删除绑定账号信息,具体看删除明细记录");
		return SUCCESS();
	}
	
	/**
	 * 根据id查询
	 */
	@RequestMapping(value = "/findById", method = RequestMethod.POST)
	@ResponseBody
	public Object findById(ModelMap model, Integer id) throws Exception {
		AccountInfo m = accountService.findOne(id);
		return SUCCESS(m);
	}
}
