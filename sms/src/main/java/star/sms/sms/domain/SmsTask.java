package star.sms.sms.domain;

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
 * 短信任务表
 * @author
 */
@Data
@Entity
@Table(name="tb_sms_task")
public class SmsTask extends BaseModel {
	@Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	@Column(name = "contentType") private Integer contentType;//1不变内容 2可变模板需要字符替换
    @Column(name = "title") private String title;//名称
    @Column(name = "content") private String content;//模板内容
	@Column(name = "sendStatus") private Integer sendStatus;//-1是临时任务，不能发送的 0待发送 1发送成功 2发送失败3已终止 4发送中
	@Column(name = "priority") private Integer priority;//优先级 默认0，优先级越大发送越靠前
	@Column(name = "channelType") private Integer channelType;//通道 1 http,2 smpp,3 cmpp
    @Column(name = "sendTime") private Timestamp sendTime;//发送时间
    @Column(name = "createTime") private Timestamp createTime;//创建时间
    @Column(name = "createUserId") private Integer createUserId;//创建人
    @Column(name = "updateTime") private Timestamp updateTime;//修改时间
    @Column(name = "updateUserId") private Integer updateUserId;//修改人
    @Column(name = "nickName") private String nickName;//用户昵称
}
