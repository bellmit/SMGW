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
 * 平台部门表
 * @author star
 */
@Data
@Entity
@Table(name = "tb_plat_group")
public class PlatGroup extends BaseModel {
	/**
	 * 主键:部门id
	 */
	@Id
	@Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Integer groupId;
	@Column(name = "group_name") private String groupName;// 部门名称
	@Column(name = "parent_group_id") private Integer parentGroupId;//父级部门ID
	@Column(name = "group_code") private String groupCode;//部门编码
	@Column(name = "group_path") private String groupPath;//部门层级路径(前后英文逗号分割)
}
