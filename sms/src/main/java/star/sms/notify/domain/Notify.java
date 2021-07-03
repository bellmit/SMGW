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
@Table(name="tb_notify")
public class Notify extends BaseModel {
	private static final long serialVersionUID = 1L;
	@Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "title") private String title;//标题
    @Column(name = "content") private String content;//内容 富文本内容
    @Column(name = "createTime") private Timestamp createTime;//发布时间
    @Column(name = "createName") private Integer createName;//发件人
    
}
