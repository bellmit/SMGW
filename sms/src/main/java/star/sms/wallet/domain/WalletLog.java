package star.sms.wallet.domain;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import star.sms._frame.base.BaseModel;

/**
 * 钱包信息-提现记录
 * @author songRT
 */
@Data
@Entity
@Table(name="tb_wallet_log")
public class WalletLog extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id
	@Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(name="userId") private Integer userId;
	@Column(name="walletId") private Integer walletId;
	@Column(name="money") private BigDecimal money;
	@Column(name="oldMoney") private BigDecimal oldMoney;//修改前金额
	@Column(name="memo") private String memo;
	@Column(name="createTime") private Timestamp createTime;
}
