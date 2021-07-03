package star.sms.notify.domain;

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
 * 系统公告
 * @author
 */
@Data
@Entity
@Table(name="tb_notify_user")
public class NotifyUser extends BaseModel {
	private static final long serialVersionUID = 1L;
	@Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "notifyId") private Integer notifyId;//公告id
    @Column(name = "userId") private Integer userId;//接收人id
    @Column(name = "isRead") private Integer isRead;//是否已读
    @Column(name = "isDelete") private Integer isDelete;//是否删除
    @Column(name = "readTime") private Timestamp readTime;//阅读时间
    
}
