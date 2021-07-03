package star.sms.platrole.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import star.sms._frame.base.BaseModel;

/**
 * 平台角色
 * @author star
 */
@Data
@Entity
@Table(name="tb_plat_role")
public class PlatRole extends BaseModel {
	@Id
	@Column(name="role_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Integer roleId;
	@Column(name="role_code") private String roleCode;//角色编码
	@Column(name="role_name") private String roleName;//角色名
}
