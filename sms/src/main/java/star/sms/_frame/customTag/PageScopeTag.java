package star.sms._frame.customTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import star.sms._frame.utils.Variant;

/**
 * @author star
 */
public class PageScopeTag implements TemplateDirectiveModel {

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		SimpleScalar _totalPages = (SimpleScalar) params.get("totalPages");
		SimpleScalar _currentPage = (SimpleScalar) params.get("currentPage");
		int totalPages = Variant.valueOf(_totalPages.getAsString()).intValue();
		int currentPage = Variant.valueOf(_currentPage.getAsString()).intValue();
		int start = 1;
		int end = totalPages;
		if(currentPage+1-2>=1){
			start = currentPage+1-2;
		}
		if(start+4<=totalPages){
			end = start+4;
		}
		List<Integer> pageScope = new ArrayList<Integer>();
		for(int i=start; i<=end; i++){
			pageScope.add(i);
		}
		DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_25);
		env.setVariable("pageScope", builder.build().wrap(pageScope));
		body.render(env.getOut());
	}

}
