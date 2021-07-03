package star.sms._frame.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Description
 * @auther 
 * @create 2019-05-07 12:48
 * 硬盘异常
 */
public class DiskException extends AuthenticationException {
    public DiskException(String message) {
        super(message);
    }
}
