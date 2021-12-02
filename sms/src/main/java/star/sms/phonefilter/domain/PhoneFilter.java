package star.sms.phonefilter.domain;

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
 * 拦截策略配置
 * @author star
 *
 */
@Data
@Entity
@Table(name = "tb_phone_filter")
public class PhoneFilter extends BaseModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;//主键
	@Column(name="title") private String title; //策略名称
	@Column(name="keyword") private String keyword; //关键词列表
	@Column(name="phones") private String phones; //号码拦截
	@Column(name="areas") private String areas; //地区号段拦截
	@Column(name="createUserId") private Integer createUserId;
	@Column(name="createTime") private Timestamp createTime;
	@Column(name="updateUserId") private Integer updateUserId;
	@Column(name="updateTime") private Timestamp updateTime;
	@Column(name="accountId") private Integer accountId;//线路Id 非必填，不填或者为0则代表适用于所有的用户
	
	@Transient private String channelName;
}
