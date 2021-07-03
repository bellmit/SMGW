package star.sms.wallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import star.sms._frame.base.BaseModel;

/**
 * 钱包信息
 * @author 
 */
@Data
@Entity
@Table(name="tb_wallet")
public class Wallet extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id
	@Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(name="userId") private Integer userId;
	@Column(name="money") private BigDecimal money;
}
