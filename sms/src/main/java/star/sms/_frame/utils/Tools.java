package star.sms._frame.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cglib.beans.BeanCopier;

/**
 * @author star
 */
public class Tools {

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

	public static String formatFloat(float f) {
		DecimalFormat df = new DecimalFormat("00.00");
		String sdf = df.format(f, new StringBuffer(), new FieldPosition(0)).toString();
		return sdf;
	}

	/**
	 * 获取两个时间差
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String getTimeSpan3(String startTime, String endTime) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date now = df.parse(startTime);
			java.util.Date date = df.parse(endTime);
			long l = date.getTime() - now.getTime();
			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			if (hour < 0) {
				hour = 0;
			}
			if (min < 0) {
				min = 0;
			}
			if (s < 0) {
				s = 0;
			}
			if (day < 0) {
				day = 0;
			}

			min = hour * 60 + min;
			String m = String.valueOf(min);
			if (min < 10) {
				m = "0" + min;
			}
			String ss = String.valueOf(s);
			if (s < 0) {
				ss = "0" + s;
			}
			return m + "分钟" + ss + "秒";
		} catch (Exception ex) {
			return "";
		}
	}

	/**
	 * 获取两个时间差
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String getTimeSpan2(String startTime, String endTime) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date now = df.parse(startTime);
			java.util.Date date = df.parse(endTime);
			long l = date.getTime() - now.getTime();
			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			if (hour < 0) {
				hour = 0;
			}
			if (min < 0) {
				min = 0;
			}
			if (s < 0) {
				s = 0;
			}
			if (day < 0) {
				day = 0;
			}
			String h = String.valueOf(hour);
			if (hour < 10) {
				h = "0" + hour;
			}
			String m = String.valueOf(min);
			if (min < 10) {
				m = "0" + min;
			}
			return h + ":" + m;
		} catch (Exception ex) {
			return "";
		}
	}

	/**
	 * 获取两个时间差
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static long getDayTimeSpan(String startTime, String endTime) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date now = df.parse(startTime);
			java.util.Date date = df.parse(endTime);
			long l = date.getTime() - now.getTime();
			long day = l / (24 * 60 * 60 * 1000);
			if (day < 0)
				day = 0;
			return day;

		} catch (Exception ex) {
			return 0;
		}
	}

	/**
	 * 获取两个时间差
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static long getIntTimeSpan(String startTime, String endTime) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date now = df.parse(startTime);
			java.util.Date date = df.parse(endTime);
			long l = date.getTime() - now.getTime();
			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			if (hour < 0) {
				hour = 0;
			}
			if (min < 0) {
				min = 0;
			}
			if (s < 0) {
				s = 0;
			}
			if (day < 0) {
				day = 0;
			}
			return day * 24 * 3600 + hour * 3600 + min * 60 + s;

		} catch (Exception ex) {
			return 0;
		}
	}

	/**
	 * 获取两个时间差
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String getTimeSpan(String startTime, String endTime) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date now = df.parse(startTime);
			java.util.Date date = df.parse(endTime);
			long l = date.getTime() - now.getTime();
			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			if (hour < 0) {
				hour = 0;
			}
			if (min < 0) {
				min = 0;
			}
			if (s < 0) {
				s = 0;
			}
			if (day < 0) {
				day = 0;
			}
			if (day > 0) {
				return day * 24 + hour + "小时" + min + "分" + s + "秒";
			} else {
				return hour + "小时" + min + "分" + s + "秒";
			}

		} catch (Exception ex) {
			return "";
		}
	}

	/**
	 * 转换对象为INT
	 *
	 * @param s
	 *            对象
	 * @return 如果为空则返回-1
	 */
	public static int null2Int(Object s) {
		return null2Int(s, -1);
	}

	/**
	 * 转换日期字符串为'yyyy-MM-dd'
	 *
	 * @param date
	 * @return
	 */
	public static String dateToStr(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	/**
	 * 转换日期字符串为'yyyy-MM-dd'
	 * 
	 * @param DateStr
	 * @return
	 */
	public static String strToDate2(String DateStr) {
		SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// formatYMD表示的是yyyy-MM-dd格式
		SimpleDateFormat formatD = new SimpleDateFormat("MM月dd日  HH:mm:ss");// "E"表示"day in week"
		Date d = null;
		String weekDay = "";
		try {
			d = formatYMD.parse(DateStr);// 将String 转换为符合格式的日期
			weekDay = formatD.format(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return weekDay;
	}

	public static String strToDate3(String DateStr) {
		SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// formatYMD表示的是yyyy-MM-dd格式
		SimpleDateFormat formatD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// "E"表示"day in week"
		Date d = null;
		String weekDay = "";
		try {
			d = formatYMD.parse(DateStr);// 将String 转换为符合格式的日期
			weekDay = formatD.format(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return weekDay;
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	/**
	 * 
	 * 获取现在时间
	 * 
	 * 
	 * 
	 * @return 返回短时间字符串格式yyyy-MM-dd
	 * 
	 */

	public static String getStringDateShort() {

		Date currentTime = new Date();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		String dateString = formatter.format(currentTime);

		return dateString;

	}

	/**
	 * 
	 * 获取一个月的最后一天
	 * 
	 * 
	 * 
	 * @param dat
	 * 
	 * @return
	 * 
	 */

	public static String getEndDateOfMonth(String dat) {

		String str = dat.substring(0, 8);

		String month = dat.substring(5, 7);

		int mon = Integer.parseInt(month);

		if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) {

			str += "31";

		} else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {

			str += "30";

		} else {

			if (isLeapYear(dat)) {

				str += "29";

			} else {

				str += "28";

			}

		}

		return str;

	}

	/**
	 * 
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd
	 * 
	 * 
	 * 
	 * @param strDate
	 * 
	 * @return
	 * 
	 */

	public static Date strToDate(String strDate) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		ParsePosition pos = new ParsePosition(0);

		Date strtodate = formatter.parse(strDate, pos);

		return strtodate;

	}

	/**
	 * 
	 * 将时间格式字符串转换为特定格式的字符串
	 * 
	 * @param strDate
	 * 
	 * @return
	 * 
	 */

	public static String formatDateTime(String strDate, String format) {

		try {
			Date date = strToDate(strDate);
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);

		} catch (Exception ex) {
			return strDate;
		}

	}

	/**
	 * 
	 * 判断是否润年
	 * 
	 * 
	 * 
	 * @param ddate
	 * 
	 * @return
	 * 
	 */

	public static boolean isLeapYear(String ddate) {

		/**
		 * 
		 * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
		 * 
		 * 3.能被4整除同时能被100整除则不是闰年
		 * 
		 */

		Date d = strToDate(ddate);

		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();

		gc.setTime(d);

		int year = gc.get(Calendar.YEAR);

		if ((year % 400) == 0)

			return true;

		else if ((year % 4) == 0) {

			if ((year % 100) == 0)

				return false;

			else

				return true;

		} else

			return false;

	}

	/**
	 * 转换对象为INT
	 * 
	 * @param object
	 *            对象
	 * @param def
	 *            失败默认值
	 * @return INT值
	 */
	public static int null2Int(Object object, int def) {

		if (object != null) {
			try {
				return Integer.parseInt(object.toString());
			} catch (Exception e) {
			}
		}
		return def;
	}

	public static double div(double v1, double v2, int scale) {

		BigDecimal b1 = new BigDecimal(Double.toString(v1));

		BigDecimal b2 = new BigDecimal(Double.toString(v2));

		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();

	}

	/**
	 * 上月第一天
	 * 
	 * @return
	 */
	public static String getPreviousMonthFirst() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, -1);// 减一个月，变为下月的1号
		str = sdf.format(lastDate.getTime());
		return str;
	}

	/**
	 * 当月第一天
	 * 
	 * @return
	 */
	public static String getCurrentMonthFirst() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, 0);// 减一个月，变为下月的1号
		str = sdf.format(lastDate.getTime());
		return str;
	}

	public static String getCurrentMonthFirst1() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, 0);// 减一个月，变为下月的1号
		str = sdf.format(lastDate.getTime());
		return str;
	}

	/**
	 * 比较日期是否一样
	 */
	public static boolean dateCompare(Date dat1, Date dat2) {
		boolean dateComPareFlag = true;
		if (dat2.compareTo(dat1) != 1) {
			dateComPareFlag = false; //
		}
		return dateComPareFlag;
	}

	public static String formatLongToTimeStr(Long l) {
		// String str = "";
		int hour = 0;
		int minute = 0;
		int second = 0;

		second = l.intValue() / 1000;

		if (second > 60) {
			minute = second / 60;
			second = second % 60;

		} else {
			if (second > 0) {
				minute = minute + 1;
			}
		}
		if (minute > 60) {
			hour = minute / 60;
			minute = minute % 60;

		}
		return (hour + "小时" + minute + "分钟");

	}

	public static String formatLongToTimeStr1(Long l) {
		// String str = "";
		// int hour = 0;
		int minute = 0;
		int second = 0;

		second = l.intValue() / 1000;

		if (second > 60) {
			minute = second / 60;
			second = second % 60;

		} else {
			if (second > 0) {
				minute = minute + 1;
			}
		}
		// if (minute > 60) {
		// hour = minute / 60;
		// minute = minute % 60;
		//
		// }
		return (minute + "");

	}

	public static String percent(double p1, double p2) {
		String str;
		double p3 = p1 / p2;
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(2);
		str = nf.format(p3);
		return str;
	}

	public static String timespan2Str(Timestamp time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 定义格式，不显示毫秒

		String str = df.format(time);
		return str;
	}

	// 复制方法
	public static void copy(String src, String des) throws Exception {
		// 初始化文件复制
		File file1 = new File(src);
		if (!file1.exists()) {
			return;
		}
		// 把文件里面内容放进数组
		File[] fs = file1.listFiles();
		// 初始化文件粘贴
		File file2 = new File(des);
		// 判断是否有这个文件有不管没有创建
		if (!file2.exists()) {
			file2.mkdirs();
		}
		// 遍历文件及文件夹
		for (File f : fs) {
			if (f.isFile()) {
				// 文件
				fileCopy(f.getPath(), des + "/" + f.getName()); // 调用文件拷贝的方法
			} else if (f.isDirectory()) {
				// 文件夹
				copy(f.getPath(), des + "/" + f.getName());// 继续调用复制方法 递归的地方,自己调用自己的方法,就可以复制文件夹的文件夹了
			}
		}

	}

	/**
	 * 文件复制的具体方法
	 */
	private static void fileCopy(String src, String des) throws Exception {
		// io流固定格式
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
		int i = -1;// 记录获取长度
		byte[] bt = new byte[2014];// 缓冲区
		while ((i = bis.read(bt)) != -1) {
			bos.write(bt, 0, i);
		}
		bis.close();
		bos.close();
		// 关闭流
	}

	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * bean copy
	 * @param object
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T beanCopyForLodBean(T object) throws InstantiationException, IllegalAccessException {
		/* cpoy object bean */
		BeanCopier copy = BeanCopier.create(object.getClass(), object.getClass(), false);
		T copyBean = (T) object.getClass().newInstance();
		copy.copy(object, copyBean, null);
		return copyBean;
	}
	
    /**
     * 计算哪些是需要新增的，哪些是需要删除的。执行后，oldValueSet中是需要删除的，newValueSet中是需要新增的。
     * @param oldValueSet 旧数据集合
     * @param newValueSet 新数据集合
     */
    public static <T> void diffNewOrDel(Set<T> oldValueSet, Set<T> newValueSet) {
		
		Set<T> tempValues = new HashSet<T>(oldValueSet);

		// 交集
		tempValues.retainAll(newValueSet);
//		System.out.println("交集是 " + tempValues);
//		System.out.println("oldValueSet不变是 " + oldValueSet);
//		System.out.println("newValueSet不变是 " + newValueSet);

		// 差集
		oldValueSet.removeAll(tempValues);
//		System.out.println("需要删除的：" + oldValueSet);
		newValueSet.removeAll(tempValues);
//		System.out.println("需要新增的：" + newValueSet);
    }
}
