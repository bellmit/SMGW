package star.sms._frame.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author star
 */
public class SecurityUtil {

    private final static Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    // 默认24字节的密钥
    private final static byte[] defaultKeyBytes = {0x11, 0x22, 0x4F, 0x58,
            (byte) 0x88, 0x10, 0x40, 0x38, 0x28, 0x25, 0x79, 0x51, (byte) 0xCB,
            (byte) 0xDD, 0x55, 0x66, 0x77, 0x29, 0x74, (byte) 0x98, 0x30, 0x40,
            0x36, (byte) 0xE2};

    // 定义 加密算法,可用  DES,DESede,Blowfish
    private static final String Algorithm = "DESede";


    /**
     * @param src 被加密的数据缓冲区（源）
     * @return
     */
    public static byte[] encryptMode(byte[] src) {
        return encryptMode(defaultKeyBytes, src);
    }

    /**
     * @param keybyte 加密密钥，长度为24字节
     * @param src     被加密的数据缓冲区（源）
     * @return
     */
    public static byte[] encryptMode(byte[] keybyte, byte[] src) {
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            // 加密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /**
     * @param src 加密后的缓冲区
     * @return
     */
    public static byte[] decryptMode(byte[] src) {
        return decryptMode(defaultKeyBytes, src);
    }

    /**
     * @param keybyte 加密密钥，长度为24字节
     * @param src     加密后的缓冲区
     * @return
     */
    public static byte[] decryptMode(byte[] keybyte, byte[] src) {
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            // 解密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }


    /**
     * 字节流转换成十六进制字符串
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";

        for (Integer n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
            if (n < b.length - 1)
                hs = hs + ":";
        }
        return hs.toUpperCase();
    }

    /**
     * 十六进制字符串转换成字节流
     *
     * @param hex
     * @return
     */
    public static byte[] hex2byte(String hex) {
        String[] hexCode = hex.split(":");
        byte[] byteCode = new byte[hexCode.length];

        for (int i = 0; i < byteCode.length; i++) {
            byteCode[i] = (byte) Integer.parseInt(hexCode[i], 16);
        }
        return byteCode;

    }

    /**
     * 加密方法
     *
     * @param str 原始字符串
     * @return 加密后的字符串
     * @throws UnsupportedEncodingException 
     */
    public static String encrypt(String str) throws UnsupportedEncodingException {
        byte[] encoded = encryptMode(str.getBytes("utf-8"));
        return byte2hex(encoded);
    }

    /**
     * 解密方法
     *
     * @param str 加密后的字符串
     * @return 原始字符串
     * @throws UnsupportedEncodingException 
     */
    public static String decrypt(String str) throws UnsupportedEncodingException {
        byte[] encoded = hex2byte(str);
        encoded = decryptMode(encoded);
        return new String(encoded,"utf-8").trim();
    }

}