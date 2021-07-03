package star.sms.platmanager.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import star.sms._frame.base.BaseRepository;
import star.sms.platmanager.domain.PlatManager;

/**
 * @author star
 */
@Repository
public interface PlatManagerRepository extends BaseRepository<PlatManager> {

	/**
	 * 通过ids 查询参与人
	 * @param ids
	 * @return
	 */
	List<PlatManager> findByIdIn(Collection<Integer> ids);
	
	List<PlatManager> findByState(Integer state);
	
	PlatManager findTop1ByLoginName(String loginName);
	
	PlatManager findTop1ByLoginNameAndIsDelete(String loginName,Integer isDelete);
}
