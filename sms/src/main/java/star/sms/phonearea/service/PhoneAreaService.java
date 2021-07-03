package star.sms.phonearea.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import star.sms._frame.base.BaseRepository;
import star.sms._frame.base.BaseServiceProxy;
import star.sms.phonearea.dao.PhoneAreaRepository;
import star.sms.phonearea.domain.PhoneArea;
import star.sms.phonearea.vo.PhoneAreaParams;

/**
 * 电话归属地
 * @author star
 */
@Slf4j
@Service
@Transactional
public class PhoneAreaService extends BaseServiceProxy<PhoneArea> {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
    @Autowired
    private RedissonClient redissonClient;
	
	@Resource
	private PhoneAreaRepository phoneAreaRepository;
	
	@Resource
	private EntityManager entityManager;
	
	@Override
	protected BaseRepository<PhoneArea> getBaseRepository() {
		return phoneAreaRepository;
	}
	
	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}
	/**
	 * 存取归属地到redis，使用pb结构
	 */
	@PostConstruct
	public void savePhoneToRedis() {
		log.info(" 开始写入归属地到REDIS ");
		int i=0;
		RMap<String,PhoneArea> map =  redissonClient.getMap("phoneAreaMap");
		if(!map.isExists()) {
			Iterable<PhoneArea> phoneAreaIterable =  phoneAreaRepository.findAll();
			if(phoneAreaIterable!=null) {
				for (PhoneArea phoneArea : phoneAreaIterable) {
					map.putIfAbsent(phoneArea.getPhone(), phoneArea);
					i++;
				}
			}
		}
		log.info(" 结束写入归属地到REDIS，数量: " + i);
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
		fromWhereSql.append(" FROM tb_phone_area where 1=1 ");
		if(StringUtils.isNotEmpty(keyword)) {
			fromWhereSql.append("  and (pref like '%"+keyword+"%' or phone like '%"+keyword+"%' or province like '%"+keyword+"%' or city like '%"+keyword+"%' or isp like '%"+keyword+"%') ");
		}
		Page<PhoneAreaParams> page = super.findPageBySql(PhoneAreaParams.class, "select * ", fromWhereSql.toString(), " order by id asc", null, pageable);
		return page;
	}
	
	
	/**
	 * 批量删除信息
	 * @param ids
	 */
	public void deleteByIds(String ids) {
		if(ids.endsWith(",")) ids=ids.substring(0,ids.length()-1);
		jdbcTemplate.update("delete from tb_phone_area where id in ("+ids+")");
	}
}
