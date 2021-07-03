package star.sms._frame.utils;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.Session;

public class CommandUtils {
	public static Map<String, Session> sessions = new HashMap<String, Session>();
	
	public final static String CLIENT_ACK_LOGIN="ack_login";//门口机登录
	public final static String CLIENT_ACK_ALIVE="ack_alive";//门口机心跳包 20s
	public final static String CLIENT_ACK_UPLOAD="ack_upload";//设备上报 Mac
	public final static String CLIENT_ICCARD_ADD_ECHO="iccard_add_echo";//门口机回码
	public final static String CLIENT_ICCARD_REMOVE_ECHO="iccard_remove_echo";//门口机回码
	public final static String CLIENT_FINGERPRINT_ADD_ECHO="fingerprint_add_echo";//门口机回码
	public final static String CLIENT_FINGERPRINT_REMOVE_ECHO="fingerprint_remove_echo";//门口机回码

	
	public final static String SERVER_ACK_SIGNATURE="ack_signature";//websocket连接后返回
	public final static String SERVER_REPLY_LOGIN="reply_login";//服务器验证成功后返回
	public final static String SERVER_REPLY_ALIVE="reply_alive";//服务器返回心跳
	public final static String SERVER_ICCARD_ADD="iccard_add";//服务器下发 IC 卡
	public final static String SERVER_ICCARD_REMOVE="iccard_remove";//服务器下发 IC 卡
	public final static String SERVER_FINGERPRINT_ADD="fingerprint_add";//服务器下发指纹
	public final static String SERVER_FINGERPRINT_REMOVE="fingerprint_remove";//服务器删除指纹

}
