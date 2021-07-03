package star.sms.menuinfo.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms._frame.utils.Testtrans;
import star.sms.menuinfo.dao.MenuInfoRepository;
import star.sms.menuinfo.domain.MenuInfo;
import star.sms.platrole.domain.MenuRole;
import star.sms.platrole.domain.PlatRoleUser;
import star.sms.platrole.service.MenuRoleService;
import star.sms.platrole.service.PlatRoleUserService;

/**
 * @author star
 */
@Service
@Transactional
public class MenuInfoService extends BaseServiceProxy<MenuInfo> {

	@Resource
	private MenuRoleService menuRoleService;

	@Resource
	private PlatRoleUserService platRoleUserService;

	@Resource
	private MenuInfoRepository menuInfoRepository;
	@Resource
	private EntityManager em;
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	@Override
	protected BaseRepository<MenuInfo> getBaseRepository() {
		return menuInfoRepository;
	}

	/**
	 * 查询该用户的权限。如果查询出的数据只有一条，且menu_id为NULL，表示用户账号权限被清空。否则返回的是拥有的菜单。
	 * 
	 * @param userId
	 * @return
	 */
	public List<MenuInfo> findByUserId(Integer userId) {
		/* 拥有的菜单权限集合 */
		Set<Integer> menuIdSet = new LinkedHashSet<Integer>();

		/* 先查询账号权限数据 */
//		List<MenuUser> menuUserList = menuUserService.findByUserId(userId);
//		for (MenuUser menuUser : menuUserList) {
//			menuIdSet.add(menuUser.getMenuId());
//		}
		/* 如果没有查询出数据，则拥有的角色权限 */
		if (menuIdSet.size() == 0) {
			List<PlatRoleUser> roleUserList = platRoleUserService.findByUserId(userId);
			Set<Integer> roleIdSet = roleUserList.stream().map(c -> c.getRoleId()).collect(Collectors.toSet());
			List<MenuRole> menuRoleList = menuRoleService.findByRoleIdIn(roleIdSet);
			menuIdSet = menuRoleList.stream().map(c -> c.getMenuId()).collect(Collectors.toSet());
		}

		Iterator<Integer> menuIdIterator = menuIdSet.iterator();
		while (menuIdIterator.hasNext()) {
			Integer menuId = menuIdIterator.next();
			if (menuId == null) {
				menuIdIterator.remove();
			}
		}

		return menuInfoRepository.findByMenuIdInAndMenuStatusOrderByMenuLevelAscMenuIndexAsc(menuIdSet, 1);
	}
	
	/**
	 * 查询这些菜单ID集合的父级菜单集合
	 * @param menuIds 菜单ID集合
	 * @return
	 */
	public Set<Integer> findMenuParentIdByMenuIdIn(Collection<Integer> menuIds) {
		List<MenuInfo> MenuInfoList = menuInfoRepository.findByMenuIdInAndMenuStatusOrderByMenuLevelAscMenuIndexAsc(menuIds, 1);
		Set<Integer> menuParentIdSet = MenuInfoList.stream().map(r->r.getMenuParentId()).filter(r->r.intValue() != 0).collect(Collectors.toSet());
		return menuParentIdSet;
	}
	
	@SuppressWarnings("unchecked")
	public List<MenuInfo> findByRoleCode(int user_id,int meun_id){
		
		Query contentQuery = em.createNativeQuery("select*  from tb_op_menu_info where menu_id in (select menu_id  from tb_op_menu_role where role_id=(select  role_id from tb_plat_role_user where user_id="+user_id+" )) and menu_status=0 and menu_parent_id="+meun_id);
 
		contentQuery.unwrap(SQLQuery.class).setResultTransformer(new Testtrans(MenuInfo.class));
		return contentQuery.getResultList();
		
	}
	
	
	
	
	
}
