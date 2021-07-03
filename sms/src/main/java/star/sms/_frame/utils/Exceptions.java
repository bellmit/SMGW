package star.sms._frame.utils;

/**
 * @author star
 */
public class Exceptions {

	public static RuntimeException code(String msg){
		return new RuntimeException(msg);
	}
	
	public static RuntimeException wrap(Throwable t){
		t.printStackTrace();
		return new RuntimeException("服务异常，请稍后重试!");
	}
}
