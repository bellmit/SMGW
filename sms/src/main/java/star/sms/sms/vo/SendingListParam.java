package star.sms.sms.vo;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 正在发送中任务
 * @author
 */
@Data
public class SendingListParam {
	private Integer taskId;
	private Integer sendStatus;
	private Timestamp sendTime;
	private Timestamp createTime;
	private Integer totalCount;
	private Integer successCount;
}
