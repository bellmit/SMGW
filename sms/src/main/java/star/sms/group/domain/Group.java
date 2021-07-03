package star.sms.group.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import star.sms._frame.base.BaseModel;

/**
 * 通讯录-分组
 * @author
 */
@Data
@Entity
@Table(name="tb_group")
public class Group extends BaseModel {
	private static final long serialVersionUID = 1L;
	@Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "groupName") private String groupName;//分组名称
    @Column(name = "createTime") private Timestamp createTime;//创建时间
    @Column(name = "createUserId") private Integer createUserId;//创建人
    @Column(name = "updateTime") private Timestamp updateTime;//修改时间
    @Column(name = "updateUserId") private Integer updateUserId;//修改人
    
}
