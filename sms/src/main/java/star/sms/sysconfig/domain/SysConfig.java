package star.sms.sysconfig.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import star.sms._frame.base.BaseModel;

/**
 * 系统配置
 * @author star
 *
 */
@Data
@Entity
@Table(name = "tb_sys_config")
public class SysConfig extends BaseModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;//主键
	@Column(name="taskCount") private Integer taskCount; //每个用户最大任务个数
	@Column(name="logo") private String logo; //logo
	@Column(name="systemName") private String systemName; //名称
	@Column(name="logoHome") private String logoHome; //homelogo
}
