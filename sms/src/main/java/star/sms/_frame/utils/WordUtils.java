package star.sms._frame.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 导出word模板
 * @author
 */
public class WordUtils {
	public static File createWord(HttpServletResponse response,Map dataMap,String templateName,File outFile){
        try {
            //创建配置实例
            Configuration configuration = new Configuration();
    	    File directory = new File("");
			String templateFolder = directory.getAbsolutePath();
            //设置编码
            configuration.setDefaultEncoding("UTF-8");
            configuration.setEncoding(Locale.getDefault(), "utf-8");
            try {
	            configuration.setDirectoryForTemplateLoading(new File(templateFolder+"\\template\\"));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
 
            //获取模板
            Template template = configuration.getTemplate(templateName);
            //将模板和数据模型合并生成文件
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"UTF-8"));
            //生成文件
            template.process(dataMap, out);
            //关闭流
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outFile;
    }
}
