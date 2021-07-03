package star.sms.platgroup.service;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.platgroup.dao.PlatGroupRepository;
import star.sms.platgroup.domain.PlatGroup;

/**
 * @author star
 */
@Service
@Transactional
public class PlatGroupService extends BaseServiceProxy<PlatGroup> {
	
	@Resource
	private EntityManager em;

	@Resource
	private PlatGroupRepository platGroupRepository;

	@Override
	protected BaseRepository<PlatGroup> getBaseRepository() {
		return platGroupRepository;
	}
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * 通过部门编号查询部门信息
	 * @param groupCode
	 * @return
	 */
	public PlatGroup findByGroupCode(String groupCode) {
		return platGroupRepository.findTop1ByGroupCode(groupCode);
	}
	
	/**
	 * 通过部门编号模糊查询部门信息
	 * @param groupCode
	 * @return
	 */
	public List<PlatGroup> findByGroupCodeStartingWith(String groupCode) {
		return platGroupRepository.findByGroupCodeStartingWith(groupCode);
	}
	
	/**
	 * 查询一个用户所拥有的部门
	 * @param userId 用户ID
	 * @return
	 */
	public List<PlatGroup> findByUserId(Integer userId) {
		List<Object> params = new LinkedList<Object>();
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append("FROM tb_plat_group pg ");
		fromWhereSql.append("INNER JOIN tb_plat_group_user pgu ON pgu.group_id = pg.group_id ");
		fromWhereSql.append("INNER JOIN tb_plat_manager pm ON pm.id = pgu.user_id AND pm.id = ? ");
		params.add(userId);
		
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT pg.group_id AS groupId, pg.group_name AS groupName, pg.parent_group_id AS parentGroupId ");
		selectSql.append(", pg.group_code AS groupCode, pg.group_path AS groupPath ");
		
		String orderBySql = "ORDER BY pg.group_id DESC ";
		
		// 封装并返回
		return super.findListBySql(PlatGroup.class, selectSql.toString(), fromWhereSql.toString(), orderBySql, params);
	}
	
}
