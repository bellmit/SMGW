package star.sms.account.dao;

import org.springframework.stereotype.Repository;

import star.sms._frame.base.BaseRepository;
import star.sms.account.domain.AccountInfo;

@Repository
public interface AccountDao extends BaseRepository<AccountInfo> {
}
