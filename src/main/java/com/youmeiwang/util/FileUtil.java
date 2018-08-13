package com.youmeiwang.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.youmeiwang.config.UrlConfig;
import com.youmeiwang.controller.FileController;

public class FileUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	public static void downFile(String filename, String path, HttpServletResponse response, HttpServletRequest request, ResourceLoader resourceLoader) {
		
		InputStream inputStream = null;
		ServletOutputStream servletOutputStream = null;
		try {
			org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:" + path);

			response.setContentType("application/vnd.ms-word");
			response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			response.addHeader("charset", "utf-8");
			response.addHeader("Pragma", "no-cache");
			String encodeName = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + encodeName + "\"; filename*=utf-8''" + encodeName);

			inputStream = resource.getInputStream();
			servletOutputStream = response.getOutputStream();
			IOUtils.copy(inputStream, servletOutputStream);

			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (servletOutputStream != null) {
					servletOutputStream.close();
					servletOutputStream = null;
				}
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				// 召唤jvm的垃圾回收器
				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Map<String, Object> upload(String userID, MultipartFile file) throws IllegalStateException, IOException {

		// 获取文件名
        String oldFileName = file.getOriginalFilename();
        logger.info("上传的文件名为：" + oldFileName);
        // 解决中文问题，liunx下中文路径，图片显示问题
        // 设置文件的前缀名
        String prefixName = generatePrefix();
        logger.info("保存的前缀名为：" + prefixName);
        // 获取文件的后缀名
        String suffixName = oldFileName.substring(oldFileName.lastIndexOf("."));
        logger.info("保存的后缀名为：" + suffixName);
        String newFileName = prefixName + suffixName;
        // 文件上传后的路径
        String filePath = UrlConfig.FILEPATHURL + userID + "//";
        
        File destination = new File(filePath + newFileName);
        destination.getAbsolutePath();
        
        // 检测是否存在目录
        if (!destination.getParentFile().exists()) {
        	destination.getParentFile().mkdirs();
        }
        
        file.transferTo(destination);
        
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("fileName", destination.getName());
    	data.put("filePath", destination.getAbsolutePath());
    	return data;
	}
	
	public static void download(String userID, String filePath, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		
		File file = new File(filePath);
		String prefixName = generatePrefix();
		String suffixName = filePath.substring(filePath.lastIndexOf("."));
		response.setContentType("application/force-download");// 设置强制下载不打开
		response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.addHeader("charset", "utf-8");
		response.addHeader("Pragma", "no-cache");
		String encodeName = URLEncoder.encode(prefixName + suffixName, StandardCharsets.UTF_8.toString());
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + encodeName + "\"; filename*=utf-8''" + encodeName);
        
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.gc();
        }
	}
	
	public static Map<String, Object> batchUpload(String userID, MultipartHttpServletRequest request) throws IOException {
		
		List<MultipartFile> files = request.getFiles("file");
        Map<String, Object> data = new HashMap<String, Object>();
        String fileName = null;
        for (MultipartFile file : files) {
        	fileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
        	Map<String, Object> map = upload(userID, file);
        	data.put(fileName, map);
        }
        return data;
	}
	
	public static String generatePrefix() {
        // 获得当前时间
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        // 转换为字符串
        String formatDate = format.format(new Date());
        // 随机生成文件编号
        String random = RandomUtil.getRandomNumber(6);
        return new StringBuffer().append(formatDate).append(random).toString();
    }
}