package star.sms.attachment.domain;

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
 * 附件表
 * @author star
 */
@Data
@Entity
@Table(name="tb_attachment")
public class Attachment extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id
	@Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(name="file_name") private String fileName;//附件名称
	@Column(name="type") private String type;//附件类型
	@Column(name="file_path") private String filePath;//附件存储路径
	@Column(name="file_size") private String fileSize;//文件类型
	@Column(name="create_time") private Timestamp createTime;
	@Column(name="create_user_id") private Integer createUserId;
}
