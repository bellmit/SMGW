package star.sms._frame.config;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import star.sms._frame.exception.CodeEmptyException;
import star.sms._frame.exception.CodeErrorException;
import star.sms._frame.exception.CodeTimeoutException;

/**
 * @author star
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;
	@Resource
	private UserDetailServiceImpl userDetailService;
	
	@Autowired
	private AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(customAuthenticationProvider);
		/*auth.userDetailsService(userDetailService).passwordEncoder(new PasswordEncoder() {
			
			Md5PasswordEncoder encoder = new Md5PasswordEncoder();
			@Override
			public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
				return encoder.encodePassword(rawPass, null).equals(encPass);
			}
			
			@Override
			public String encodePassword(String rawPass, Object salt) {
				return encoder.encodePassword(rawPass, null);
			}
		});*/
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().disable();
		http
		// 配置登录
		.formLogin()
			.loginPage("/login").loginProcessingUrl("/login")
			.usernameParameter("loginname").passwordParameter("password")
			.defaultSuccessUrl("/home", true)
			.authenticationDetailsSource(authenticationDetailsSource)//自定义验证逻辑，增加验证码信息
			.failureHandler(new AuthenticationFailureHandler() {
				
				@Override
				public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
						AuthenticationException exception) throws IOException, ServletException {
					String strUrl = null;
					if (exception instanceof DisabledException) {
			        	strUrl = request.getContextPath() + "/login?disabled";
			        } else if (exception instanceof UsernameNotFoundException) {
			        	strUrl = request.getContextPath() + "/login?notfound";	
			        } else if (exception instanceof BadCredentialsException) {
			        	strUrl = request.getContextPath() + "/login?badcredentials";
			        } else if (exception instanceof CodeEmptyException) {
			        	strUrl = request.getContextPath() + "/login?codeEmpty";
			        } else if (exception instanceof CodeErrorException) {
			        	strUrl = request.getContextPath() + "/login?codeError";
			        } else if (exception instanceof CodeTimeoutException) {
			        	strUrl = request.getContextPath() + "/login?codeTimeout";
			        } else {
			        	strUrl = request.getContextPath() + "/login?error";
			        }
					response.sendRedirect(strUrl);
				}
			})
		// 配置退出
		.and().logout()
			.logoutUrl("/logout").logoutSuccessUrl("/login")
		// 配置请求拦截
		.and().authorizeRequests()
			.antMatchers("/login**").permitAll()
			.antMatchers("/login/captcha**").permitAll()
			.antMatchers("/ip/white/init**").permitAll()
			.antMatchers("/logout**").permitAll()
			.antMatchers("/entrust/**").permitAll()
			.antMatchers("/sample/**").permitAll()
			.antMatchers("/attachment/**").permitAll()
			.anyRequest().authenticated()
		// 配置是否启用CSFF
		.and().csrf()
			.disable();
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/fonts/**","/img/**","/css/**","/js/**","/plugins/**");
	}
}
