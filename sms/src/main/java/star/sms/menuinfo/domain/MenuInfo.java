package star.sms.menuinfo.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import star.sms._frame.base.BaseModel;
/**
 * 菜单表
 * @author star
 */
@Data
@Entity
@Table(name = "tb_op_menu_info")
public class MenuInfo extends BaseModel {
    @Id
    @Column(name = "menu_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer menuId;// 菜单id
    @Column(name = "menu_name") private String menuName;//菜单名称
    @Column(name = "menu_icon") private String menuIcon;//菜单图标
    @Column(name = "menu_index") private Integer menuIndex;//菜单排序
    @Column(name = "menu_url") private String menuUrl;//菜单url
    @Column(name = "menu_level") private Integer menuLevel;//菜单级别
    @Column(name = "menu_parent_id") private Integer menuParentId;//父菜单id
    @Column(name = "menu_status") private Integer menuStatus;//菜单状态 1 显示 0 不显示
    @Column(name = "menu_code") private String menuCode;//唯一标识，用来左侧菜单是否显示
    @Column(name = "is_jump_manage") private Integer isJumpManage;//是否跳manage 0 不跳，1 跳
    @Column(name = "request_type") private String requestType;//跳转manage类型
}
