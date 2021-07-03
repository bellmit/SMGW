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
 * 短信模板表
 * @author
 */
@Data
@Entity
@Table(name="tb_sms_template")
public class SmsTemplate extends BaseModel {
	@Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "title") private String title;//标题
    @Column(name = "content") private String content;//模板内容
    @Column(name = "approveStatus") private Integer approveStatus;//审批状态 0待审批 1审批通过 2审批不通过
    @Column(name = "memo") private String memo;//拒绝原因
    @Column(name = "smsSize") private Integer smsSize;//字数
    @Column(name = "createTime") private Timestamp createTime;//创建时间
    @Column(name = "createUserId") private Integer createUserId;//创建人
    @Column(name = "updateTime") private Timestamp updateTime;//修改时间
    @Column(name = "updateUserId") private Integer updateUserId;//修改人
    
    @Transient
    private String nickName;//昵称
    
}
