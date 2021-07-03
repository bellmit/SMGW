package star.sms.wallet.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import star.sms._frame.utils.excel.annotation.ExcelAttribute;
import star.sms._frame.utils.excel.annotation.ExcelAttributeHandle;

/**
 * 充值记录
 * @author star
 */
@Data
public class WalletLogParams implements Serializable {
	private static final long serialVersionUID = 1L;
	@ExcelAttribute(name = "登录名", sort = 0)
	private String loginName;
	@ExcelAttribute(name = "名称", sort = 1)
	private String nickName;
	@ExcelAttribute(name = "金额", sort = 2)
	private BigDecimal money;
	@ExcelAttribute(name = "备注", sort = 3)
	private String memo;
	@ExcelAttribute(name = "时间", sort = 4,format = ExcelAttributeHandle.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private Timestamp createTime;//时间
	
	private Integer id;
}
