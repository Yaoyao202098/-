package com.lw.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author lw
 * @data 2023/10/16
 * @周一
 */
//@Slf4j
//public class FileUtil {
//    public static Result<Boolean> downloadFile(String fileName, HttpServletResponse response){
//        InputStream is=null;
//        OutputStream os=null;
//        try {
//            os=response.getOutputStream();
//            response.setContentType("application/octet-stream");
////            response.setContentType("application/x-download;charset=UTF-8");
//            response.setHeader("Content-Disposition","attachment;filename="+fileName);
//            String downLoadPath=System.getProperty("user.dir") + "\\file\\"+fileName;
//            File file=new File(downLoadPath);
//            if (!file.exists()){
//                log.error("未上传文件或者文件不存在!");
//                return Result.error(false,"文件不存在");
//            }
//            is=new FileInputStream(file);
//            byte[] buffer=new byte[1024];
//            int len;
//            while((len= is.read(buffer))!=-1){
//                os.write(buffer,0,len);
//            }
//        } catch (Exception e) {
//            log.error("下载文件异常", e);
//            return Result.error(false, "下载文件异常: " + e.getMessage());
//        }finally {
//            try {
//                if (is != null) {
//                    is.close();
//                }
//                if (os != null) {
//                    os.close();
//                }
//            } catch (Exception e) {
//                log.error("关流异常", e);
//                e.printStackTrace();
//            }
//        }
//        return Result.success(true,"下载成功");
//    }
//}
@Slf4j
public class FileUtil {
    public static void downloadFile(String fileName, HttpServletResponse response) {
        InputStream is = null;
        OutputStream os = null;
        try {
            String downLoadPath = System.getProperty("user.dir") + File.separator + "file" + File.separator + fileName;
            File file = new File(downLoadPath);
            if (!file.exists()) {
                log.error("未上传文件或者文件不存在!");
                return;
            }
            os = response.getOutputStream();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            is = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } catch (Exception e) {
            log.error("下载文件异常", e);
            return;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                log.error("关闭流异常", e);
            }
        }
    }
}

