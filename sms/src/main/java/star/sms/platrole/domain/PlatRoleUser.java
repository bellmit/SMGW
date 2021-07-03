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
 * 平台角色用户中间表类
 * @author star
 */
@Data
@Entity
@Table(name="tb_plat_role_user")
public class PlatRoleUser extends BaseModel {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "role_id") private Integer roleId;
    @Column(name = "user_id") private Integer userId;
}
