package star.sms._frame.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Description
 * @auther 
 * @create 2019-05-07 12:48
 * 授权码 异常
 */
public class AuthorizationCodeException extends AuthenticationException {
    public AuthorizationCodeException(String message) {
        super(message);
    }
}
