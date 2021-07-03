package star.sms.platrole.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import star.sms._frame.base.BaseRepository;
import star.sms.platrole.domain.PlatRoleUser;

/**
 * 平台角色
 * @author star
 */
@Repository
public interface PlatRoleUserRepository extends BaseRepository<PlatRoleUser> {

	List<PlatRoleUser> findByUserId(Integer userId);
	
	List<PlatRoleUser> findByRoleId(Integer roleId);
	
	void deleteByUserId(Integer userId);
}
