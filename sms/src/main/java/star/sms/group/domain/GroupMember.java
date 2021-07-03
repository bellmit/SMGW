package star.sms.group.domain;

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
 * 通讯录
 * @author
 */
@Data
@Entity
@Table(name="tb_group_member")
public class GroupMember extends BaseModel {
	private static final long serialVersionUID = 1L;
	@Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "groupId") private Integer groupId;//通讯录分组id
    @Column(name = "phone") private String phone;//手机号码
    @Column(name = "name") private String name;//姓名
    @Column(name = "birthday") private Timestamp birthday;//生日
    @Column(name = "address") private String address;//联系地址
    @Column(name = "company") private String company;//公司
    @Column(name = "qq") private String qq;//qq
    @Column(name = "memo") private String memo;//备注
    @Column(name = "createTime") private Timestamp createTime;//创建时间
    @Column(name = "createUserId") private Integer createUserId;//创建人
    @Column(name = "updateTime") private Timestamp updateTime;//修改时间
    @Column(name = "updateUserId") private Integer updateUserId;//修改人
    
    @Transient private String birthdayStr;
}
