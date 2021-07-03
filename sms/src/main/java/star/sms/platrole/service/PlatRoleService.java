package star.sms.platrole.service;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms._frame.base.PageSupport;
import star.sms._frame.utils.StringUtils;
import star.sms._frame.utils.Testtrans;
import star.sms.platmanager.service.PlatManagerService;
import star.sms.platrole.dao.PlatRoleRepository;
import star.sms.platrole.domain.PlatRole;

/**
 * 平台角色
 * @author star
 */
@Service
@Transactional
public class PlatRoleService extends BaseServiceProxy<PlatRole> {

	@Resource
	private EntityManager em;
	
	@Resource
	private PlatManagerService platManagerService;

	@Resource
	private PlatRoleRepository platRoleRepository;

	@Override
	protected BaseRepository<PlatRole> getBaseRepository() {
		return platRoleRepository;
	}
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * 根据角色编码查询一个角色对象
	 * @param roleCode
	 * @return
	 */
	public PlatRole findTop1ByRoleCode(String roleCode) {
		return platRoleRepository.findTop1ByRoleCode(roleCode);
	}

	/**
	 * 列表查询
	 * @param roleCode
	 * @param pageable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Page<PlatRole> findByRoleCodeContaining(String keyword, Pageable pageable) {
		
		StringBuffer hql = new StringBuffer();
		hql.append("FROM PlatRole ");
		if (StringUtils.isNotEmpty(keyword)) {
			hql.append("WHERE roleCode LIKE ? or roleName like ?");
		}
		Query countQuery = em.createQuery("SELECT COUNT(1) as total " + hql.toString());
		if (StringUtils.isNotEmpty(keyword)) {
			countQuery.setParameter(1, "%"+keyword+"%");
			countQuery.setParameter(2, "%"+keyword+"%");
		}
		Number total = (Number) countQuery.getSingleResult();
		// 校正页码
		int realPageNum = super.correctionPageNum(total.intValue(), pageable.getPageSize(), pageable.getPageNumber());
		
		Query contentQuery = em.createQuery(hql.toString()+"ORDER BY roleId DESC ");
		if (StringUtils.isNotEmpty(keyword)) {
			contentQuery.setParameter(1, "%"+keyword+"%");
			contentQuery.setParameter(2, "%"+keyword+"%");
		}
		
		contentQuery.setFirstResult(realPageNum * pageable.getPageSize());
		contentQuery.setMaxResults(pageable.getPageSize());
		List<PlatRole> content = contentQuery.getResultList();
		
		PageSupport newPageSupport = new PageSupport();
		newPageSupport.setPageNumber(realPageNum);
		newPageSupport.setPageSize(pageable.getPageSize());
		return new PageImpl<PlatRole>(content, newPageSupport.getPage(), total.longValue());
	}
	
	/**
	 * 更新角色对象
	 * @param platRole
	 */
	@Transactional(readOnly = false)
	public void updatePlatRole(PlatRole platRole) {
		PlatRole oldPlatRole = platRoleRepository.findOne(platRole.getRoleId());
		
		StringUtils.mergeObject(platRole, oldPlatRole);
		
		platRoleRepository.save(oldPlatRole);
	}

	/**
	 * 建议 在Controller中调用findAll()查询后，在Controller中封装成Map
	 * 查询平台用户角色，封装成一个Map，key是roleCode，value是roleName。
	 * 
	 * @return Map<String, String> key: roleCode, value: roleName
	 */
	@Deprecated
	public Map<String, String> findPlatRoleMap() {
		Map<String, String> platRoleMap = new LinkedHashMap<String, String>();
		Iterable<PlatRole> platRoleIterable = platRoleRepository.findAll();
		Iterator<PlatRole> platRoleIterator = null;
		if (platRoleIterable != null) {
			platRoleIterator = platRoleIterable.iterator();
			while (platRoleIterator.hasNext()) {
				PlatRole role = platRoleIterator.next();
				platRoleMap.put(role.getRoleCode(), role.getRoleName());
			}
		}
		return platRoleMap;
	}

	/**
	 * 验证 roleCode 是否重复
	 * @param roleId 主键
	 * @param roleCode 角色编码
	 * @return
	 */
	public PlatRole verifyRepeatByRoleIdAndRoleCode(Integer roleId, String roleCode) {
		StringBuffer hql = new StringBuffer();
		hql.append("FROM PlatRole WHERE roleCode = :roleCode ");
		if (roleId != null) {
			hql.append("AND roleId != :roleId ");
		}
		Query query = em.createQuery(hql.toString());
		query.setParameter("roleCode", roleCode);
		if (roleId != null) {
			query.setParameter("roleId", roleId);
		}
		query.setMaxResults(1);
		PlatRole platRole = null;
		try {
			platRole =(PlatRole) query.getSingleResult();
		} catch (NoResultException nre) {
		}
		return platRole;
	}
	
	/**
	 * 验证 roleCode 是否重复
	 * @param roleId 主键
	 * @param roleName 角色名
	 * @return
	 */
	public PlatRole verifyRepeatByRoleIdAndRoleName(Integer roleId, String roleName) {
		StringBuffer hql = new StringBuffer();
		hql.append("FROM PlatRole WHERE roleName = :roleName ");
		if (roleId != null) {
			hql.append("AND roleId != :roleId ");
		}
		Query query = em.createQuery(hql.toString());
		query.setParameter("roleName", roleName);
		if (roleId != null) {
			query.setParameter("roleId", roleId);
		}
		query.setMaxResults(1);
		PlatRole platRole = null;
		try {
			platRole =(PlatRole) query.getSingleResult();
		} catch (NoResultException nre) {
		}
		return platRole;
	}
	
	/**
	 * 查询一个用户所拥有的权限
	 * @param userId 用户ID
	 * @return
	 */
	public List<PlatRole> findByUserId(Integer userId) {
		List<Object> params = new LinkedList<Object>();
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append("FROM tb_plat_role pr ");
		fromWhereSql.append("INNER JOIN tb_plat_role_user pru ON pru.role_id = pr.role_id ");
		fromWhereSql.append("INNER JOIN tb_plat_manager pm ON pm.id = pru.user_id AND pm.id = ? ");
		params.add(userId);
		
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT pr.role_id AS roleId, pr.role_code AS roleCode, pr.role_name AS roleName ");
		
		String orderBySql = "ORDER BY pr.role_id DESC ";
		
		// 封装并返回
		return super.findListBySql(PlatRole.class, selectSql.toString(), fromWhereSql.toString(), orderBySql, params);
	}
	

	/**
	 * 根据用户id查找权限信息
	 */
	public List<PlatRole> findJurisdiction(Integer id) {
		Query countQuery = getEntityManager().createNativeQuery("select*  from tb_plat_role where role_id=(select role_id from tb_plat_role_user where user_id="+id+") ");
		countQuery.unwrap(SQLQuery.class).setResultTransformer(new Testtrans(PlatRole.class));
		List<PlatRole> content = countQuery.getResultList();
		return content;
	}
	
	
	
	
}
