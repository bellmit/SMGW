package star.sms.account.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.common.util.concurrent.RateLimiter;

import lombok.Data;
import star.sms._frame.base.BaseModel;

/**
 * 短信账号信息表
 * @author
 */
@Data
@Entity
@Table(name="tb_account")
public class AccountInfo extends BaseModel  {
	@Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	@Column(name = "title") private String title;//名称
	@Column(name = "account") private String account;//账号
    @Column(name = "password") private String password;//密码
    @Column(name = "extno") private String extno;//接入号
	@Column(name = "price") private BigDecimal price;//单价
	@Column(name = "balance") private BigDecimal balance;//余额
	@Column(name = "alertBalance") private BigDecimal alertBalance;//余额告警
	@Column(name = "alertPhone") private String alertPhone;//通知手机号 逗号分割
	@Column(name = "accountStatus") private Integer accountStatus;//账号是否启用0未启用 1启用
    @Column(name = "createTime") private Timestamp createTime;//创建时间
    @Column(name = "createUserId") private Integer createUserId;//创建人
    @Column(name = "updateTime") private Timestamp updateTime;//修改时间
    @Column(name = "updateUserId") private Integer updateUserId;//修改人
    @Column(name = "isDelete") private Integer isDelete;
    @Column(name = "sendTime") private Timestamp sendTime;//最近发送时间
    @Column(name = "limiter") private Integer limiter;//网关限流
    @Column(name = "ip") private String ip;//网关地址 IP:PORT
    @Column(name = "channelType") private Integer channelType;//通道 1 http,2 smpp,3 cmpp

    @Transient
    private RateLimiter accountLimiter;
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AccountInfo))
			return false;	
		if (obj == this)
			return true;
		return this.account.equals(((AccountInfo) obj).account);
	}
}
