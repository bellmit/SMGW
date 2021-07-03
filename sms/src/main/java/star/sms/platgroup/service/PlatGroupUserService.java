package star.sms.platgroup.service;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.platgroup.dao.PlatGroupUserRepository;
import star.sms.platgroup.domain.PlatGroupUser;

/**
 * @author star
 */
@Service
@Transactional
public class PlatGroupUserService extends BaseServiceProxy<PlatGroupUser> {
	
	@Resource
	private EntityManager em;
	
	@Resource
	private PlatGroupUserRepository platGroupUserRepository;
	
	@Override
	protected BaseRepository<PlatGroupUser> getBaseRepository() {
		return platGroupUserRepository;
	}
	
	/**
	 * 查询人员的所有部门
	 * @param userId
	 * @return
	 */
	public List<PlatGroupUser> findByUserId(Integer userId) {
		return platGroupUserRepository.findByUserId(userId);
	}
	
	/**
	 * 检查指定人员是否属于总部及下级部门
	 * @param userId 人员ID
	 * @return
	 */
	public boolean checkBelongHeadQuartersByUserId(Integer userId) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(1) AS num ");
		sql.append("FROM tb_plat_group_user pgu ");
		sql.append("INNER JOIN tb_plat_group pg ON pg.group_id = pgu.group_id AND pg.group_code LIKE 'HEADQUARTERS%' ");
		sql.append("WHERE pgu.user_id = :userId ");
		
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("userId", userId);
		
		Number number = (Number) query.getSingleResult();
		return number.intValue() > 0 ? true : false;
	}

	/**
	 * 检查指定人员是否属于运营中心部门
	 * @param userId 人员ID
	 * @return
	 */
	public boolean checkBelongOperationCenterByUserId(Integer userId) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(1) AS num ");
		sql.append("FROM tb_plat_group_user pgu ");
		sql.append("INNER JOIN tb_plat_group pg ON pg.group_id = pgu.group_id AND pg.group_code LIKE 'OPERATIONS_CENTER%' ");
		sql.append("WHERE pgu.user_id = :userId ");
		
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("userId", userId);
		
		Number number = (Number) query.getSingleResult();
		return number.intValue() > 0 ? true : false;
	}
	
	/**
	 * 删除这个人的所有部门
	 * @param userId
	 */
	public void deleteByUserId(Integer userId) {
		platGroupUserRepository.deleteByUserId(userId);
	}
}
