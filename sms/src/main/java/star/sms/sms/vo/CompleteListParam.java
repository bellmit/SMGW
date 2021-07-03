package star.sms.sms.vo;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 已完成的任务
 * @author
 */
@Data
public class CompleteListParam {
	private Integer taskId;
	private Integer sendStatus;
	private Timestamp sendTime;
	private Timestamp createTime;
	private Integer totalCount;
	private Integer successCount;
}
