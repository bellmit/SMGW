package star.sms._frame.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @Description
 * @create 2019-01-24 17:18
 * 得到磁盘的序列号（默认取第1个）
 */
public class DiskUtils {
    public static List<String> getDiskId() {
        List<String> serial = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec(
                    new String[] { "wmic", "diskdrive", "get", "serialnumber" });
            process.getOutputStream().close();
            Scanner sc = new Scanner(process.getInputStream());
            sc.next();
            while (sc.hasNext()){
                serial.add(sc.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serial;
    }
    public static void main(String[] args){
        for (String s:getDiskId()){
            System.out.println(s);
        }
    }
}
