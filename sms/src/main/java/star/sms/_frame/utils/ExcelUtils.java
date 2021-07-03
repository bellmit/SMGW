package star.sms._frame.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

public class ExcelUtils {

    //读取excel
    public static Workbook readExcel(MultipartFile file) {
        Workbook workbook = null;
        if (file == null) {
            return null;
        }
        String originalFilename = file.getOriginalFilename();
        String extString = originalFilename.substring(originalFilename.lastIndexOf("."));
        InputStream is = null;
        try {
            is = file.getInputStream();
            if (".xls".equals(extString)) {
                return workbook = new HSSFWorkbook(is);
            } else if (".xlsx".equals(extString)||".xltx".equals(extString)) {
                long l = System.currentTimeMillis();
                System.out.println("begin...");
                workbook = new XSSFWorkbook(is);
//                workbook = StreamingReader.builder()
//                        .rowCacheSize(100000)  //缓存到内存中的行数，默认是10
//                        .bufferSize(1024*1024*4)  //读取资源时，缓存到内存的字节大小，默认是1024
//                        .open(is);  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
                System.out.println("花费："+(System.currentTimeMillis()-l)/1000+"秒");
                return workbook;
            } else {
                return workbook = null;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workbook;
    }

    /**
     * 判断合并了行
     *
     * @param sheet
     * @param row 行下标
     * @param column 列下标
     * @return
     */
    public static boolean isMergedRow(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row == firstRow && row == lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet
     *            工作表
     * @param row
     *            行下标
     * @param column
     *            列下标
     * @return
     */
    public static boolean isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取单元格的值
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {

        if (cell == null)
            return "";

        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {

            return cell.getStringCellValue();

        } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {

            return String.valueOf(cell.getBooleanCellValue());

        } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {

            return cell.getCellFormula();

        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            String dateString = "";
            if (HSSFDateUtil.isCellDateFormatted(cell)) { //Excel Date类型处理
                Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                dateString = sd.format(date);
            }else{
                DecimalFormat df = new DecimalFormat("0.0000");
                dateString = df.format(cell.getNumericCellValue());
            }
            return dateString;
        }
        return "";
    }

    /**
     * 获取单元格的值
     *
     * @param cell
     * @return
     */
    public static String getTaxCellValue(Cell cell) {

        if (cell == null)
            return "";

        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {

            return cell.getStringCellValue();

        } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {

            return String.valueOf(cell.getBooleanCellValue());

        } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {

            return cell.getCellFormula();

        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            String dateString = "";
            if (HSSFDateUtil.isCellDateFormatted(cell)) { //Excel Date类型处理
                Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dateString = sd.format(date);
            }else{
                DecimalFormat df = new DecimalFormat("0.0000");
                dateString = df.format(cell.getNumericCellValue());
            }
            return dateString;
        }
        return "";
    }

    /**
     * 获取单元格的值,用于话单
     *
     * @param cell
     * @return
     */
    public static String getCellPhoneValue(Cell cell) {

        if (cell == null)
            return "";

        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {

            return cell.getStringCellValue();

        } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {

            return String.valueOf(cell.getBooleanCellValue());

        } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {

            return cell.getCellFormula();

        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            short format = cell.getCellStyle().getDataFormat();
            SimpleDateFormat sdf = null;
            if (format == 14 || format == 31 || format == 57 || format == 58
                    || (176<=format && format<=178) || (182<=format && format<=196)
                    || (210<=format && format<=213) || (208==format ) ) { // 日期
                sdf = new SimpleDateFormat("yyyy-MM-dd");
            } else if (format==21 || format == 20 || format == 32 || format==183 || (200<=format && format<=209) ) { // 时间
                sdf = new SimpleDateFormat("HH:mm:ss");
            } else { // 不是日期格式
                DecimalFormat df = new DecimalFormat("0");
                return df.format(cell.getNumericCellValue());
            }
            double value = cell.getNumericCellValue();
            Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
            if(date==null || "".equals(date)){
                return "";
            }
            String result="";
            try {
                result = sdf.format(date);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
            return result;
        }
        return "";
    }

    /**
     * 判断是不是空白行  false表示是空白行，true表示不是空白行
     * @author 刘中华
     * @date 2018/11/20 21:07
     * @param [row]
     * @return boolean
     * @Description
     */
    public static boolean isBlankLine(Row row){

        boolean flag=true;
        int i=0;
        if (row==null||row.getLastCellNum()==-1){
            flag=false;
            return flag;
        }
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK){
                i++;
                if (i==row.getLastCellNum()){
                    flag=false;
                    return flag;
                }

            }
        }
        return flag;
    }

   
    /**
     * 读合并单元格值
     * @author 刘中华
     * @date 2018/11/20 21:07
     * @param [sheet, cell]
     * @return java.lang.String
     * @Description
     */
    public static String getMergedCellValue(Sheet sheet, Cell cell){

        int firstC;
        int lastC;
        int firstR;
        int lastR;
        String value = "";
        List<CellRangeAddress> listCombineCell = sheet.getMergedRegions();
        for (CellRangeAddress ca : listCombineCell) {
            // 获得合并单元格的起始行, 结束行, 起始列, 结束列
            firstC = ca.getFirstColumn();
            lastC = ca.getLastColumn();
            firstR = ca.getFirstRow();
            lastR = ca.getLastRow();
            if (cell.getRowIndex() >= firstR && cell.getRowIndex() <= lastR) {
                if (cell.getColumnIndex() >= firstC && cell.getColumnIndex() <= lastC) {
                    // 获取合并单元格左上角单元格值
                    Row fRow = sheet.getRow(firstR);
                    Cell fCell = fRow.getCell(firstC);
                    value = getCellValue(fCell);
                }
            }
        }
        return value;
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
        }else{
            throw new Exception("上传文件类型错误");
        }
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
}
