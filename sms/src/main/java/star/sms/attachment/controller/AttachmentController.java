package star.sms.attachment.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import star.sms._frame.base.BaseController;
import star.sms._frame.utils.StringUtils;
import star.sms.attachment.domain.Attachment;
import star.sms.attachment.service.AttachmentService;

/**
 * 附件表
 * 
 * @author star
 */
@Slf4j
@Controller
@RequestMapping("attachment")
public class AttachmentController extends BaseController {

	@Autowired
	private AttachmentService attachmentService;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test(ModelMap model) {
		return "/test";
	}
	public AttachmentController() {
		super();
	}
	@RequestMapping(value = "/upload")
	@ResponseBody
	public Object upload(@RequestParam("file") MultipartFile file) throws IOException {
		// 判断文件是否为空
		if (file.isEmpty()) {
			log.error("上传失败，请选择文件！");
			return ERROR("上传失败，请选择文件！");
		}
		String fileName = file.getOriginalFilename();
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

		Date dt = new Date();
		String year = String.format("%tY", dt);
		String month = String.format("%tm", dt);
		String day = String.format("%td", dt);

		File directory = new File("");
		String absolutePath = directory.getAbsolutePath();
		String filePath = absolutePath + "/upload/" + year + "/" + month + "/" + day + "/";
		File dir = new File(filePath);
		dir.mkdirs();
		String uuid = StringUtils.generateUUID();
		final File f = new File(filePath, uuid + "." + suffix);
		f.createNewFile();
		file.transferTo(f);

		Attachment attachment = new Attachment();
		attachment.setFileName(fileName);
		attachment.setType(suffix);
		attachment.setFileSize(file.getSize() + "");
		attachment.setFilePath("/upload/" + year + "/" + month + "/" + day + "/" + uuid + "." + suffix);
		attachment.setCreateTime(new Timestamp(System.currentTimeMillis()));
		if(getLoginUser()!=null) {
			attachment.setCreateUserId(getLoginUser().getId());
		}
		attachmentService.save(attachment);
		return SUCCESS(attachment);

	}
 
	@RequestMapping(value = "/download")
	public void download(HttpServletResponse response, Integer id) throws UnsupportedEncodingException {
		Attachment attachment = attachmentService.findOne(id);
		// 要上传的文件名字
	 
		// 通过文件的保存文件夹路径加上文件的名字来获得文件
		File directory = new File("");
		String absolutePath = directory.getAbsolutePath();
		String fileName = URLEncoder.encode(attachment.getFileName(), "UTF-8");

	 
	 
		File file = new File(absolutePath + attachment.getFilePath());
		// 当文件存在
		if (file.exists()) {
			response.setContentType("application/octet-stream");
			// 通过设置头信息给文件命名，也即是，在前端，文件流被接受完还原成原文件的时候会以你传递的文件名来命名
	        response.setHeader("content-disposition", "attachment;filename= " + new String(fileName.getBytes("ISO8859-1")));
	        response.setHeader("filename", fileName);
			// 进行读写操作
			byte[] buffer = new byte[1024];
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			try {
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				OutputStream os = response.getOutputStream();
				//  PrintWriter  os = response.getWriter();
				// 从源文件中读
				int i = bis.read(buffer);
				while (i != -1) {
					// 写到response的输出流中
					os.write(buffer, 0, i);
					i = bis.read(buffer);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			 	// 善后工作，关闭各种流
				try {
					if (bis != null) {
						bis.close();
					}
					if (fis != null) {
						fis.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}
	}

	
	private void zipFile(File[] subs, String baseName, ZipOutputStream zos,String[] name) throws IOException {
		for (int i = 0; i < subs.length; i++) {
			File f = subs[i];
			zos.putNextEntry(new ZipEntry(name[i]));
			FileInputStream fis = new FileInputStream(f);
			byte[] buffer = new byte[1024];
			int r = 0;
			while ((r = fis.read(buffer)) != -1) {
				zos.write(buffer, 0, r);
			}
			fis.close();
		}
		if (zos != null) {
			zos.close();
		}
	}

}
