package star.sms._frame.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Description
 * @create 2019-05-25 2:00
 */
public class ReadPropertiesConfig {
    public static String read(String key,String path){
        Properties properties = new Properties();
        try {
            InputStream in = new FileInputStream(new File(path));
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return properties.getProperty(key);
    }
    public static String read(String key){
        Properties properties = new Properties();
        try {
            InputStream in =  ReadPropertiesConfig.class.getResourceAsStream("/application-authorization.properties");//加载 application.properties资源文件，如果该文件在包内则加包名
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return properties.getProperty(key);
    }
}
