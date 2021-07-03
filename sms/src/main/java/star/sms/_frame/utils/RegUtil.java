package star.sms._frame.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description
 * @create 2019-03-21 16:09
 */
public class RegUtil {
    public static List getMatcher(String regex, String source) {
        List<String> result=new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                result.add(matcher.group(i+1));
            }
        }
        return result;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("").trim();
        }
        dest = dest.replaceAll(" ","");
        return dest;
    }
    public static String replaceSingleQuotes(String str) {
        String dest = "";
        String regexp = "\'";
        if (StringUtils.isNotEmpty(str)) {
        	dest = str.replaceAll(regexp, "\"");
        }
        return dest;
    }
    //验证是否符合数字类型
    public static  boolean isShuzi(String value){
        Pattern pattern = Pattern.compile("^[\\+\\-]?[\\d]+(\\.[\\d]+)?$");
        Matcher matcher = pattern.matcher(value);
        if ( matcher.matches()){
            return true;
        }
        return false;
    }

    /**
     * 主要作用是  给一个字符串  提取到里面的数字 然后让里面的数字加一  另返回
     * @param str
     * @param reg
     * @return
     */
    public static String replaceNumberByReg(String str,String reg){
        List<String> matcher = RegUtil.getMatcher(reg, str); //Contents(\d+)\.html  提取数字
        if (matcher.size()>0){
            Integer page = Integer.valueOf(matcher.get(0))+1;
            str = str.replaceAll("\\d+",page.toString());
            return str;
        }
        return null;
    }
    public static void main(String[] args){
       String s="全部短信(20)";
       List<String> matcher = RegUtil.getMatcher(".+\\((.+)\\)", s);
        System.out.println(matcher.toString());
    }

    /**
     * 判断是否符合正则要求
     * @param reg
     * @param str
     * @return
     */
    public static Boolean isTrueReg(String reg,String str){
        //编译
        Pattern pattern = Pattern.compile(reg);
        //匹配
        Matcher matcher = pattern.matcher(str);
        //得到匹配结果，布尔值
        return matcher.matches();
    }
}
