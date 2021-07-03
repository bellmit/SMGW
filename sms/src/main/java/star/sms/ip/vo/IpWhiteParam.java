package star.sms.ip.vo;

import java.io.Serializable;

import lombok.Data;
import star.sms._frame.utils.excel.annotation.ExcelAttribute;

@Data
public class IpWhiteParam implements Serializable {
	private static final long serialVersionUID = 1L;

	@ExcelAttribute(name = "IP", sort = 0)
	private String ip;
	@ExcelAttribute(name = "访问次数", sort = 1)
	private Integer visitCount;
	
	private Integer id;
}
