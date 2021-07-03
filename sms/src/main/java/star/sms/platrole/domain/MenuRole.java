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
 * 运营平台菜单角色中间表类
 * @author star
 */
@Data
@Entity
@Table(name = "tb_op_menu_role")
public class MenuRole extends BaseModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;//主键
    @Column(name = "menu_id") private Integer menuId;//菜单主键
    @Column(name = "role_id") private Integer roleId;//角色主键
}
