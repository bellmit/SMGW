package star.sms._frame.utils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import star.sms._frame.utils.excel.annotation.ExcelAttribute;
import star.sms._frame.utils.excel.annotation.ExcelAttributeHandle;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CSVExportUtil<T> {
    private Class clazz;
    private Field fields[];

    public CSVExportUtil(Class clazz) {
        this.clazz = clazz;
        fields = clazz.getDeclaredFields();
    }
    
    /**
     * 普通格式导出
     */
    public void downCommonCsv(HttpServletResponse response, String fileName,List<String> list) throws Exception{
    	 fileName = URLEncoder.encode(fileName, "UTF-8");
         response.setContentType("application/octet-stream");
         response.setHeader("content-disposition", "attachment;filename=" + new String(fileName.getBytes("ISO8859-1")));
         response.setHeader("filename", fileName);
         BufferedWriter writer=null;
         OutputStreamWriter write=null;
         try {
        	 response.getOutputStream().write(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});
             write = new OutputStreamWriter(response.getOutputStream(), "utf-8");
             writer = new BufferedWriter(write);
             
             for(String str:list) {
            	 if(str!=null) {
            		 writer.write(str + "\r\n");
            	 }
             }
             writer.close();
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         } finally {
			if(write!=null){
				write.close();
				write=null;
			}
			if(writer!=null){
				writer.close();
				writer=null;
			}
		}
    }
    
    /**
     * 基于注解导出
     */
    public void down(HttpServletResponse response, String fileName,List<T> objs) throws Exception{
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("content-disposition", "attachment;filename= " + new String(fileName.getBytes("ISO8859-1")));
        response.setHeader("filename", fileName);
        BufferedWriter writer=null;
        OutputStreamWriter write=null;
        try {
            write = new OutputStreamWriter(response.getOutputStream(), "utf-8");
            writer = new BufferedWriter(write);
            //遍历头部文件
            List<ExcelAttribute> columns = new ArrayList<ExcelAttribute>();
			for (int k = 0; k < fields.length; k++) {
				ExcelAttribute ea = fields[k].getAnnotation(ExcelAttribute.class);
				if (ea != null) {
					columns.add(ea);
				}
			}
			columns.sort((final ExcelAttribute c1, final ExcelAttribute c2)->{
				if(c1.sort()>c2.sort()) {
					return 1;
				}else if(c1.sort()<c2.sort()) {
					return -1;
				}else {
					return 0;
				}
			});
			List<String> names = columns.stream().map(ExcelAttribute::name).collect(Collectors.toList());
			writer.write(StringUtils.join(names, ",") + "\r\n");

			//遍历塞数据
			for (int i = 0; i < objs.size(); i++) {
				String data = "";
				for (int j = 0; j < fields.length; j++) {
					if (fields[j].isAnnotationPresent(ExcelAttribute.class)) {
						fields[j].setAccessible(true);
						ExcelAttribute ea = fields[j].getAnnotation(ExcelAttribute.class);

						if (ea != null && fields[j].get(objs.get(i)) != null) {
							if (ea.format() != null && !ea.format().equals("")) {
								data = data+ExcelAttributeHandle.handle(ea.format(),
										fields[j].get(objs.get(i)).toString()).replace(",", "，")+",";
							} else {
								data = data+fields[j].get(objs.get(i)).toString().replace(",", "，")+",";
							}
						}
					}
				}
				if(data.endsWith(",")) data = data.substring(0,data.length()-1);
				writer.write(data + "\r\n");
			}
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
			if(write!=null){
				write.close();
				write=null;
			}
			if(writer!=null){
				writer.close();
				writer=null;
			}
		}
    }
}
