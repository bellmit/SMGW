package star.sms.dict.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import star.sms._frame.base.BaseController;
import star.sms._frame.base.PageSupport;
import star.sms._frame.utils.JsonUtil;
import star.sms._frame.utils.TreeNode;
import star.sms.dict.domain.Dict;
import star.sms.dict.service.DictService;
import star.sms.operation_log.service.DataOperationLogService;
import star.sms.platmanager.domain.PlatManager;

/**
 * 数据字典
 * 
 * @author star
 */
@Controller
@RequestMapping("/dict")
public class DictController extends BaseController {
	@Autowired
	private DictService dictService;
	@Resource
	private DataOperationLogService dataOperationLogService;
	/**
	 * 数据字典首页
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap model) {
		return "/dict/index";
	}

	/**
	 * 新增/修改数据字典
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/dictForm")
	public String dictForm(ModelMap model) {
		// 单位类别
		List<Map<String, Object>> basicUnitCategory = dictService
				.findByPage("", "dwlb", new PageRequest(0, Integer.MAX_VALUE)).getContent();
		List<String> listbasicUnitCategory = basicUnitCategory.stream().map(d -> d.get("dictName").toString())
				.collect(Collectors.toList());
	model.addAttribute("dict", JsonUtil.obj2String(listbasicUnitCategory));
		return "/dict/dictForm";
	}

	@RequestMapping(value = "/dictTree")
	@ResponseBody
	public Object dictTree() {
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		try {
			TreeNode treeHead = new TreeNode();
			treeHead.setId("pid_0");// 根节点
			treeHead.setName("全部字典");
			treeHead.setTitle("全部字典");
			treeHead.setPId("pid_-1");
			treeHead.setOpen(true);
			treeHead.setIsParent(true);
			treeNodes.add(treeHead);

			List<Dict> list = dictService.getDictTree();
			if (list != null && list.size() > 0) {
				for (Dict dict : list) {
					TreeNode treeNode = new TreeNode();
					treeNode.setId("dict_" + dict.getId());
					treeNode.setName(dict.getDictName());
					treeNode.setTitle(dict.getDictName());
					treeNode.setPId("pid_0");
					treeNode.setOpen(false);
					treeNode.setIsParent(false);
					treeNode.setObj(dict.getDictType());
					treeNodes.add(treeNode);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS(treeNodes);
	}

	@RequestMapping(value = "/dictList")
	@ResponseBody
	public Object dictList(ModelMap model, String keyword, String dictType, PageSupport pagesupport) {
		Page page = dictService.findByPage(keyword, dictType, pagesupport.getPage());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotalElements());
		result.put("rows", page.getContent());
		return result;
	}

	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete(ModelMap model, Integer id) {
		dictService.delete(id);
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
	public Object saveOrUpdate(ModelMap model, Dict dict) throws Exception {
		PlatManager loginUser = getLoginUser();
		if (dict.getId() == null) {
			dict.setSysTag(0);
			dict.setCreateUserId(loginUser.getId());
			dict.setCreateTime(new Timestamp(System.currentTimeMillis()));
			dictService.save(dict);
			dataOperationLogService.add(0,0,"新增字典数据",super.getLoginUser());
		} else {
			Dict old = dictService.findOne(dict.getId());
			if (old != null) {
				BeanUtils.copyProperties(dict, old, "createTime", "createUserId", "sysTag");
				dictService.save(old);
				dataOperationLogService.add(0,5,"修改字典数据",super.getLoginUser());
			}
		}
		return SUCCESS();
	}

	/**
	 * 查看详情
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/findById", method = RequestMethod.POST)
	@ResponseBody
	public Object findById(ModelMap model, Integer id) throws Exception {
		Dict dict = dictService.findOne(id);
		return SUCCESS(dict);
	}

	/**
	 * 查找单位类型
	 */

	@RequestMapping(value = "/findByType", method = RequestMethod.POST)
	@ResponseBody
	public List<Dict> findByType(ModelMap model, String unitcategory) throws Exception {
		List<Dict> dicttype = dictService.findByType(unitcategory);
		return dicttype;
	}

}
