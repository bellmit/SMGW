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
import star.sms._frame.utils.GoogleAuthenticator;
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
	 * ????????????????????????????????????
	 * @param loginName
	 * @return
	 */
	public PlatManager findByLoginName(String loginName) {
		return platManagerRepository.findTop1ByLoginNameAndIsDelete(loginName,0);
	}
	
	/**
	 * ????????????????????????????????????????????????????????????
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
	 * ????????????????????????????????????
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
	 * ?????????????????????
	 * @param id ??????
	 * @param loginName ?????????
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
	 * ???????????????
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
		subAccount.setSecret(GoogleAuthenticator.generateSecretKey());
		subAccount = platManagerRepository.save(subAccount);
		
		//????????????
		Wallet wallet = new Wallet();
		wallet.setUserId(subAccount.getId());
		wallet.setMoney(new BigDecimal(0));
		walletDao.save(wallet);
		
		// ??????????????????????????????????????????????????????????????? ??????????????????????????????
		this.saveRole(subAccount, roleIds, loginUser);
	}
	
	/**
	 * ????????????-????????????
	 * @param subAccount
	 * @param userType
	 * @param roleIds
	 * @param groupIds
	 * @param loginUser
	 */
	private void saveRole (PlatManager subAccount , int[] roleIds, PlatManager loginUser) {
		if(roleIds!=null && roleIds.length>0) {
			// ?????????????????????
			PlatRoleUser platRoleUser = new PlatRoleUser();
			platRoleUser.setRoleId(roleIds[0]);
			platRoleUser.setUserId(subAccount.getId());
			platRoleUserService.save(platRoleUser);
		}
	}
	
	/**
	 * ???????????????
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
		// ??????????????????????????????????????????????????????????????? ??????????????????????????????
		this.saveRole(oldSubAccount, roleIds, loginUser);
	}
	
	
	/**
	 * ???????????????
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
		// ????????????????????????
		// ?????????????????????????????????
		platGroupUserService.deleteByUserId(platManager.getId());
		PlatGroupUser platGroupUser = new PlatGroupUser();
		platGroupUser.setGroupId(groupId);
		platGroupUser.setUserId(platManager.getId());
		platGroupUserService.save(platGroupUser);
		// ????????????
		//dataOperationLogService.save(oldPlat.getId(), loginUser, oldPlat, copyPlat, SystemFlag.OP, ModuleFlag.PLATMANAGER, ActionType.MODIFY);
		
	}
	
	/**
	 * ????????????-????????????-??????
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
		fromWhereSql.append(" left join tb_account a on m.account_id=a.id ");
		
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT m.id AS id, m.login_name AS loginName, m.nick_name AS nickName ");
		selectSql.append(", m.phone AS phone, m.state AS state,m.price,m.percent,n.money,m.priority,a.title,m.secret ");
		
		String orderBySql = "ORDER BY m.id DESC ";
		// ???????????????
		return super.findPageBySql(PlatManager.class, selectSql.toString(), fromWhereSql.toString(), orderBySql, params, pageable);
	}
	/**
	 * ???????????????????????????
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
	 * ?????????OR??????
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

	
	/**
	 * @param state
	 * @return
	 */
	public List<PlatManager> findValidUser(){
		if(!getLoginUser().getRoleCode().equals("ADMIN")) {
			return new ArrayList<>();
		}
		return jdbcTemplate.query("select * from tb_plat_manager where isDelete=0 and state='100' and login_name <> 'admin' ", new BeanPropertyRowMapper<PlatManager>(PlatManager.class));
	}
}
