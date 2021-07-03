package star.sms.logs.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import star.sms._frame.base.BaseModel;

@Data
@Entity
@Table(name = "tb_logs")
public class Logs extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="log_xh")
	@GeneratedValue(generator="logs_uuid")
	@GenericGenerator(name="logs_uuid",strategy="uuid")
	private String logXh;// 序号
	@Column(name="log_nr") private String logNr;//内容
	@Column(name="log_tjr") private String logTjr;//添加人
	@Column(name="log_tjsj") private Timestamp logTjsj;//内容

}
