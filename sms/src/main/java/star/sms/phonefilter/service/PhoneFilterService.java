package star.sms.phonefilter.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.account.domain.AccountInfo;
import star.sms.account.service.AccountService;
import star.sms.phonefilter.dao.PhoneFilterRepository;
import star.sms.phonefilter.domain.PhoneFilter;

/**
 * 拦截策略配置
 * @author star
 */
@Slf4j
@Service
@Transactional
public class PhoneFilterService extends BaseServiceProxy<PhoneFilter> {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Resource
	private PhoneFilterRepository phoneFilterRepository;
	
	@Resource
	private EntityManager entityManager;
	
	@Autowired
	private AccountService accountService;
	
	@Override
	protected BaseRepository<PhoneFilter> getBaseRepository() {
		return phoneFilterRepository;
	}
	
	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * 分页查
	 * @param keyword
	 * @param loginUser
	 * @param pageable
	 * @return
	 */
	public Page findByPage(String keyword, Pageable pageable) {
		StringBuffer fromWhereSql = new StringBuffer();
		fromWhereSql.append(" FROM tb_phone_filter where 1=1 ");
		if(StringUtils.isNotEmpty(keyword)) {
			fromWhereSql.append("  and title like '%"+keyword+"%' ");
		}
		Page<PhoneFilter> page = super.findPageBySql(PhoneFilter.class, "select * ", fromWhereSql.toString(), " order by id asc", null, pageable);

		List<AccountInfo> accountList = accountService.findAccountInfoList();
		Map<Integer, String> accountMap = accountList.stream().collect(Collectors.toMap(AccountInfo::getId, AccountInfo::getTitle));
		for (PhoneFilter info : page.getContent()) {
			info.setChannelName(accountMap.get(info.getAccountId())==null?"":accountMap.get(info.getAccountId()));
		}
		return page;
	}
	
	
	/**
	 * 批量删除信息
	 * @param ids
	 */
	public void deleteByIds(String ids) {
		if(ids.endsWith(",")) ids=ids.substring(0,ids.length()-1);
		jdbcTemplate.update("delete from tb_phone_filter where id in ("+ids+")");
	}
}
