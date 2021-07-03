 package star.sms.platmanager.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms._frame.base.Constant;
import star.sms._frame.utils.StringUtils;
import star.sms._frame.utils.Testtrans;
import star.sms._frame.utils.Tools;
import star.sms.platgroup.domain.PlatGroupUser;
import star.sms.platgroup.service.PlatGroupUserService;
import star.sms.platmanager.dao.PlatManagerRepository;
import star.sms.platmanager.domain.PlatManager;
import star.sms.platrole.domain.PlatRoleUser;
import star.sms.platrole.service.PlatRoleService;
import star.sms.platrole.service.PlatRoleUserService;
import star.sms.wallet.dao.WalletDao;
import star.sms.wallet.domain.Wallet;

/**
 * @author star
 */
@Service
@Transactional
public class PlatManagerService extends BaseServiceProxy<PlatManager> {
	@Resource
	private JdbcTemplate jdbcTemplate;
	@Resource
	private EntityManager em;
	
	@Resource
	private PlatManagerRepository platManagerRepository;
	
	@Resource
	private PlatRoleService platRoleService;
	
	@Resource
	private PlatRoleUserService platRoleUserService;
	
	@Resource
	private PlatGroupUserService platGroupUserService;
	@Resource
	private WalletDao walletDao;
	
	@Override
	protected BaseRepository<PlatManager> getBaseRepository() {
		return platManagerRepository;
	}
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
	/**
	 * 根据登录名查询（登录用）
	 * @param loginName
	 * @return
	 */
	public PlatManager findByLoginName(String loginName) {
		return platManagerRepository.findTop1ByLoginNameAndIsDelete(loginName,0);
	}
	
	/**
	 * 根据主键集合，查询一批用户对象（关联用）
	 * @param ids
	 * @return
	 */
	public List<PlatManager> findByIdIn(Collection<Integer> ids){
		Iterator<Integer> iterator = ids.iterator();
		while (iterator.hasNext()) {
			Integer id = iterator.next();
			if(id == null) {
				iterator.remove();
			}
		}
		return platManagerRepository.findByIdIn(ids);
	}
	
	
	
	/**
	 * 根据角色查询平台用户信息
	 * @param roleCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PlatManager> findByRoleCode(String roleCode){
		if(StringUtils.isEmpty(roleCode)) {
			throw new RuntimeException("roleCode is null");
		}
		List<Object> params = new LinkedList<Object>();
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append("FROM tb_plat_manager pm ");
		fromWhereSql.append("INNER JOIN tb_plat_role_user pru ON pru.user_id = pm.id AND pm.state = 100 ");
		fromWhereSql.append("INNER JOIN tb_plat_role pr ON pr.role_id = pru.role_id AND pr.role_code = ? AND pm.state = 100 ");
		params.add(roleCode);
		
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT pm.id AS id, pm.login_name AS loginName, pm.nick_name AS nickName, pm.agent_sale_id AS agentSaleId, pm.agent_sale_name AS agentSaleName ");
		selectSql.append(", pm.superior_id AS superiorId, pm.account_manager_id AS accountManagerId, pm.account_manager_name AS accountManagerName ");
		
		String orderBySql = "ORDER BY pm.id DESC ";
		
		Query contentQuery = em.createNativeQuery(selectSql + fromWhereSql.toString() + orderBySql);
		if (!params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				contentQuery.setParameter(i+1, params.get(i));
			}
		}
		contentQuery.unwrap(SQLQuery.class).setResultTransformer(new Testtrans(PlatManager.class));
		return contentQuery.getResultList();
		
	}
	
	/**
	 * @param state
	 * @return
	 */
	public List<PlatManager> findByState(Integer state){
		return platManagerRepository.findByState(state);
	}
	
	/**
	 * 验证登录名唯一
	 * @param id 主键
	 * @param loginName 登录名
	 * @return
	 */
	public PlatManager verifyRepeatByIdAndLoginName(Integer id, String loginName) {
		StringBuffer hql = new StringBuffer();
		hql.append("FROM PlatManager WHERE loginName = :loginName ");
		if (id != null) {
			hql.append("AND id != :id ");
		}
		Query query = em.createQuery(hql.toString());
		query.setParameter("loginName", loginName);
		if (id != null) {
			query.setParameter("id", id);
		}
		query.setMaxResults(1);
		PlatManager platManager = null;
		try {
			platManager =(PlatManager) query.getSingleResult();
		} catch (NoResultException nre) {
			
		}
		return platManager;
	}
	
	/**
	 * 新增子账号
	 * @param subAccount
	 * @param roleIds
	 * @param groupIds
	 * @param loginUser
	 * @throws Exception
	 */
	@Transactional(readOnly=false)
	public void saveSubAccount(PlatManager subAccount, int[] roleIds, PlatManager loginUser) throws Exception {
		
		subAccount.setCreateId(loginUser.getId());
		subAccount.setCreaterName(loginUser.getNickName());
		subAccount.setCreateTime(new Date());
		
		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		String password = md5.encodePassword(Constant.PLATMANAGER_DEFAULT_PASSWORD, null);
		subAccount.setPassword(password);
		subAccount.setIsSubAccount(0);
		subAccount = platManagerRepository.save(subAccount);
		
		//创建钱包
		Wallet wallet = new Wallet();
		wallet.setUserId(subAccount.getId());
		wallet.setMoney(new BigDecimal(0));
		walletDao.save(wallet);
		
		// 保存角色和部门。这里一个人只保存一个部门， 一个人只保存一个角色
		this.saveRole(subAccount, roleIds, loginUser);
	}
	
	/**
	 * 私有方法-保存角色
	 * @param subAccount
	 * @param userType
	 * @param roleIds
	 * @param groupIds
	 * @param loginUser
	 */
	private void saveRole (PlatManager subAccount , int[] roleIds, PlatManager loginUser) {
		if(roleIds!=null && roleIds.length>0) {
			// 保存所选的角色
			PlatRoleUser platRoleUser = new PlatRoleUser();
			platRoleUser.setRoleId(roleIds[0]);
			platRoleUser.setUserId(subAccount.getId());
			platRoleUserService.save(platRoleUser);
		}
	}
	
	/**
	 * 修改子账号
	 * @param subAccount
	 * @param userType
	 * @param roleIds
	 * @param groupIds
	 * @param loginUser
	 * @throws Exception
	 */
	@Transactional(readOnly=false)
	public void updateSubAccount(PlatManager subAccount , int[] roleIds, PlatManager loginUser) throws Exception {
		
		PlatManager oldSubAccount = platManagerRepository.findOne(subAccount.getId());
		StringUtils.mergeObject(subAccount, oldSubAccount);
		oldSubAccount = platManagerRepository.save(oldSubAccount);
		// 保存角色和部门。这里一个人只保存一个部门， 一个人只保存一个角色
		this.saveRole(oldSubAccount, roleIds, loginUser);
	}
	
	
	/**
	 * 修改代理商
	 * @param agent
	 * @param loginUser
	 * @throws Exception 
	 */
	@Transactional(readOnly=false)
	public void updateAgent(PlatManager platManager, PlatManager loginUser, Integer groupId) throws Exception {
		
		PlatManager oldPlat = platManagerRepository.findOne(platManager.getId());
		PlatManager copyPlat = Tools.beanCopyForLodBean(oldPlat);
		
		StringUtils.mergeObject(platManager, oldPlat);
		
		platManagerRepository.save(oldPlat);
		// 保存到所选的部门
		// 先删除这个人的所有部门
		platGroupUserService.deleteByUserId(platManager.getId());
		PlatGroupUser platGroupUser = new PlatGroupUser();
		platGroupUser.setGroupId(groupId);
		platGroupUser.setUserId(platManager.getId());
		platGroupUserService.save(platGroupUser);
		// 添加日志
		//dataOperationLogService.save(oldPlat.getId(), loginUser, oldPlat, copyPlat, SystemFlag.OP, ModuleFlag.PLATMANAGER, ActionType.MODIFY);
		
	}
	
	/**
	 * 用户管理-列表查询-总部
	 * @param keywordType
	 * @param keyword
	 * @param pageable
	 * @return
	 */
	public Page<PlatManager> findByPage(String keyword, Pageable pageable) {
		List<Object> params = new LinkedList<Object>();
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" FROM ( ");
		fromWhereSql.append(" select * FROM tb_plat_manager pm ");
		fromWhereSql.append(appendWhere(keyword, params));
		fromWhereSql.append(" ) m left join tb_wallet n on m.id=n.userId ");
		
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT m.id AS id, m.login_name AS loginName, m.nick_name AS nickName ");
		selectSql.append(", m.phone AS phone, m.state AS state,m.price,n.money,m.priority ");
		
		String orderBySql = "ORDER BY m.id DESC ";
		// 封装并返回
		return super.findPageBySql(PlatManager.class, selectSql.toString(), fromWhereSql.toString(), orderBySql, params, pageable);
	}
	/**
	 * 指派鉴定员人员获取
	 * @param keywordType
	 * @param keyword
	 * @param pageable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PlatManager> findByList(String rode_code) {
		StringBuffer hql = new StringBuffer();
		hql.append("select*FROM tb_plat_manager   where id in (select user_id from tb_plat_role a,tb_plat_role_user b  where a.role_id=b.role_id  and  a.role_code='"+rode_code+"')");
		Query countQuery = getEntityManager().createNativeQuery(hql.toString());
		return (List<PlatManager>)  countQuery.getResultList();	
		}
	/**
	 * WHERE SQL append
	 * @param keywordType
	 * @param keyword
	 * @param params
	 * @return
	 */
	private String appendWhere(String keyword, List<Object> params) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where login_name <> 'admin' and isDelete = 0 ");
		if(StringUtils.isNotEmpty(keyword)) {
			String nickName = keyword.trim();
			String loginName = keyword.trim();
			String whereOrSql = appendWhereOrSql(nickName, loginName, params);
			if (whereOrSql != null) {
				sql.append(" and ("+whereOrSql+") ");
			}
		}
		return sql.toString();
	}
	
	/**
	 * 关键字OR拼接
	 * @param nickName
	 * @param loginName
	 * @param params
	 * @return
	 */
	private String appendWhereOrSql(String nickName, String loginName, List<Object> params) {
		
		StringBuffer sql = new StringBuffer();
		if (StringUtils.isNotEmpty(nickName)) {
			sql.append("OR nick_name LIKE ? ");
			params.add("%"+nickName+"%");
		}
		if (StringUtils.isNotEmpty(loginName)) {
			sql.append("OR login_name LIKE ? ");
			params.add("%"+loginName+"%");
		}
		if (sql.length() > 0) {
			sql.delete(0, "OR ".length());
		}
		if (sql.length() > 0) {
			return "AND ("+sql.toString()+") ";
		}else {
			return null;
		}
	}
	
	public void deleteUser(PlatManager platManager) {
		jdbcTemplate.update("update tb_plat_manager set isDelete=1,state='200' where id='"+platManager.getId()+"'");
	}
	
	/**
	 * 
	 * @param userIdList
	 * @return
	 */
	public List<PlatManager> findUserListByUserId(Set<Integer> userIdSet) {
		List<PlatManager> platManagerList = new ArrayList<PlatManager>();
		StringBuilder userIds=new StringBuilder();
		int i=0;
		for (Integer userId : userIdSet) {
			userIds.append("'"+userId+"'");
			if(i<userIdSet.size()-1) {
				userIds.append(",");
			}
			i++;
		}
		platManagerList = jdbcTemplate.query("select * from tb_plat_manager where id in ("+userIds.toString()+")", new BeanPropertyRowMapper<PlatManager>(PlatManager.class));
		return platManagerList;
	}
	
	public Integer getUserCount() {
		return jdbcTemplate.queryForObject("select count(1) from tb_plat_manager where isDelete=0 and state='100'", Integer.class);
	}
}
