package star.sms.platmanager.domain;

import java.math.BigDecimal;
import java.util.Date;

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
 * 平台用户(登录用)
 * @author star
 */
@Data
@Entity
@Table(name="tb_plat_manager")
public class PlatManager extends BaseModel {
	/**
	 * 主键
	 */
	@Id
	@Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(name="login_name") private String loginName;//登录名
	@Column(name="password") private String password;//密码
	@Column(name="company_id") private String companyId;//物业公司ID
	@Column(name="nick_name") private String nickName;//昵称(代理商用户显示代理商名称，内部员工显示员工姓名)
	@Transient private String roleName;//角色姓名
	@Transient private String roleCode;//角色姓名
	@Column(name="state") private Integer state;//状态(100:启用,200:禁用)
	@Column(name="create_time") private Date createTime;//创建时间
	@Column(name="create_id") private Integer createId;//创建人ID
	@Column(name="creater_name") private String createrName;//创建人姓名
	@Column(name="is_sub_account") private Integer isSubAccount;//是否子账号(0和null不是, 1是)
    @Column(name="phone") private String phone;//联系电话
    @Column(name="price") private BigDecimal price;
    @Column(name="last_login_time")private Date lastLoginTime;//最后登录时间
    @Column(name="isDelete") private Integer isDelete;
    @Column(name="priority") private Integer priority;//优先级 默认是0 越大发送短信的优先级别越高
    @Transient private String companyName;
    @Transient private BigDecimal money;
}
