package star.sms.sms.vo;

import lombok.Data;

@Data
public class SmsDateParam {
	private String day;//日期
	private Integer totalCount;//发送数量
	private Integer successCount;//成功条数
	private Integer failCount;//失败条数
	private Integer sendingCount;//发送中
	private Integer unknowCount;//未知条数
}
