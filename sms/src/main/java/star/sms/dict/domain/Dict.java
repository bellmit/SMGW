package star.sms.dict.domain;

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
 * 数据字典
 * @author star
 */
@Data
@Entity
@Table(name="tb_dict")
public class Dict extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id
	@Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(name="dict_name") private String dictName;//字典内容
	@Column(name="dict_type") private String dictType;//字典类型 唯一且不重复
	@Column(name="dict_index") private Integer dictIndex;//字典序号
	@Column(name="dict_value") private String dictValue;//字典值
	@Column(name="sys_tag") private Integer sysTag;//1一级字典 0二级字典
	@Column(name="create_user_id") private Integer createUserId;
	@Column(name="create_time") private Timestamp createTime;
	@Column(name="superior") private String superior;
}
