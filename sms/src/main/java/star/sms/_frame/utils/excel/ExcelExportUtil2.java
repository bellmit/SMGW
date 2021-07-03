package star.sms._frame.utils.excel;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import star.sms._frame.utils.excel.annotation.ExcelAttribute;
import star.sms._frame.utils.excel.annotation.ExcelAttributeHandle;

/**
 * excel导出优化版   支持多sheet导出。支持实体类字段名称无规则
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelExportUtil2<T> {
    private int rowIndex;
    private int styleIndex;
    private String templatePath;
    private Class clazz;
    private Field fields[];
    private String[] letter = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z","AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP"};
    private XSSFWorkbook workbook;

    public ExcelExportUtil2(Class clazz) {
        this.clazz = clazz;
        fields = clazz.getDeclaredFields();
        workbook =  new XSSFWorkbook();
    }

    /**
     * 基于注解导出
     */
    public void export(List<T> objs, String sheetNames) throws Exception {
        createSheet(objs,sheetNames);
    }
    private void createSheet(List<T> objs,String sheetName) throws Exception{
        XSSFSheet sheet = workbook.createSheet(sheetName);
        Row title_row = sheet.createRow(0);
        int cellIndex=0;
        for (int k = 0; k < fields.length; k++) {
            ExcelAttribute ea = fields[k].getAnnotation(ExcelAttribute.class);
            if(ea!=null) {
                int sort = ea.sort();
                Cell cell = title_row.createCell(sort);
                String name = ea.name();
                cell.setCellValue(name);
                cellIndex=sort>cellIndex?sort:cellIndex;
            }
        }
        String addStr = "A0:"+letter[cellIndex]+"0";
        //添加过滤
        CellRangeAddress addr = CellRangeAddress.valueOf(addStr);
        sheet.setAutoFilter(addr);
        //遍历塞数据
        for (int i = 0; i <objs.size() ; i++) {
            Row row = sheet.createRow(i+1);
            for (int j = 0; j <fields.length ; j++ ) {
                if(fields[j].isAnnotationPresent(ExcelAttribute.class)){
                    fields[j].setAccessible(true);
                    ExcelAttribute ea = fields[j].getAnnotation(ExcelAttribute.class);
                    Cell cell = row.createCell(ea.sort());
                    if(ea!=null&&fields[j].get(objs.get(i))!=null) {
                        if (ea.format()!=null&&!ea.format().equals("")){
                            cell.setCellValue(ExcelAttributeHandle.handle(ea.format(),fields[j].get(objs.get(i)).toString()));
                        }else{
                            cell.setCellValue(fields[j].get(objs.get(i)).toString());
                        }
                    }
                }
            }
        }
    }

    public void down(HttpServletResponse response, String fileName) throws Exception{
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("content-disposition", "attachment;filename= " + new String(fileName.getBytes("ISO8859-1")));
        response.setHeader("filename", fileName);
        workbook.write(response.getOutputStream());
    }

    /**
     * 导出中文乱码解决
     * @param s
     * @return
     */
    public static String toUtf8String(String s){
        StringBuffer sb = new StringBuffer();
        for (int i=0;i<s.length();i++){
            char c = s.charAt(i);
            if (c >= 0 && c <= 255){sb.append(c);}
            else{
                byte[] b;
                try { b = Character.toString(c).getBytes("utf-8");}
                catch (Exception ex) {
                    System.out.println(ex);
                    b = new byte[0];
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0) k += 256;
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }
    public static String getFileSort(MultipartFile file) throws Exception{
        String filename = file.getOriginalFilename();
        if (filename.toLowerCase().endsWith("xlsx")||filename.toLowerCase().endsWith("xls")||filename.toLowerCase().endsWith("xltx")||filename.toLowerCase().endsWith("et")){
            return "excel";
        }else if (filename.toLowerCase().endsWith("csv")){
            return "csv";
        }else if (filename.toLowerCase().endsWith("txt")){
            return "txt";
        }else if (filename.toLowerCase().endsWith("pdf")){
            return "pdf";
        }else if (filename.toLowerCase().endsWith("doc")){
            return "doc";
        }else if (filename.toLowerCase().endsWith("dat")){
            return "dat";
        }else if (filename.toLowerCase().endsWith("rar")){
            return "rar";
        }else if (filename.toLowerCase().endsWith("zip")){
            return "zip";
        }else if (filename.toLowerCase().endsWith("7z")){
            return "7z";
        }else if (filename.toLowerCase().endsWith("out")){
            return "out";
        }else{
            throw new Exception("上传文件类型错误");
        }
    }
}
