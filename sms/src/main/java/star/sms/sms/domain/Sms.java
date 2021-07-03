package star.sms.sms.domain;

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
 * 短信表
 * @author
 */
@Data
@Entity
@Table(name="tb_sms")
public class Sms extends BaseModel {
	@Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	@Column(name = "taskId") private Integer taskId;//任务id
	@Column(name = "batchId") private String batchId;//批次id
    @Column(name = "content") private String content;//短信内容
	@Column(name = "sendResult") private Integer sendResult;//发送结果
	@Column(name = "sendStatus") private Integer sendStatus;//0待发送 1发送成功 2发送失败3已终止 4发送中
	@Column(name = "sendStat") private String sendStat;//投递结果，查看statMap
	@Column(name = "channelType") private Integer channelType;//通道 1 http,2 smpp,3 cmpp
	@Column(name = "statTime") private Timestamp statTime;//通道回执时间
    @Column(name = "sendTime") private Timestamp sendTime;//发送时间
    @Column(name = "phone") private String phone;//手机号码
    @Column(name = "accountId") private String accountId;//账号id
    @Column(name = "account") private String account;//账号
    @Column(name = "password") private String password;//密码
    @Column(name = "extno") private String extno;//接入号
    @Column(name = "mid") private String mid;//mid
    @Column(name = "memo") private String memo;//注释
    @Column(name = "createTime") private Timestamp createTime;//创建时间
    @Column(name = "createUserId") private Integer createUserId;//创建人
    @Column(name = "updateTime") private Timestamp updateTime;//修改时间
    @Column(name = "updateUserId") private Integer updateUserId;//修改人
    
    @Transient
    private Integer contentType;//内容类型
    @Transient
    private String taskContent;//任务内容或模板
}
