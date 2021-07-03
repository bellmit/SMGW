package star.sms.operation_log.pojo;


import java.io.Serializable;
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
 * @author star
 */
@Data
@Entity
@Table(name="tb_data_operation_log")
public class DataOperationLog extends BaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;//日志id
	@Column(name="create_user_id") private Integer createUserId;//操作人id
	@Column(name="create_user_name") private String createUserName;//操作人姓名
	@Column(name="create_time") private Timestamp createTime;//创建时间
	@Column(name="system_flag") private Integer systemFlag;//系统标识(1.实验室管理系统)
	@Column(name="module_flag")  private Integer moduleFlag;//0.系统设置  1.设备管理，2.案件管理 3.检材管理
	@Column(name="action_type") private Integer actionType;//操作类型(0:新增；1.导出 2:删除；3:借出；4:归还 5修改 6审核 7指派 8归档 9申请 10入库 11清退 12重新送检)
	@Column(name="operation") private String operation;//操作详情
	@Column(name="is_delete") private Integer isDelete;//是否删除(0:未删除；1:已删除)
}
