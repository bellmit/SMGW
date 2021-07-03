package star.sms.config;

import java.io.File;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import star.sms._frame.utils.IdWorker;

/**
 * @author star
 */
@Configuration
public class CommonConfig {
	@Autowired
	private SystemConfig systemConfig;
	
    @Bean
    public IdWorker idWorkker(){
        return new IdWorker(1, 1);
    }
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(100000);
        requestFactory.setReadTimeout(100000);
        return new RestTemplate(requestFactory);
    }

    /**
     * 文件上传临时路径
     */
    @Bean
    MultipartConfigElement multipartConfigElement() {
        File file = new File(systemConfig.getTempdir());
        if (!file.exists()) {
            file.mkdirs();
        }
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation(systemConfig.getTempdir());
        return factory.createMultipartConfig();
    }
}
