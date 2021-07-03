package star.sms._frame.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author star
 */
public class FileUtils {

	/* 判断文件是否存在 */
	public static boolean isExists(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	/* 判断是否是文件夹 */
	public static boolean isDir(String path) {
		File file = new File(path);
		if (file.exists()) {
			return file.isDirectory();
		} else {
			return false;
		}
	}

	/**
     * 如果已打成jar包，则返回jar包所在目录
     * 如果未打成jar，则返回target所在目录
     * @return
     */
    public static String getRootPath() throws UnsupportedEncodingException {
        // 项目的编译文件的根目录
        String path = URLDecoder.decode(FileUtils.class.getResource("/").getPath(), String.valueOf(StandardCharsets.UTF_8));
        if (path.startsWith("file:")) {
            int i = path.indexOf(".jar!");
            path = path.substring(0, i);
            path = path.replaceFirst("file:", "");
        }
        // 项目所在的目录
        return new File(path).getParentFile().getAbsolutePath();
    }
    
	/**
	 * 文件或者目录重命名
	 * 
	 * @param oldFilePath
	 *            旧文件路径
	 * @param newName
	 *            新的文件名,可以是单个文件名和绝对路径
	 * @return
	 */
	public static boolean renameTo(String oldFilePath, String newName) {
		try {
			File oldFile = new File(oldFilePath);
			// 若文件存在
			if (oldFile.exists()) {
				// 判断是全路径还是文件名
				if (newName.indexOf("/") < 0 && newName.indexOf("\\") < 0) {
					// 单文件名，判断是windows还是Linux系统
					String absolutePath = oldFile.getAbsolutePath();
					if (newName.indexOf("/") > 0) {
						// Linux系统
						newName = absolutePath.substring(0, absolutePath.lastIndexOf("/") + 1) + newName;
					} else {
						newName = absolutePath.substring(0, absolutePath.lastIndexOf("\\") + 1) + newName;
					}
				}
				File file = new File(newName);
				// 判断重命名后的文件是否存在
				if (file.exists()) {
					System.out.println("该文件已存在,不能重命名");
				} else {
					// 不存在，重命名
					return oldFile.renameTo(file);
				}
			} else {
				System.out.println("原该文件不存在,不能重命名");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/* 文件拷贝操作 */
	public static void copy(String sourceFile, String targetFile) {
		File source = new File(sourceFile);
		File target = new File(targetFile);
		target.getParentFile().mkdirs();
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(target);
			in = fis.getChannel();// 得到对应的文件通道
			out = fos.getChannel();// 得到对应的文件通道
			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* 写入Text文件操作 */
	public static void writeText(String filePath, String content, boolean isAppend) {
		FileOutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			outputStream = new FileOutputStream(filePath, isAppend);
			outputStreamWriter = new OutputStreamWriter(outputStream);
			bufferedWriter = new BufferedWriter(outputStreamWriter);
			bufferedWriter.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedWriter != null) {
					bufferedWriter.close();
				}
				if (outputStreamWriter != null) {
					outputStreamWriter.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 通过上一层目录和目录名得到最后的目录层次
	 * 
	 * @param previousDir
	 *            上一层目录
	 * @param dirName
	 *            当前目录名
	 * @return
	 */
	public static String getSaveDir(String previousDir, String dirName) {
		if (StringUtils.isNotEmpty(previousDir)) {
			dirName = previousDir + "/" + dirName + "/";
		} else {
			dirName = dirName + "/";
		}
		return dirName;
	}

	/**
	 * 如果目录不存在，就创建文件
	 * 
	 * @param dirPath
	 * @return
	 */
	public static String mkdirs(String dirPath) {
		try {
			File file = new File(dirPath);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dirPath;
	}

	/**
	 * 普通文件下载，文件在服务器里面
	 * 
	 * @param request
	 * @param response
	 */
	public static void download(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置文件下载时，文件流的格式
			String realPath = request.getServletContext().getRealPath("/");
			realPath = realPath + "index.jsp";
			System.out.println("下载地址=" + realPath);
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(realPath));
			BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
			// 下面这个变量保存的是要下载的文件拼接之后的完整路径
			String downName = realPath.substring(realPath.lastIndexOf("/") + 1);
			System.out.println("下载文件名=" + downName);
			response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(downName, "utf-8"));
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
			try {
				bis.close();
				bos.close();
			} catch (Exception e) {
				e.printStackTrace();
				;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("下载出错");
		}
	}

	/**
	 * 普通文件下载，文件路径固定
	 * 
	 * @param targetFile
	 *            下载的文件路径
	 * @param response
	 */
	public static void download(String targetFile, HttpServletResponse response) {
		try {
			System.out.println("下载文件路径=" + targetFile);
			// 设置文件下载时，文件流的格式
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(targetFile));
			BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
			// 下面这个变量保存的是要下载的文件拼接之后的完整路径
			String downName = targetFile.substring(targetFile.lastIndexOf("/") + 1);
			System.out.println("下载文件名=" + downName);
			response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(downName, "utf-8"));
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
			try {
				bis.close();
				bos.close();
			} catch (Exception e) {
				e.printStackTrace();
				;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("下载出错");
		}
	}

	/**
	 * 下载网络文件
	 * 
	 * @param targetFile
	 * @param response
	 */
	public static void downloadUrl(String targetFile, HttpServletResponse response) {
		try {
			URL website = new URL(targetFile);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream("D:/img/1.zip");// 例如：test.txt
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("下载出错");
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteFile(String fileName) {
		try {
			File sourceFile = new File(fileName);
			if (sourceFile.isFile() && sourceFile.exists()) {
				return sourceFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
