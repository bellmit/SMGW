package star.sms.sms.vo;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import star.sms._frame.utils.excel.annotation.ExcelAttribute;
import star.sms._frame.utils.excel.annotation.ExcelAttributeHandle;

@Data
public class SmsLogParam implements Serializable  {
	private static final long serialVersionUID = 1L;
	@ExcelAttribute(name = "mid", sort = 0)
	private String mid;
	@ExcelAttribute(name = "用户", sort = 1)
	private String nickName;
	@ExcelAttribute(name = "线路名称", sort = 2)
	private String channelName;
	@ExcelAttribute(name = "手机号码", sort = 3)
	private String phone;
	@ExcelAttribute(name = "归属运营地", sort = 4)
	private String isp;
	@ExcelAttribute(name = "发送内容", sort = 5)
	private String content;
	@ExcelAttribute(name = "发送时间", sort = 6,format = ExcelAttributeHandle.TIME)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Timestamp sendTime;
	@ExcelAttribute(name = "报告时间", sort = 7,format = ExcelAttributeHandle.TIME)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Timestamp statTime;
	@ExcelAttribute(name = "发送状态", sort = 8,format = ExcelAttributeHandle.SENDSTATUS)
	private Integer sendStatus;
	@ExcelAttribute(name = "发送结果", sort = 9,format = ExcelAttributeHandle.SENDSTAT)
	private String sendStat;//投递结果，查看statMap
	
	private String keyword;
	private String beginTime;
	private String endTime;
	private Integer taskId;
	private Integer id;
	private String batchId;
	private Integer sendResult;
	private Timestamp createTime;
	private Integer createUserId;
	private Timestamp updateTime;
	private Integer updateUserId;
    private String password;//密码
    private String extno;//接入号
    private String memo;//memo
    private Integer accountId;
	private String account;
	private String fileName;
	private String fileType;
}
