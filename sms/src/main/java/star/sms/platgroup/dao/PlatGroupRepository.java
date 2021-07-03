package star.sms.platgroup.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import star.sms._frame.base.BaseRepository;
import star.sms.platgroup.domain.PlatGroup;

/**
 * @author star
 */
@Repository
public interface PlatGroupRepository extends BaseRepository<PlatGroup> {
	
	PlatGroup findTop1ByGroupCode(String groupCode);
	
	/**
	 * 通过部门编号模糊查询部门信息
	 * @param groupCode
	 * @return
	 */
	List<PlatGroup> findByGroupCodeStartingWith(String groupCode);
}
