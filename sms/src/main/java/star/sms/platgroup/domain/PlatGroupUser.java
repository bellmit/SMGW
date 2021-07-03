package star.sms.platgroup.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import star.sms._frame.base.BaseModel;

/**
 * 平台部门人员中间表
 * @author star
 */
@Data
@Entity
@Table(name="tb_plat_group_user")
public class PlatGroupUser extends BaseModel {
	
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "group_id") private Integer groupId;
    @Column(name = "user_id") private Integer userId;
}
