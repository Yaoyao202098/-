package com.lw.executor;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.Callable;
/**
 * @author lw
 * @data 2023/9/27
 * @周三
 * @Description: 多文件分片上传，分片文件合并写入执行器
 */
@Slf4j
public class PartMergeTaskExecutor implements Callable<Integer>{
    private String filePath;

    private String tempPath;

    private String fileName;

    private Integer partNum;

    public PartMergeTaskExecutor() {
        super();
    }

    public PartMergeTaskExecutor(String filePath, String tempPath, String fileName, Integer partNum) {
        this.filePath = filePath;
        this.tempPath = tempPath;
        this.fileName = fileName;
        this.partNum = partNum;
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("当前正在合并的文件是：" + fileName);
        InputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        Integer partSizeTotal = 0;
        try {
            fileOutputStream = new FileOutputStream(filePath + fileName);

            for (int i = 0; i < partNum; i++) {
                //读取分片文件
                fileInputStream = new FileInputStream(tempPath + "\\" + fileName + "_" + i + ".part");
                byte[] buf = new byte[1024 * 8];//8kb
                int length;
                while ((length = fileInputStream.read(buf)) != -1) {//读取fis文件输入字节流里面的数据
                    fileOutputStream.write(buf, 0, length);//通过fos文件输出字节流写出去
                    partSizeTotal += length;
                }
                fileInputStream.close();
            }
        } catch (Exception e) {
            log.error("{}:文件分片合并失败!", fileName, e);
            throw new Exception(e);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                log.error("{} 文件分片合并完成后,关闭输入输出流错误！", fileName, e);
                e.printStackTrace();
            }
        }
        return partSizeTotal;
    }
}
