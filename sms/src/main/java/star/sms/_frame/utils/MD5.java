package star.sms._frame.utils;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密类
 */
public class MD5 { 
	/**
	 * MD5加密
	 * @param
	 *
	 */
	public static final String HEX_DIG="PLLRDXKVOU2UX2HX";
	public static final String HEX_DIG2="ZOOH42QIJLAW25DD";
	public static final String HEX_DIX="72122ce96bfec66e2396d2e25225d70a";
	public static final String HEX_DIX2="1b3231655cebb7a1f783eddf27d254ca";
	public static final String HEX_MIX="a43dd1941a22b35eb105b5fcb43b425b";
	public static final String HEX_MIX2="2bffb3bbd66a9b469723cbfe03a521d0";
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * byte数组转为16进制字符串
	 * @param b byte数组
	 * @return 16进制数组
	 */
	public static String toHexString(byte[] b) {  
        StringBuilder sb = new StringBuilder(b.length * 2);  
        for (int i = 0; i < b.length; i++) {  
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);  
            sb.append(HEX_DIGITS[b[i] & 0x0f]);  
        }  
        return sb.toString();  
	}
	//MD5加密
    public String encrypt(String s) {
       try {
           // Create MD5 Hash
           MessageDigest digest = MessageDigest.getInstance("MD5");
           digest.update(s.getBytes());
           byte messageDigest[] = digest.digest();
                       
           return toHexString(messageDigest);
       } catch (NoSuchAlgorithmException e) {
           e.printStackTrace();
       }      
       return "";
   }
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 解析
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * 将指定byte数组以16进制的形式打印到控制台
     * @param b
     */
    public static void printHexString(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print(hex.toUpperCase());
        }

    }

    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789abcdef".indexOf(c);
    }

    /**
     * 加密
     * @param str
     * @return
     */
    public static String encode(String str) {
        String strDigest = "";
        try {
            // 此 MessageDigest 类为应用程序提供信息摘要算法的功能，必须用try,catch捕获
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            byte[] data;
            data = md5.digest(str.getBytes("utf-8"));// 转换为MD5码
            strDigest = bytesToHexString(data);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return strDigest;
    }
    /**
     * java获取文件的md5值  
     * @param fis 输入流
     * @return
     */
    public synchronized static String md5HashCode(InputStream fis) {  
        try {  
        	//拿到一个MD5转换器,如果想使用SHA-1或SHA-256，则传入SHA-1,SHA-256  
            MessageDigest md = MessageDigest.getInstance("MD5"); 
            
            //分多次将一个文件读入，对于大型文件而言，比较推荐这种方式，占用内存比较少。
            byte[] buffer = new byte[1024*1024];  
            int length = -1;  
            while ((length = fis.read(buffer, 0, 1024*1024)) != -1) {  
                md.update(buffer, 0, length);  
            }  
            fis.close();
            //转换并返回包含16个元素字节数组,返回数值范围为-128到127
  			byte[] md5Bytes  = md.digest();
            BigInteger bigInt = new BigInteger(1, md5Bytes);//1代表绝对值 
            return bigInt.toString(16);//转换为16进制
        } catch (Exception e) {  
            e.printStackTrace();  
            return "";  
        }  
    } 

}
