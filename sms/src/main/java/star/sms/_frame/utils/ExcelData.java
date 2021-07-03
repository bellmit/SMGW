package star.sms._frame.utils;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * 解析excel 上传数据
 * @author Administrator
 *
 */
public class ExcelData {


	private final static Logger log = LoggerFactory
			.getLogger(ExcelData.class);

	public static List<String[]> getExcelData(MultipartFile file) throws IOException{
		//获得Workbook工作薄对象
		Workbook workbook = getWorkBook(file);
		//创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
		List<String[]> list = new ArrayList<String[]>();
		if(workbook != null){
			for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
				//获得当前sheet工作表
				Sheet sheet = workbook.getSheetAt(sheetNum);
				if(sheet == null){
					continue; 
				}
				//获得当前sheet的开始行
				int firstRowNum  = sheet.getFirstRowNum();
				//获得当前sheet的结束行
				int lastRowNum = sheet.getLastRowNum();
				
				int firstColNum = 0;
				//获得当前行的列数
				int lastColNum = 0;
				//循环除了第一行的所有行
				for(int rowNum = firstRowNum;rowNum <= lastRowNum;rowNum++){
					//获得当前行
					Row row = sheet.getRow(rowNum);
					if(row == null){
						continue;
					}
					if (rowNum == firstRowNum) {
						firstColNum = row.getFirstCellNum();
						//获得当前行的列数
						lastColNum = row.getLastCellNum();
					}
					//获得当前行的开始列
					
					String[] cells = new String[lastColNum-firstColNum];
					//循环当前行
					for(int cellNum = firstColNum; cellNum < lastColNum;cellNum++){

						Cell cell = row.getCell(cellNum);
						if (cell == null) {
							cells[cellNum] = "";
						} else {
							cells[cellNum] = getCellValue(cell);
						}
					}
					list.add(cells);
				}
			}
		}
		return list;
	}
	public static List<String[]> getExcelData(MultipartFile file,int sheetNum,int firstRowNum,int lastRowNum,int firstColNum,int lastColNum ) throws IOException{
		//获得Workbook工作薄对象
		Workbook workbook = getWorkBook(file);
		//创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
		List<String[]> list = new ArrayList<String[]>();
		int sheetSum = sheetNum;
		if(workbook != null){
			if (sheetNum < 0) {
				sheetSum = workbook.getNumberOfSheets();
			}
			for (sheetNum = 0; sheetNum < sheetSum; sheetNum++) {

				//获得当前sheet工作表
				Sheet sheet = workbook.getSheetAt(sheetNum);
				String sheetName = sheet.getSheetName();
				//获得当前sheet的开始行
				if(firstRowNum < 0){
					firstRowNum  = sheet.getFirstRowNum();
				}

				//获得当前sheet的结束行
				if (lastRowNum<0) {
					lastRowNum = sheet.getLastRowNum();
				}

				for(int rowNum = firstRowNum;rowNum <= lastRowNum;rowNum++){
					//获得当前行
					Row row = sheet.getRow(rowNum);
					if(row == null){
						continue;
					}
					if (rowNum == firstRowNum) {
						if (firstColNum <0) {
							firstColNum = row.getFirstCellNum();
						}

						//获得当前行的列数
						if (lastColNum<0) {
							lastColNum = row.getLastCellNum();
						}
					}
					//获得当前行的开始列

					String[] cells = new String[lastColNum-firstColNum+1];
					cells[0] = sheetName;
					int i = 1;
					//循环当前行
					for(int cellNum = firstColNum; cellNum < lastColNum;cellNum++){

						Cell cell = row.getCell(cellNum);
						if (cell == null) {
							cells[i++] = "";
						} else {
							cells[i++] = getCellValue(cell);
						}
					}
					list.add(cells);
				}
			}
		}
		return list;
	}


	/**
	 * 检查文件
	 * @param file
	 * @throws IOException
	 */
	public static  void checkFile(MultipartFile file) throws IOException{
		//判断文件是否存在
		if(null == file){
			log.error("文件不存在！");
		}
		//获得文件名
		String fileName = file.getOriginalFilename();
		//判断文件是否是excel文件
		if(!fileName.endsWith("xls") && !fileName.endsWith("xlsx")){
			log.error(fileName + "不是excel文件");
		}
	}


	public static Workbook getWorkBook(MultipartFile file) {
		//获得文件名
		String fileName = file.getOriginalFilename();
		//创建Workbook工作薄对象，表示整个excel
		Workbook workbook = null;
		try {
			//获取excel文件的io流
			InputStream is = file.getInputStream();
			//根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
			if(fileName.endsWith("xls")){
				//2003
				workbook = new HSSFWorkbook(is);
			}else if(fileName.endsWith("xlsx")){
				//2007 及2007以上
				workbook = new XSSFWorkbook(is);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return workbook;
	}

	public static String getCellValue(Cell cell){
		String cellValue = "";
		if(cell == null){
			return cellValue;
		}
		//判断数据的类型
		switch (cell.getCellType()){
		case Cell.CELL_TYPE_NUMERIC: //数字
			cellValue = stringDateProcess(cell);
			break;
		case Cell.CELL_TYPE_STRING: //字符串
			cellValue = String.valueOf(cell.getStringCellValue());
			break;
		case Cell.CELL_TYPE_BOOLEAN: //Boolean
			cellValue = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA: //公式
			cellValue = String.valueOf(cell.getCellFormula());
			break;
		case Cell.CELL_TYPE_BLANK: //空值
			cellValue = "";
			break;
		case Cell.CELL_TYPE_ERROR: //故障
			cellValue = "非法字符";
			break;
		default:
			cellValue = "未知类型";
			break;
		}
		return cellValue;
	}


	/**
	 * 时间格式处理
	 * @return
	 * @author Liu Xin Nan
	 * @data 2017年11月27日
	 */
	public static String stringDateProcess(Cell cell){
		String result = new String();  
		if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式  
			SimpleDateFormat sdf = null;  
			if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {  
				sdf = new SimpleDateFormat("HH:mm");  
			} else {// 日期  
				sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
			}  
			Date date = cell.getDateCellValue();  
			result = sdf.format(date);  
		} else if (cell.getCellStyle().getDataFormat() == 58) {  
			// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)  
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
			double value = cell.getNumericCellValue();  
			Date date = org.apache.poi.ss.usermodel.DateUtil  
					.getJavaDate(value);  
			result = sdf.format(date);  
		} else {  
			double value = cell.getNumericCellValue();  
			CellStyle style = cell.getCellStyle();  
			DecimalFormat format = new DecimalFormat();  
			String temp = style.getDataFormatString();  
			// 单元格设置成常规  
			if (temp.equals("General")) {  
				format.applyPattern("#.#####");  
			}  
			result = format.format(value);  
		}  
		return result;
	}
	public static HSSFWorkbook createWorkBook(List<Map<String, Object>> list, String []keys, String columnNames[]) {
		// 创建excel工作簿
		HSSFWorkbook wb = new HSSFWorkbook();
		// 创建第一个sheet页，并命名
		HSSFSheet sheet = wb.createSheet(list.get(0).get("sheetName").toString());
		// 设置列宽
		for(int i=0;i<keys.length;i++){
			//最后一列为附件URL地址,列宽设置大一些
			if(i==(keys.length-1)){
				sheet.setColumnWidth((short) i, (short) (200*120));
			}else{
				sheet.setColumnWidth((short) i, (short) (50*60));
			}
		}

		// 创建第一行，并设置其单元格格式
		HSSFRow row = sheet.createRow((short) 0);
		row.setHeight((short)500);
		// 单元格格式(用于列名)
		HSSFCellStyle cs = wb.createCellStyle();
		HSSFFont f = wb.createFont();
		f.setFontName("宋体");
		f.setFontHeightInPoints((short) 10);
		f.setBold(true);
		cs.setFont(f);
		cs.setAlignment(HorizontalAlignment.CENTER);// 水平居中
		cs.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中
		cs.setLocked(true);
		cs.setWrapText(true);//自动换行
		//设置列名
		for(int i=0;i<columnNames.length;i++){
			HSSFCell cell = row.createCell(i);
			cell.setCellValue(columnNames[i]);
			cell.setCellStyle(cs);
		}

		//设置首行外,每行每列的值(Row和Cell都从0开始)
		for (short i = 1; i < list.size(); i++) {
			HSSFRow row1 = sheet.createRow((short) i);
			String flag = "";
			//在Row行创建单元格
			for(short j=0;j<keys.length;j++){
				HSSFCell cell = row1.createCell(j);
				cell.setCellValue(list.get(i).get(keys[j]) == null?" ": list.get(i).get(keys[j]).toString());
				if(list.get(i).get(keys[j])!=null){
					if("优".equals(list.get(i).get(keys[j]).toString())){
						flag = "优";
					}else if("差".equals(list.get(i).get(keys[j]).toString())) {
						flag = "差";
					}
				}
			}
			//设置该行样式
			HSSFFont f2 = wb.createFont();
			f2.setFontName("宋体");
			f2.setFontHeightInPoints((short) 10);
			if("优".equals(flag)){
				HSSFCellStyle cellStyle = wb.createCellStyle();
				cellStyle.setFont(f2);
				cellStyle.setAlignment(HorizontalAlignment.CENTER);// 左右居中
				cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
				cellStyle.setLocked(true);
				cellStyle.setWrapText(true);//自动换行
				cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());// 设置背景色
				cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				//依次为每个单元格设置样式
				for(int m=0;m<keys.length;m++){
					HSSFCell hssfCell = row1.getCell(m);
					hssfCell.setCellStyle(cellStyle);
				}
			}else if("差".equals(flag)){
				HSSFCellStyle cellStyle2 = wb.createCellStyle();
				cellStyle2.setFont(f2);
				cellStyle2.setAlignment(HorizontalAlignment.CENTER);// 左右居中
				cellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
				cellStyle2.setLocked(true);
				cellStyle2.setWrapText(true);//自动换行
				cellStyle2.setFillForegroundColor(HSSFColor.HSSFColorPredefined.RED.getIndex());// 设置背景色
				cellStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				for(int m=0;m<keys.length;m++){
					HSSFCell hssfCell = row1.getCell(m);
					hssfCell.setCellStyle(cellStyle2);
				}
			}else{
				HSSFCellStyle cs2 = wb.createCellStyle();
				cs2.setFont(f2);
				cs2.setAlignment(HorizontalAlignment.CENTER);// 左右居中
				cs2.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
				cs2.setLocked(true);
				cs2.setWrapText(true);//自动换行
				for(int m=0;m<keys.length;m++){
					HSSFCell hssfCell = row1.getCell(m);
					hssfCell.setCellStyle(cs2);
				}
			}
		}
		return wb;
	}
	public static void downloadWorkBook(List<Map<String,Object>> list,
										String keys[],
										String columnNames[],
										String fileName,
										HttpServletResponse response) throws IOException{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ExcelData.createWorkBook(list,keys,columnNames).write(os);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] content = os.toByteArray();
		InputStream is = new ByteArrayInputStream(content);
		// 设置response参数
		response.reset();
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment;filename="+ new String((fileName + ".xls").getBytes(), "iso-8859-1"));
		ServletOutputStream out = response.getOutputStream();
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
		} catch (final IOException e) {
			throw e;
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}
	}
	public static void main(String args[]) {
		double value = 6.44;  
		DecimalFormat df = new DecimalFormat();  
		df.applyPattern("#.#####");  
		System.out.println(df.format(value));
    } 
}