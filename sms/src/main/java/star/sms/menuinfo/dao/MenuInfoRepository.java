package star.sms.menuinfo.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import star.sms._frame.base.BaseRepository;
import star.sms.menuinfo.domain.MenuInfo;

/**
 * @author star
 */
@Repository
public interface MenuInfoRepository extends BaseRepository<MenuInfo> {
	
	/**
	 * 查询指定菜单ID的菜单对象
	 * @param menuIds 菜单ID集合
	 * @param menuStatus 菜单状态
	 * @return
	 */
	List<MenuInfo> findByMenuIdInAndMenuStatusOrderByMenuLevelAscMenuIndexAsc(Collection<Integer> menuIds, Integer menuStatus);

}
