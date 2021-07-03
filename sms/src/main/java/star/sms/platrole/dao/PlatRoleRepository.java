package star.sms.platrole.dao;

import org.springframework.stereotype.Repository;

import star.sms._frame.base.BaseRepository;
import star.sms.platrole.domain.PlatRole;

/**
 * 平台角色
 * @author star
 */
@Repository
public interface PlatRoleRepository extends BaseRepository<PlatRole> {

	PlatRole findTop1ByRoleCode(String roleCode);
}
