package star.sms.sms.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import lombok.Data;
import star.sms._frame.base.BaseModel;

/**
 * 短信批次表
 * @author
 */
@Data
@Entity
@Table(name="tb_sms_batch")
public class SmsBatch extends BaseModel {
	@Id
	@Column(name="id")
	@GeneratedValue(generator="sms_batch_uuid")
	@GenericGenerator(name="sms_batch_uuid",strategy="uuid")
    private String id;
	@Column(name = "taskId") private Integer taskId;//任务id
    @Column(name = "content") private String content;//模板内容
	@Column(name = "sendStatus") private Integer sendStatus;//发送状态,0待发送 1发送成功 2发送失败3已终止 4发送中
	@Column(name = "sendResult") private Integer sendResult;//服务器返回状态
    @Column(name = "sendTime") private Timestamp sendTime;//发送时间
    @Column(name = "createTime") private Timestamp createTime;//创建时间
    @Column(name = "createUserId") private Integer createUserId;//创建人
    @Column(name = "updateTime") private Timestamp updateTime;//修改时间
    @Column(name = "updateUserId") private Integer updateUserId;//修改人
    
}
