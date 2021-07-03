package star.sms.group.vo;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import star.sms._frame.utils.excel.annotation.ExcelAttribute;
import star.sms._frame.utils.excel.annotation.ExcelAttributeHandle;

/**
 * 通讯录
 * @author star
 */
@Data
public class GroupMemberParam implements Serializable {
	private static final long serialVersionUID = 1L;
	@ExcelAttribute(name = "分组", sort = 0)
	private String groupName;
	@ExcelAttribute(name = "手机号码", sort = 1)
	private String phone;
	@ExcelAttribute(name = "姓名", sort = 2)
	private String name;
	@ExcelAttribute(name = "生日", sort = 3,format = ExcelAttributeHandle.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private Timestamp birthday;//时间
	@ExcelAttribute(name = "联系地址", sort = 4)
	private String address;
	@ExcelAttribute(name = "公司", sort = 5)
	private String company;
	@ExcelAttribute(name = "qq", sort = 6)
	private String qq;
	@ExcelAttribute(name = "备注", sort = 7)
	private String memo;
	@ExcelAttribute(name = "创建时间", sort = 8,format = ExcelAttributeHandle.TIME)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Timestamp createTime;//时间
	
	private Integer id;
}
