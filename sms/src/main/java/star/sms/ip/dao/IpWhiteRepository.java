package star.sms.ip.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import star.sms._frame.base.BaseRepository;
import star.sms.ip.domain.IpWhite;

/**
 * ip白名单配置
 * @author star
 */
@Repository
public interface IpWhiteRepository extends BaseRepository<IpWhite>{

	List<IpWhite> findByIp(String ip);
}
