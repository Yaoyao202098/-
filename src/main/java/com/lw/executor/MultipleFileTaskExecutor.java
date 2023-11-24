package com.lw.executor;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.Callable;

/**
 * @author lw
 * @data 2023/9/27
 * @周三
 * @Description: 多文件上传文件写入执行器
 */
@Slf4j
public class MultipleFileTaskExecutor implements Callable<String > {
//Callable类似于Runnable接口,但是Runnable不会返回结果,也不会报错
    private String filePath;
    private MultipartFile file;

    public MultipleFileTaskExecutor() {
        super();
    }

    public MultipleFileTaskExecutor(String filePath, MultipartFile file) {
        this.filePath = filePath;
        this.file = file;
    }

    @Override
    public String call() throws Exception {
        System.out.println("当前正在写入的文件是：" + file.getOriginalFilename());
        @Cleanup //关闭资源,相当于try..with..resources
        InputStream fileInputStream = null;
        @Cleanup
        FileOutputStream fileOutputStream = null;
        String fileInfo = null;
        try {
            String filename = file.getOriginalFilename();
            fileOutputStream = new FileOutputStream(filePath + filename);
            fileInputStream = file.getInputStream();

            byte[] buf = new byte[1024 * 8];
            int length;
            while ((length = fileInputStream.read(buf)) != -1) {//读取fis文件输入字节流里面的数据
                fileOutputStream.write(buf, 0, length);//通过fos文件输出字节流写出去
            }
            fileInfo = "文件名:" + filename + ";文件大小:" + file.getSize();
        } catch (Exception e) {
            log.error("{} 上传失败", file.getOriginalFilename());
            throw new Exception(e);
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                log.error("{} 上传时,关闭流错误！", file.getOriginalFilename(), e);
                e.printStackTrace();
            }
        }
        return fileInfo;
    }
}
