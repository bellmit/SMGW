package star.sms._frame.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Description
 * @auther 
 * @create 2019-05-26 17:57
 * 授权码时间没找到异常
 */
public class TimeNotFoundException extends AuthenticationException {
    public TimeNotFoundException(String message) {
        super(message);
    }
}
