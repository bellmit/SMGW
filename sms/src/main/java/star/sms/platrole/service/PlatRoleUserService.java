package star.sms.platrole.service;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.platrole.dao.PlatRoleUserRepository;
import star.sms.platrole.domain.PlatRoleUser;

/**
 * 平台角色-用户中间表接口类
 * @author star
 */
@Service
@Transactional
public class PlatRoleUserService extends BaseServiceProxy<PlatRoleUser> {
	
	@Resource
	private EntityManager em;
	
	@Resource
	private PlatRoleUserRepository platRoleUserRepository;

	@Override
	protected BaseRepository<PlatRoleUser> getBaseRepository() {
		return platRoleUserRepository;
	}

	/**
	 * 查询人员的所有角色
	 * @param userId
	 * @return
	 */
	public List<PlatRoleUser> findByUserId(Integer userId) {
		return platRoleUserRepository.findByUserId(userId);
	}
	
	/**
	 * 查询该角色的所有人员
	 * @param userId
	 * @return
	 */
	public List<PlatRoleUser> findByRoleId(Integer roleId) {
		return platRoleUserRepository.findByRoleId(roleId);
	}
	
	/**
	 * 查询拥有此角色的UserId集合
	 * @param roleId 角色ID
	 * @param isSubAccount 是否子账号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> findUserIdByHasRole(Integer roleId, Boolean isSubAccount) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT pt.id AS user_id ");
		sql.append("FROM tb_plat_manager pt ");
		sql.append("INNER JOIN tb_plat_role_user pru ON pru.user_id = pt.id AND pru.role_id = :roleId ");
		if (isSubAccount != null) {
			if (isSubAccount) {
				sql.append("WHERE pt.is_sub_account = 1 ");
			} else {
				sql.append("WHERE pt.is_sub_account IS NULL OR pt.is_sub_account = 0 ");
			}
		}
		
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("roleId", roleId);
		List<Integer> list = query.getResultList();
		return list;
	}
	
	/**
	 * 检查指定用户ID是否拥有指定角色
	 * @param userId 用户ID
	 * @return 返回true表示拥有指定角色，返回false表示没有指定角色
	 */
	public boolean checkHasRoleByUserId(Integer userId, String roleCode) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(1) AS num ");
		sql.append("FROM tb_plat_role_user pru ");
		sql.append("INNER JOIN tb_plat_role pr ON pr.role_id = pru.role_id AND pr.role_code = :roleCode ");
		sql.append("WHERE pru.user_id = :userId ");
		
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("roleCode", roleCode);
		query.setParameter("userId", userId);
		
		Number number = (Number) query.getSingleResult();
		return number.intValue() > 0 ? true : false;
	}
	
	/**
	 * 删除这个人的所有角色
	 * @param userId
	 */
	public void deleteByUserId(Integer userId) {
		platRoleUserRepository.deleteByUserId(userId);
	}
}
