package star.sms.platrole.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import star.sms._frame.base.BaseRepository;
import star.sms.platrole.domain.MenuRole;

/**
 * @author star
 */
@Repository
public interface MenuRoleRepository extends BaseRepository<MenuRole> {

	/**
	 * 查询该角色对应的菜单权限
	 * @param roleId
	 * @return
	 */
	List<MenuRole> findByRoleId(Integer roleId);

	/**
	 * 查询该角色对应的菜单权限
	 * @param roleIds
	 * @return
	 */
	List<MenuRole> findByRoleIdIn(Collection<Integer> roleIds);
	
	
	void deleteByMenuIdAndRoleId(Integer menuId, Integer roleId);
}
