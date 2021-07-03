package star.sms.notify.vo;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 通讯录
 * @author star
 */
@Data
public class NotifyParam implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer isRead;
	private Integer notifyId;
	private Timestamp createTime;//时间
	private String createName;
	private String title;
}
