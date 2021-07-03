package star.sms.dict.service;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import star.sms._frame.base.BaseDao;
import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.dict.dao.DictRepository;
import star.sms.dict.domain.Dict;

/**
 * 数据字典接口
 * @author star
 */
@Service
@Transactional
public class DictService extends BaseServiceProxy<Dict> {

	@Autowired
	private DictRepository dictRepository;
	@Autowired
	private BaseDao baseDao;
	@Resource
	private EntityManager em;
 

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	@Override
	protected BaseRepository<Dict> getBaseRepository() {
		return dictRepository;
	}
	
	public List<Dict> getDictTree(){
		return dictRepository.findBySysTagOrderByDictIndex(1);
	}
	
	public Page findByPage(String keyword,String dictType,Pageable pageable) {
		StringBuffer searchSql = new StringBuffer();
		searchSql.append(" FROM tb_dict a where a.sys_tag=0 and a.dict_type='"+dictType+"' ");
		if(StringUtils.isNotEmpty(keyword)) {
			searchSql.append("  and a.dict_name like '%"+keyword+"%' ");
		}
		int totalCount = baseDao.getCountBySql("select count(a.id) "+searchSql.toString());
		String fileds=" a.id,a.dict_name dictName,a.dict_type dictType,a.dict_value dictValue,a.dict_index dictIndex ";
		String orderStr=" order by a.dict_index,a.dict_name asc ";
		return baseDao.findByPage(pageable, searchSql.toString(), fileds, orderStr, totalCount);
	}
	
	/*
	 * 查询字典数据
	 */

	@SuppressWarnings("unchecked")
	public List<Dict> findByType(String superior) {
		String hql = "from Dict  WHERE superior = '" + superior + "'";
		Query query = em.createQuery(hql);
		return (List<Dict>) query.getResultList();
	}

 

	
	
	/**
	 * 根据字典类型查询字典列表
	 * @param sysTag
	 * @param dictType
	 * @return
	 */
	public List<Dict> dictList(Integer sysTag,String dictType){
		return dictRepository.findBySysTagAndDictTypeOrderByDictIndex(sysTag, dictType);
	}
	 
}
