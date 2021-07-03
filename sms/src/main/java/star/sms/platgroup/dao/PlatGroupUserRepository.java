package star.sms.platgroup.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import star.sms._frame.base.BaseRepository;
import star.sms.platgroup.domain.PlatGroupUser;

/**
 * @author star
 */
@Repository
public interface PlatGroupUserRepository extends BaseRepository<PlatGroupUser> {

	List<PlatGroupUser> findByUserId(Integer userId);
	
	void deleteByUserId(Integer userId);
}
