package star.sms._frame.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Description
 * @auther 
 * @create 2019-05-07 12:48
 * 时间过期 异常
 */
public class TimeExpireException extends AuthenticationException {
    public TimeExpireException(String message) {
        super(message);
    }
}
