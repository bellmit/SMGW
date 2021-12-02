package star.sms.smsmq.config;

/**
 * 消息队列常量配置
 * @author star
 *
 */
public class SmsmqConfig {

	/**
	 * 短信发送httptopic,tag
	 */
	public final static String SMS_SEND_HTTP="sms_send_http";
	/**
	 * 短信发送httpV2topic,tag
	 */
	public final static String SMS_SEND_HTTPV2="sms_send_httpV2";
	/**
	 * 短信发送httpV3topic,tag
	 */
	public final static String SMS_SEND_HTTPV3="sms_send_httpV3";
	/**
	 * 短信发送smpptopic,tag
	 */
	public final static String SMS_SEND_SMPP="sms_send_smpp";
	/**
	 * 短信发送cmpptopic,tag
	 */
	public final static String SMS_SEND_CMPP="sms_send_cmpp";

	
	
	/**
	 * 提交结果httptopic,tag
	 */
	public final static String SMS_RESULT_HTTP="sms_result_http";
	/**
	 * 提交结果httpV2topic,tag
	 */
	public final static String SMS_RESULT_HTTPV2="sms_result_httpV2";
	/**
	 * 提交结果httpV3topic,tag
	 */
	public final static String SMS_RESULT_HTTPV3="sms_result_httpV3";
	/**
	 * 提交结果smpptopic,tag
	 */
	public final static String SMS_RESULT_SMPP="sms_result_smpp";
	/**
	 * 提交结果cmpptopic,tag
	 */
	public final static String SMS_RESULT_CMPP="sms_result_cmpp";
	
	
	/**
	 * 回执状态httptopic,tag
	 */
	public final static String SMS_STAT_HTTP="sms_stat_http";
	/**
	 * 回执状态httpV2topic,tag
	 */
	public final static String SMS_STAT_HTTPV2="sms_stat_httpV2";
	/**
	 * 回执状态httpV3topic,tag
	 */
	public final static String SMS_STAT_HTTPV3="sms_stat_httpV3";
	/**
	 * 回执状态smpptopic,tag
	 */
	public final static String SMS_STAT_SMPP="sms_stat_smpp";
	/**
	 * 回执状态cmpptopic,tag
	 */
	public final static String SMS_STAT_CMPP="sms_stat_cmpp";
}
