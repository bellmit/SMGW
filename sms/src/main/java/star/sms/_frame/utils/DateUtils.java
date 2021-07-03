package star.sms._frame.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


public class DateUtils {

    public static Date getDate(String st){
        Date date = null;
        if (st==null)return null;
        //如果是以.结尾的话，就去掉
        if(st.endsWith(".")){
            st = st.substring(0,st.length()-1);
        }
        String pattern= "yyyy/MM/dd HH:mm:ss";
        String pattern2= "yyyy-MM-dd HH:mm:ss";
        try {
            if (st.trim().length()<11){
                pattern= "yyyy/MM/dd";
                pattern2= "yyyy-MM-dd";
            }
            if (st.length()>19) {
            	st = st.substring(0, 19);
            }
            if (Pattern.matches("\\d{4}年\\d{1,2}月\\d{1,2}日 \\d{2}:\\d{2}:\\d{2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{4}/\\d{2}/\\d{2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat(pattern);
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{2}:\\d{2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat(pattern);
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}\\.\\d{2}\\.\\d{2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat(pattern2);
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{4}\\d{2}\\d{2} \\d{4}\\.\\d{2}\\.\\d{2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd HH.mm.ss");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{4}\\d{2}\\d{2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{4}\\d{2}\\d{2} \\d{2}:\\d{2}:\\d{2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{4}\\d{2}\\d{2} \\d{2}\\d{2}\\d{2}", st)||Pattern.matches("\\d{4}\\d{2}\\d{2}\\d{2}\\d{2}\\d{2}", st)){
                SimpleDateFormat sd = st.contains(" ")?new SimpleDateFormat("yyyyMMdd HHmmss"):new SimpleDateFormat("yyyyMMddHHmmss");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}\\d{2}\\d{2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HHmmss");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{4}/\\d{1,2}/\\d{1,2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{1,2}/\\d{1,2}/\\d{2} \\d{1,2}:\\d{1,2}:\\d{1,2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{4}-\\d{1,2}-\\d{2} \\d{1,2}:\\d{1,2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{6}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("HHmmss");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{14}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{2}\\.\\d{2}\\.\\d{2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("HH.mm.ss");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{2}:\\d{2}:\\d{2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{2}/\\d{2}/\\d{2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yy");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{1}/\\d{1}/\\d{2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("M/d/yy");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{2}/\\d{1}/\\d{2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("MM/d/yy");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{1}/\\d{2}/\\d{2}", st)){
                SimpleDateFormat sd = new SimpleDateFormat("M/dd/yy");
                date = sd.parse(st);
            }else if (Pattern.matches("\\d{5}", st)){
                Calendar calendar = new GregorianCalendar(1900,0,-1);
                Date d = calendar.getTime();
                date = org.apache.commons.lang3.time.DateUtils.addDays(d,Integer.valueOf(st));
            }else if (Pattern.matches("^0\\..*", st)){
                Double aDouble = Double.valueOf(st);
                Long sencod =new Double(aDouble*16*3600).longValue();
                date =  secondToDate(sencod);
            }else if (Pattern.matches("\\d{5}\\..*", st)){
                Calendar calendar = new GregorianCalendar(1900,0,-1);
                Date d = calendar.getTime();
                date = org.apache.commons.lang3.time.DateUtils.addDays(d,Integer.valueOf(st.split("\\.")[0]));

                Double aDouble = Double.valueOf("0."+st.split("\\.")[1]);
                Long sencod =new Double(aDouble*24*3600*1000).longValue();
                date = new Date(date.getTime()+sencod);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return date;
        }

    }

    //格式化时间，得到字符串
    public static String getDateString (Date date){
    	if(date!=null) {
    		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		String format = sd.format(date);
    		return format;
    	}else {
    		return null;
    	}
    }
    //格式化时间，得到字符串
    public static String getDateString (Date date,String pattern){
        try {
            if(date!=null) {
                SimpleDateFormat sd = new SimpleDateFormat(pattern);
                String format = sd.format(date);
                return format;
            }else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @date 2018/11/22 16:35
     * @return java.lang.String
     * @Description 获取格式化后的日期字符串 目前只支持20060704093715 20060704 093715
     */
    public static String getFormatDateString(String str){
        if (StringUtils.isBlank(str)){
            return "";
        }
        Pattern pattern = Pattern.compile("\\d+");
        if (pattern.matcher(str).matches()){
            if (str.length()==8){ //说明是20060704 类型  只有年月日
                String year = str.substring(0, 4);
                String month = str.substring(4, 6);
                String day = str.substring(6, 8);
                return year+"-"+month+"-"+day;
            }else if (str.length()==6){ //说明是093715   类型  只有时分秒
                String hour = str.substring(0, 2);
                String minute = str.substring(2, 4);
                String second = str.substring(4, 6);
                return hour+"-"+minute+"-"+second;
            }else if (str.length()==14){  //说明是20060704093715   类型  年月日时分秒
                String year = str.substring(0, 4);
                String month = str.substring(4, 6);
                String day = str.substring(6, 8);
                String hour = str.substring(8, 10);
                String minute = str.substring(10, 12);
                String second = str.substring(12,14);
                return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
            }
        }
        return "";
    }

    public static String formatTimeString(String s,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat(format);
            Date date = sdf.parse(s);
            return sdf2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 格式化字符串20060704093715这类型的  不够补0
     * @param s
     * @return
     */
    public static String getTimestampString(String s){
        String pattern = "\\d+";
        boolean isMatch = Pattern.matches(pattern, s);
        if (!isMatch) return null;
        if (s==null) return null;
        if (s.length()<=14){
            int i=14- s.length();
            String str="";
            for (int j=0;j<i;j++){
                str+="0";
            }
            return s+str;
        }
        return null;
    }

    /**
     * @Date 2019/1/16 16:34
     * @Param [year]
     * @return java.util.List<java.lang.String>
     * @Description 输入年份 得到月份的集合
     **/
    public static List<String> getYearMonth(List<String> year){
        ArrayList<String> yearList = new ArrayList<>();
        for (int k = 0; k < year.size();k++) {
            for (int i = 1; i < 13; i++) {
                String month="";
                if (i<10){
                    month=year.get(k)+"-0"+i;
                }else{
                    month=year.get(k)+"-"+i;
                }
                yearList.add(month);
            }
        }
        return yearList;
    }
    /**
     * @Date 2019/6/12 11:37
     * @Param [s]
     * @return 字符串  yyyy-MM-dd   转Timestamp类型
     * @Description
     **/
    public static Timestamp stringToTimestamp(String s){
        try {
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = getDate(s);
            if (date!=null){
                return Timestamp.valueOf(format2.format(date));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("<<<<<<<<<<<<<<<<<字符串  yyyy-MM-dd   转Timestamp类型 失败,转换值为："+s);
        }
        return null;
    }
    /**
     * 字符串转换成timestamp
     *
     * @param dateTime
     *            要转换的时间字符串
     * @return 转换失败返回 null
     */
    public static Timestamp stringToTimestamp(String dateTime, String format) {
        try {
            SimpleDateFormat df1 = new SimpleDateFormat(format);
            Date date11 = df1.parse(dateTime);
            Timestamp ts = new Timestamp(date11.getTime());
            return ts;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 得到二个日期间的间隔天数
     */
    public static String getTwoDay(String sj1, String sj2) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        long day = 0;
        try {
            java.util.Date date = formatter.parse(sj1);
            java.util.Date mydate = formatter.parse(sj2);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return "";
        }
        return day + "";
    }

    /**
     * 判断时间是否有时分秒
     */
    public static boolean isHavingHour_minute_second(Date tradeDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = formatter.format(tradeDate);
            if (format.substring(11).equals("00:00:00")||format.substring(0,10).equals("1970-01-01")){
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 给两个时间，都是不完整的，比如  1970-01-01 22:02:03 （只有时分秒） 2010-01-01 00:00:00  （只有年月日） 要得到一个 2010-01-01 22:02:03  详细的时间
     */
    public static String bothCompareGetBestDetail(Date date1,Date date2) {
        String dateStr="";
        String timeStr="";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format1 = formatter.format(date1);
            String format2 = formatter.format(date2);
            //先判断 那个有年月日
            if (!format1.substring(0,10).equals("1970-01-01")){
                dateStr = format1.substring(0,10);
            }else if (!format2.substring(0,10).equals("1970-01-01")){
                dateStr = format2.substring(0,10);
            }
            //再判断 那个有时分秒
            if (!format1.substring(11).equals("00:00:00")){
                timeStr = format1.substring(11);
            }else if (!format2.substring(11).equals("00:00:00")){
                timeStr = format2.substring(11);
            }
            return (dateStr+" "+timeStr).trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 秒转时间
     * @param second
     * @return
     */
    private static Date secondToDate(long second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(second * 1000);//转换为毫秒
        Date date = calendar.getTime();
        return date;
    }

    public static void main(String[] arg) throws Exception{
        String s = "00:00:19";
        Date date = getNewDate(getDate(s),8);
        long x = date.getTime()/1000;
        System.out.println(x);
        System.out.println(getDateString(date));
    }
    /**
     * 设置给指定的时间 加上小时
     */
    public static Date getNewDate(Date cur,int hour) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);   //设置时间
        c.add(Calendar.HOUR, hour); //日期分钟加1,Calendar.DATE(天),Calendar.HOUR(小时)
        Date date = c.getTime(); //结果
        return date;
    }

    /**
     * 设置给指定的时间 加上小时
     */
    public static String getHour(Date date) {
        Long x = date.getTime()/1000;
        return x.toString();
    }

}
