package star.sms.phonearea.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import star.sms._frame.base.BaseModel;

/**
 * 号码归属地
 * @author star
 *
 */
@Data
@Entity
@Table(name = "tb_phone_area")
public class PhoneArea extends BaseModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;//主键
	@Column(name="pref") private String pref; //前3位
	@Column(name="phone") private String phone; //前7位
	@Column(name="province") private String province; //省份
	@Column(name="city") private String city; //地市
	@Column(name="isp") private String isp; //运营商
	@Column(name="post_code") private String postCode; //邮编
	@Column(name="city_code") private String cityCode; //区号
	@Column(name="area_code") private String areaCode; //地市编码
}
