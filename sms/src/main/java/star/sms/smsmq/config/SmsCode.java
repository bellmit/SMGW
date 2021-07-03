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
	}
}
