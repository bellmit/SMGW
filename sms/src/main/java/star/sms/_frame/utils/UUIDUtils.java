package star.sms._frame.utils;

import java.util.UUID;

public class UUIDUtils {
	public static String random() {
		String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
		return uuid;
	}
}
