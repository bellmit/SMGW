package star.sms._frame.utils.excel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import star.sms._frame.utils.excel.annotation.ExcelAttribute;
import star.sms._frame.utils.excel.annotation.ExcelAttributeHandle;

public class ExcelExportUtil<T> {

    private int rowIndex;
    private int styleIndex;
    private String templatePath;
    private Class clazz;
    private  Field fields[];
    private String[] letter = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z","AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP"};

    public ExcelExportUtil(Class clazz) {
        this.clazz = clazz;
        fields = clazz.getDeclaredFields();
    }

    /**
     * 基于注解导出
     */
    public void export(HttpServletResponse response, List<T> objs,String fileName) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        Row title_row = sheet.createRow(0);
        int cellIndex=0;
        for (int k = 0; k < fields.length; k++) {
            Cell cell = title_row.createCell(k);
            ExcelAttribute ea = fields[k].getAnnotation(ExcelAttribute.class);
            if(ea!=null && k == ea.sort()) {
                String name = ea.name();
                cell.setCellValue(name);
                cellIndex=k;
            }
        }
        String addStr = "A0:"+letter[cellIndex]+"0";
        CellRangeAddress addr = CellRangeAddress.valueOf(addStr);
        sheet.setAutoFilter(addr);
        for (int i = 0; i <objs.size() ; i++) {
            Row row = sheet.createRow(i+1);
            for (int j = 0; j <fields.length ; j++ ) {
                Cell cell = row.createCell(j);
                if(fields[j].isAnnotationPresent(ExcelAttribute.class)){
                    fields[j].setAccessible(true);
                    ExcelAttribute ea = fields[j].getAnnotation(ExcelAttribute.class);
                    if(ea!=null && j == ea.sort()&&fields[j].get(objs.get(i))!=null) {
                        if (ea.format()!=null&&!ea.format().equals("")){
                            cell.setCellValue(ExcelAttributeHandle.handle(ea.format(),fields[j].get(objs.get(i)).toString()));
                        }else if(fields[j].getName().contains("hour")){
                            sheet.setColumnWidth(j, fields[j].get(objs.get(i)).toString().length()*1000);
                            cell.setCellValue(fields[j].get(objs.get(i)) == (Object) 0?"":fields[j].get(objs.get(i)).toString());
                        }else{
                            cell.setCellValue(fields[j].get(objs.get(i)).toString());
                        }
                    }
                }
            }
        }
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("content-disposition", "attachment;filename =" + new String(fileName.getBytes("ISO8859-1")));
        response.setHeader("filename", fileName);
        workbook.write(response.getOutputStream());
    }
    public Object getValue(Object dto,String name){
        try{
            String methodName = "get" + name.substring(0,1).toUpperCase()+name.substring(1);
            Method method =  dto.getClass().getDeclaredMethod(methodName);
            return method.invoke(dto);
        }catch(Exception ex){
            return null;
        }
    }
    /**
     * 基于注解的多sheet导出
     */
    public void sheetExport(XSSFSheet sheet , List<T> objs) throws Exception {
        Row title_row = sheet.createRow(0);
        int cellIndex=0;
        for (int k = 0; k < fields.length; k++) {
            Cell cell = title_row.createCell(k);
            ExcelAttribute ea = fields[k].getAnnotation(ExcelAttribute.class);
            if(ea!=null && k == ea.sort()) {
                String name = ea.name();
                cell.setCellValue(name);
                cellIndex=k;
            }
        }
        String addStr = "A0:"+letter[cellIndex]+"0";
        CellRangeAddress addr = CellRangeAddress.valueOf(addStr);
        sheet.setAutoFilter(addr);
        for (int i = 0; i <objs.size() ; i++) {
            Row row = sheet.createRow(i+1);
            for (int j = 0; j <fields.length ; j++ ) {
                Cell cell = row.createCell(j);
                if(fields[j].isAnnotationPresent(ExcelAttribute.class)){
                    fields[j].setAccessible(true);
                    ExcelAttribute ea = fields[j].getAnnotation(ExcelAttribute.class);
                    if(ea!=null && j == ea.sort()&&fields[j].get(objs.get(i))!=null) {
                        if (ea.format()!=null&&!ea.format().equals("")){
                            cell.setCellValue(ExcelAttributeHandle.handle(ea.format(),fields[j].get(objs.get(i)).toString()));
                        }else if(fields[j].getName().contains("hour")){
                            sheet.setColumnWidth(j, fields[j].get(objs.get(i)).toString().length()*1000);
                            cell.setCellValue(fields[j].get(objs.get(i)) == (Object) 0?"":fields[j].get(objs.get(i)).toString());
                        }
                        else{
                            cell.setCellValue(fields[j].get(objs.get(i)).toString());
                        }
                    }
                }
            }
        }
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getStyleIndex() {
        return styleIndex;
    }

    public void setStyleIndex(int styleIndex) {
        this.styleIndex = styleIndex;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }
}
