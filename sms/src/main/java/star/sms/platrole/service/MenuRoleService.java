package star.sms.platrole.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms._frame.utils.Tools;
import star.sms.menuinfo.service.MenuInfoService;
import star.sms.platrole.dao.MenuRoleRepository;
import star.sms.platrole.domain.MenuRole;

/**
 * @author star
 */
@Service
@Transactional
public class MenuRoleService extends BaseServiceProxy<MenuRole> {
	
	@Resource
	private EntityManager em;
	
	@Resource
	private MenuInfoService menuInfoService;
	
	@Resource
	private PlatRoleUserService platRoleUserService;

    @Resource
    private MenuRoleRepository menuRoleRepository;

    @Override
    protected BaseRepository<MenuRole> getBaseRepository() {
        return menuRoleRepository;
    }

    /**
     * 查询该角色对应的菜单权限
     * @param roleId
     * @return
     */
    public List<MenuRole> findByRoleId(Integer roleId) {
    	return menuRoleRepository.findByRoleId(roleId);
    }
    
    /**
     * 查询该角色对应的菜单权限
     * @param roleIds
     * @return
     */
    public List<MenuRole> findByRoleIdIn(Collection<Integer> roleIds) {
    	return menuRoleRepository.findByRoleIdIn(roleIds);
    }
    
    /**
     * 角色授权
     * @param roleId 角色ID
     * @param menuIdArr 新菜单ID数组
     */
    @Transactional(readOnly = false)
    public void authorityMenuRole(Integer roleId, Integer[] menuIdArr) {
    	
    	// 区分出哪些是新增权限，哪些是删除的权限
    	List<MenuRole> oldMenuRoleList = menuRoleRepository.findByRoleId(roleId);
    	Set<Integer> oldMenuIdSet = oldMenuRoleList.stream().map(r->r.getMenuId()).collect(Collectors.toSet());
    	Set<Integer> newMenuIdSet = new HashSet<Integer>();
    	if(null != menuIdArr) {
    		newMenuIdSet = new HashSet<Integer>(Arrays.asList(menuIdArr));
    	}
    	// 新传过的菜单ID中没有一级菜单ID，要加上。
    	Set<Integer> menuParentIdSet = menuInfoService.findMenuParentIdByMenuIdIn(newMenuIdSet);
    	newMenuIdSet.addAll(menuParentIdSet);
    	Tools.diffNewOrDel(oldMenuIdSet, newMenuIdSet);
   
    	
    	// 删除的菜单权限，在角色菜单表中删除数据，并给拥有此角色的所有平台用户的账号权限删除此权限
    	if (oldMenuIdSet != null && !oldMenuIdSet.isEmpty()) {
    		// 删除角色权限数据
    		for (Integer menuId: oldMenuIdSet) {
    			menuRoleRepository.deleteByMenuIdAndRoleId(menuId, roleId);
    		}
    		/*
    		List<Integer> userIdList = platRoleUserService.findUserIdByHasRole(roleId, null);
    		if (userIdList != null && !userIdList.isEmpty()) {
    			for (Integer userId : userIdList) {
    				menuUserService.deleteByUserIdAndMenuIdIn(userId, oldMenuIdSet);
				}
    		}
    		*/
    	}
		
    	// 新增的菜单权限，在角色菜单表中增加数据， 并给拥有些角色的一级平台用户的账号权限添加此权限
    	if (newMenuIdSet != null && !newMenuIdSet.isEmpty()) {
    		// 保存角色权限数据
    		batchSave(roleId, newMenuIdSet);
    		
    		/*
    		// 保存账号权限数据
    		List<Integer> userIdList = platRoleUserService.findUserIdByHasRole(roleId, false);
    		if (userIdList != null && !userIdList.isEmpty()) {
    			for (Integer userId : userIdList) {
    				for (Integer menuId: newMenuIdSet) {
    					MenuUser menuUser = menuUserService.findByMenuIdAndUserId(menuId, userId);
    					if (menuUser == null) {
    						menuUser = new MenuUser();
    						menuUser.setMenuId(menuId);
    						menuUser.setUserId(userId);
    						menuUserService.save(menuUser);
    					}
    				}
				}
    		}
    		*/
    	}
    }
    
    private void batchSave(Integer roleId, Set<Integer> menuIdSet) {
    	if (menuIdSet == null || menuIdSet.isEmpty()) {
    		return ;
    	}
    	List<MenuRole> list = new ArrayList<MenuRole>();
    	for (Integer menuId: menuIdSet) {
			MenuRole mr = new MenuRole();
			mr.setMenuId(menuId);
			mr.setRoleId(roleId);
			list.add(mr);
		}

		if(list.size()>0){
			menuRoleRepository.save(list);
		}
    }
}
