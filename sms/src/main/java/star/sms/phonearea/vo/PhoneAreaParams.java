package star.sms.phonearea.vo;

import java.io.Serializable;

import lombok.Data;
import star.sms._frame.utils.excel.annotation.ExcelAttribute;

/**
 * 区域号段
 * @author star
 */
@Data
public class PhoneAreaParams implements Serializable {
	private static final long serialVersionUID = 1L;
	@ExcelAttribute(name = "前3位", sort = 0)
	private String pref;
	@ExcelAttribute(name = "前7位", sort = 1)
	private String phone;
	@ExcelAttribute(name = "省份", sort = 2)
	private String province;
	@ExcelAttribute(name = "地市", sort = 3)
	private String city;
	@ExcelAttribute(name = "运营商", sort = 4)
	private String isp;
	
	private Integer id;
}
