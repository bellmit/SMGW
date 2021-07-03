package star.sms.ip.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import star.sms._frame.base.BaseModel;

/**
 * IP白名单
 * @author star
 */
@Data
@Entity
@Table(name = "tb_ip_white")
public class IpWhite extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private String id;// 序号
	@Column(name="ip") private String ip;
	@Column(name="visit_count") private Integer visitCount;//访问次数

}
