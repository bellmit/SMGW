package star.sms.wallet.dao;

import org.springframework.stereotype.Repository;

import star.sms._frame.base.BaseRepository;
import star.sms.wallet.domain.Wallet;

@Repository
public interface WalletDao extends BaseRepository<Wallet> {

}
