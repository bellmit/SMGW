package star.sms._frame.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Description
 * @auther 
 * @create 2019-05-26 17:57
 * 本地时间异常
 */
public class TimeErrorException extends AuthenticationException {
    public TimeErrorException(String message) {
        super(message);
    }
}
