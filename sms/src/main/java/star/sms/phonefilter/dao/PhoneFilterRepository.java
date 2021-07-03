package star.sms.phonefilter.dao;

import org.springframework.stereotype.Repository;

import star.sms._frame.base.BaseRepository;
import star.sms.phonefilter.domain.PhoneFilter;

/**
 * 拦截策略配置
 * @author star
 */
@Repository
public interface PhoneFilterRepository extends BaseRepository<PhoneFilter>{

}
