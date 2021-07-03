package star.sms._frame.customTag;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import freemarker.template.Configuration;

/**
 * @author star
 */
@Component
public class FreemarkerConfig {

	@Resource
	private Configuration configuration;
	
	@PostConstruct
	public void init(){
		configuration.setSharedVariable("pagescope", new PageScopeTag());
	}
}
