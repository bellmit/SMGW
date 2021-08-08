package star.sms.smsmq.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信常量代码
 * 
 * @author star
 *
 */
public class SmsCode {
	// STATUS错误代码表
	public static Map<Integer, String> statusMap = new HashMap<Integer, String>();

	// RESULT错误代码表
	public static Map<Integer, String> resultMap = new HashMap<Integer, String>();
	
	// STAT状态代码表
	public static Map<String, String> statMap = new HashMap<String, String>();
	
	// SMPP状态代码表
	public static Map<String, String> smppMap = new HashMap<String, String>();
	
	static {
		// STATUS错误代码表
		statusMap.put(0, "成功");
		statusMap.put(1, "消息包格式错误");
		statusMap.put(2, "IP鉴权错误");
		statusMap.put(3, "账号密码不正确");
		statusMap.put(4, "版本号错误");
		statusMap.put(5, "其它错误");
		statusMap.put(6, "接入点错误（如账户本身开的是CMPP接入）");
		statusMap.put(7, "账号状态异常（账号已停用）");
		statusMap.put(12, "号码不能为空");
		statusMap.put(20, "内容不能为空");
		statusMap.put(10, "接入码错误");
		statusMap.put(15, "余额不足");
		statusMap.put(17, "签名错误");
		statusMap.put(21, "连接过多");
		statusMap.put(100, "系统内部错误");
		statusMap.put(102, "单次提交的号码数过多（建议200以内）");
		// RESULT错误代码表
		resultMap.put(0, "发送成功");
		resultMap.put(10, "原发号码错误，即extno错误");
		resultMap.put(12, "检查接收短信手机号码格式是否正确");
		resultMap.put(15, "余额不足");
		resultMap.put(17, "账号签名无效");
		// STAT状态代码表
		statMap.put("DELIVRD", "短信投递成功");
		statMap.put("EXPIRED", "短信过期");
		statMap.put("DELETED", "已被删除");
		statMap.put("REJECTED", "已被拒绝");
		statMap.put("MA:0001", "全局黑名单号码");
		statMap.put("MA:0002", "内容非法");
		statMap.put("MA:0003", "无法找到下级路由");
		statMap.put("MA:0004", "未知");
		statMap.put("MA:0005", "目的号码格式错误");
		statMap.put("MA:0006", "系统拒绝");
		statMap.put("MA:0009", "未定义错误");
		statMap.put("MA:00011", "未知系统内部错误");
		statMap.put("MA:00012", "防钓鱼");
		statMap.put("MA:00013", "非法错误的包时序");
		statMap.put("MA:00014", "非法的OP_ISDN号段");
		statMap.put("MA:00021", "号码格式错误");
		statMap.put("MA:00022", "号码超过半小时下发次数限制");
		statMap.put("MA:00023", "客户黑名单号码");
		statMap.put("MA:00024", "内容未报备");
		statMap.put("MA:00025", "不支持该短信");
		statMap.put("MA:00026", "分条发送，组包超时");
		statMap.put("MA:00027", "通道黑名单");
		statMap.put("MA:00028", "全局黑名单号段");
		statMap.put("MA:00029", "通道黑名单号段");
		statMap.put("MA:00030", "直接产生拒绝报告");
		statMap.put("MO:200", "不支持分条短信");
		statMap.put("MO:0254", "转发提交超时");
		statMap.put("MO:0255", "转发提交过程中，连接断开");
		
		//所有map中未注明的状态都是Reserved状态 提交线路失败
		smppMap.put("-1", "通道尚未注册成功");
		smppMap.put("0", "发送成功");//No Error
		smppMap.put("1", "消息长度错误");//Message Length is invalid
		smppMap.put("2", "指令长度错误");//Command Length is invalid
		smppMap.put("3", "无效的指令id");//Invalid Command ID
		smppMap.put("4", "无效BIND");//Incorrect BIND Status for given command
		smppMap.put("5", "ESME已存在");//ESME Already in Bound State
		smppMap.put("6", "无效的私有标记");//Invalid Priority Flag
		smppMap.put("7", "无效的发送标记");//Invalid Registered Delivery Flag
		smppMap.put("8", "已发送，无返回结果");//System Error
		smppMap.put("9", "提交线路失败");//Reserved
		smppMap.put("10", "无效的源地址");//Invalid Source Address
		smppMap.put("11", "无效的目标地址");//Invalid Dest Addr
		smppMap.put("12", "Message ID无效");//Message ID is invalid
		smppMap.put("13", "绑定错误");//Bind Failed
		smppMap.put("14", "密码错误");//Invalid Password
		smppMap.put("15", "System ID错误");//Invalid System ID
		smppMap.put("16", "提交线路失败");//Reserved
		smppMap.put("17", "取消SM失败");//Cancel SM Failed
		smppMap.put("18", "提交线路失败");//Reserved
		smppMap.put("19", "替换SM失败");//Replace SM Failed
		smppMap.put("20", "消息队列已满");//Message Queue Full
		smppMap.put("21", "无效的消息类型");//Invalid Service Type
		smppMap.put("22", "提交线路失败");//Reserved
		smppMap.put("51", "目标号码数量错误");//Invalid number of destinations
		smppMap.put("52", "目标号码错误");//Invalid Distribution List name
		smppMap.put("64", "Destination flag is invalid(submit_multi)");
		smppMap.put("66", "Invalid ‘submit with replace’ request(i.e. submit_sm with replace_if_present_flag set)");
		smppMap.put("67", "Invalid esm_class field data");
		smppMap.put("68", "Cannot Submit to Distribution List");
		smppMap.put("69", "消息体格式错误");//submit_sm or submit_multi failed
		smppMap.put("72", "源地址TON错误");//Invalid Source address TON
		smppMap.put("73", "源地址NPI错误");//Invalid Source address NPI
		smppMap.put("80", "目标地址TON错误");//Invalid Destination address TON
		smppMap.put("81", "目标地址NPI错误");//Invalid Destination address NPI
		smppMap.put("83", "Invalid system_type field");
		smppMap.put("84", "Invalid replace_if_present flag");
		smppMap.put("85", "Invalid number of messages");
		smppMap.put("88", "Throttling error (ESME has exceeded allowed message limits)");
		smppMap.put("97", "Invalid Scheduled Delivery Time");
		smppMap.put("98", "Invalid message validity period(Expiry time)");
		smppMap.put("99", "Predefined Message Invalid or Not Found");
		smppMap.put("100", "ESME Receiver Temporary App Error Code");
		smppMap.put("101", "ESME Receiver Permanent App Error Code");
		smppMap.put("102", "ESME Receiver Reject Message Error Code");
		smppMap.put("103", "query_sm request failed");
		smppMap.put("192", "Error in the optional part of the PDU Body.");
		smppMap.put("193", "Optional Parameter not allowed");
		smppMap.put("194", "Invalid Parameter Length.");
		smppMap.put("195", "Expected Optional Parameter missing");
		smppMap.put("196", "Invalid Optional Parameter Value");
		smppMap.put("254", " Delivery Failure (used for data_sm_resp)");
		smppMap.put("255", "Unknown Error");
		smppMap.put("DELIVRD", "短信投递成功");
		smppMap.put("EXPIRED", "短信过期");
		smppMap.put("DELETED", "已被删除");
		smppMap.put("REJECTED", "已被拒绝");
	}
}
