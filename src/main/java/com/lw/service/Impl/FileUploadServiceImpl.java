package com.lw.service.Impl;

import com.alibaba.fastjson2.JSONObject;
import com.lw.domain.PointFileIndex;
import com.lw.executor.MultipleFileTaskExecutor;
import com.lw.executor.PartMergeTaskExecutor;
import com.lw.service.FileUploadService;
import com.lw.utils.FileUtil;
import com.lw.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lw
 * @data 2023/9/27
 * @周三
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final Map<String, Integer> isMergePart = new HashMap<>();

    private final Map<String, List<String>> uploadProgress = new HashMap<>();

    //创建线程池
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 15,
            30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(4));
    /**
     * 单文件上传
     * 直接将传入的文件通过io流形式直接写入(服务器)指定路径下
     *
     * @param file 上传的文件
     * @return
     */
    @Override
    public Result<Boolean> singleFileUpload(MultipartFile file) {
        //System.getProperty("user.dir")获取项目的位置
        String filePath = System.getProperty("user.dir") + "\\file\\";
        File dir = new File(filePath);
        if (!dir.exists()) dir.mkdir();
        if (file == null) {
            return Result.error(false, "上传文件为空！");
        }
        InputStream fis = null;
        FileOutputStream fos = null;
        try {
            String filename = file.getOriginalFilename();
            fos = new FileOutputStream(filePath + filename);
            fis = file.getInputStream();

            byte[] buf = new byte[1024 * 8];
            int length;
            while ((length = fis.read(buf)) != -1) {//读取fis文件输入字节流里面的数据
                fos.write(buf, 0, length);//通过fos文件输出字节流写出去
            }
            log.info("单文件上传完成！文件路径:{},文件名:{},文件大小:{}", filePath, filename, file.getSize());
            return Result.success(true, "单文件上传完成！");
        } catch (IOException e) {
            return Result.error(true, "单文件上传失败！");
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 多文件上传
     * 直接将传入的多个文件通过io流形式直接写入(服务器)指定路径下
     * 写入指定路径下是通过多线程进行文件写入的，文件写入线程执行功能就和上面单文件写入是一样的
     *
     * @param files 上传的所有文件
     * @return
     */
    @Override
    public Result<Boolean> multipleFileUpload(MultipartFile[] files) {
        //实际情况下，这些路径都应该是服务器上面存储文件的路径
        String filePath = System.getProperty("user.dir") + "\\file\\";
        File dir = new File(filePath);
        if (!dir.exists()) dir.mkdir();

        if (files.length == 0) {
            return Result.error(false, "上传文件为空！");
        }
        ArrayList<String> uploadFiles = new ArrayList<>();
        try {
            ArrayList<Future<String>> futures = new ArrayList<>();
            //使用多线程来完成对每个文件的写入
            for (MultipartFile file : files) {
                futures.add(threadPoolExecutor.submit(new MultipleFileTaskExecutor(filePath, file)));
            }

            //这里主要用于监听各个文件写入线程是否执行结束
            int count = 0;
            while (count != futures.size()) {
                for (Future<String> future : futures) {
                    if (future.isDone()) {
                        uploadFiles.add(future.get());
                        count++;
                    }
                }
                Thread.sleep(1);
            }
            log.info("多文件上传完成！文件路径:{},文件信息:{}", filePath, uploadFiles);
            return Result.success(true, "多文件上传完成！");
        } catch (Exception e) {
            log.error("多文件分片上传失败!", e);
            return Result.error(true, "多文件上传失败！");
        }

    }
    /**
     * 单文件分片上传
     * 直接将传入的文件分片通过io流形式写入(服务器)指定临时路径下
     * 然后判断是否分片都上传完成，如果所有分片都上传完成的话，就把临时路径下的分片文件通过流形式读入合并并从新写入到(服务器)指定文件路径下
     *
     *
     * @param filePart  分片文件
     * @param partIndex 当前分片值
     * @param partNum   所有分片数
     * @param fileName  当前文件名称
     * @param fileUid   当前文件uid
     * @return
     */
    @Override
    public Result<Boolean> singleFilePartUpload(MultipartFile filePart, Integer partIndex, Integer partNum, String fileName, String fileUid) {
        //实际情况下，这些路径都应该是服务器上面存储文件的路径
        String filePath = System.getProperty("user.dir") + "\\file\\";//文件存放路径
        String tempPath = filePath + "temp\\" + fileUid;//临时文件存放路径
        File dir = new File(tempPath);
        if (!dir.exists()) dir.mkdirs();
        //生成一个临时文件名
        String tempFileNamePath = tempPath + "\\" + fileName + "_" + partIndex + ".part";
        try {
            //将分片存储到临时文件夹中
            filePart.transferTo(new File(tempFileNamePath));
            File tempDir = new File(tempPath);
            File[] tempFiles = tempDir.listFiles(); //把所有分片装入File[]中
            //进行了一个简单的判断,如果所要判断的元素为null,则返回空指针异常 NullPointerException,否则直接返回对应的对象
            if (partNum.equals(Objects.requireNonNull(tempFiles).length)) {
                System.out.println("所有分片上传完成，上传的总分片数为：" + partNum + "; 接受到的总分片数为:" + tempFiles.length);

                FileOutputStream fileOutputStream = new FileOutputStream(filePath + fileName);
                //这里如果分片很多的情况下，可以采用多线程来执行
                for (int i = 0; i < partNum; i++) {
                    //读取分片数据，进行分片合并
                    FileInputStream fileInputStream = new FileInputStream(tempPath + "\\" + fileName + "_" + i + ".part");
                    byte[] buf = new byte[1024 * 8];//8kb
                    int length;
                    while ((length = fileInputStream.read(buf)) != -1) {//读取fis文件输入字节流里面的数据
                        fileOutputStream.write(buf, 0, length);//通过fos文件输出字节流写出去
                    }
                    fileInputStream.close();
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                //删除临时文件
                for (int i = 0; i < partNum; i++) {
                    boolean isDelete=new File(tempPath + "\\" + fileName + "_" + i + ".part").delete();
                    if (isDelete){
                        System.out.println(tempPath + "\\" + fileName + "_" + i + ".part"+"已删除");
                    }
                }
                dir.delete();
            }
        } catch (Exception e) {
            log.error("单文件分片上传失败!", e);
            return Result.error(false, "单文件分片上传失败");
        }
        //通过返回成功的分片值，来验证分片是否有丢失
        return Result.success(true,partIndex.toString());
    }

    /**
     * 多文件分片上传
     * 先将所有文件分片读入到(服务器)指定临时路径下
     * 然后判断对已经上传所有分片的文件进行合并，此处是通过多线程对每一个文件的分片文件进行合并的
     *z这里参数不用数组形式是因为前端执行的是一个文件,有多少个文件就执行了多少次此方法
     * @param filePart  分片文件
     * @param partIndex 当前分片值
     * @param partNum   总分片数
     * @param fileName  当前文件名称
     * @param fileUid   当前文件uid
     * @return
     */
    @Override
    public Result<String> multipleFilePartUpload(MultipartFile filePart, Integer partIndex, Integer partNum, String fileName, String fileUid) {
        //实际情况下，这些路径都应该是服务器上面存储文件的路径
        String filePath = System.getProperty("user.dir") + "\\file\\";//文件存放路径
        String tempPath = filePath + "temp\\" + fileUid;//临时文件存放路径
        File dir = new File(tempPath);
        if (!dir.exists()) dir.mkdirs();
        //生成一个临时文件名
        String tempFileNamePath = tempPath + "\\" + fileName + "_" + partIndex + ".part";
        try {
            filePart.transferTo(new File(tempFileNamePath));
            File tempDir = new File(tempPath);
            File[] tempFiles = tempDir.listFiles();
            //如果临时文件夹中分片数量和实际分片数量一致的时候，就需要进行分片合并
            if (partNum.equals(tempFiles.length)) {
                isMergePart.put(fileUid, tempFiles.length);
                System.out.println(fileName + ":所有分片上传完成，预计总分片：" + partNum + "; 实际总分片:" + tempFiles.length);
                //使用多线程来完成对每个文件的合并
                Future<Integer> submit = threadPoolExecutor.submit(new PartMergeTaskExecutor(filePath, tempPath, fileName, partNum));
                System.out.println("上传文件名:" + fileName + "; 总大小：" + submit.get());
                isMergePart.remove(fileUid);
            }
        } catch (Exception e) {
            log.error("{}:多文件分片上传失败!", fileName, e);
            return Result.error("", "多文件分片上传失败");
        }
        //通过返回成功的分片值，来验证分片是否有丢失
        return Result.success(partIndex.toString(),fileUid);
    }

    /**
     * 根据文件md5来判断此文件在服务器中是否未上传完整，
     * 如果没上传完整，则返回相关上传进度等信息
     * @param pointFileIndex
     * @return
     */
    @Override
    public Result<PointFileIndex> checkUploadFileIndex(PointFileIndex pointFileIndex) {
        try {
            List<String> list = uploadProgress.get(pointFileIndex.getFileMd5());
            if (list == null) list = new ArrayList<>();
            pointFileIndex.setParts(list);
            System.out.println("已上传部分：" + list);
            return Result.success(pointFileIndex);
        } catch (Exception e) {
            log.error("上传文件检测异常!", e);
            return Result.error("上传文件检测异常!");
        }
    }

    /**
     * 单文件(分片)断点上传
     *
     * @param filePart 需要上传的分片文件
     * @param fileInfo 当前需要上传的分片文件信息，如文件名，文件总分片数量等
     * @return
     */
    @Override
    public Result<String> singleFilePartPointUpload(MultipartFile filePart, String fileInfo) {
        //JSON字符串转换成Java对象:JSONObject.parseObject(JSON字符串,Java对象.class)
        PointFileIndex pointFileIndex = JSONObject.parseObject(fileInfo, PointFileIndex.class);
        //实际情况下，这些路径都应该是服务器上面存储文件的路径
        String filePath = System.getProperty("user.dir") + "\\file\\";//文件存放路径
        String tempPath = filePath + "temp\\" + pointFileIndex.getFileMd5();//临时文件存放路径
        File dir = new File(tempPath);
        if (!dir.exists()) dir.mkdirs();

        //定义part文件名
        String tempFileNamePath = tempPath + "\\" + pointFileIndex.getFileName() + "_" + pointFileIndex.getPartIndex() + ".part";
        try {
            //将分片存储到临时文件夹中
            filePart.transferTo(new File(tempFileNamePath));

            List<String> partIndex = uploadProgress.get(pointFileIndex.getFileMd5());
            if (partIndex == null) {       //Objects.isNull(partIndex)
                partIndex = new ArrayList<>();
            }
            partIndex.add(pointFileIndex.getPartIndex().toString());
            uploadProgress.put(pointFileIndex.getFileMd5(), partIndex);

            File[] tempFiles = dir.listFiles();
            if (pointFileIndex.getPartNum().equals(tempFiles.length)) {
                System.out.println("所有分片上传完成，本该上传总分片：" + pointFileIndex.getPartNum() + "; 实际上传总分片:" + tempFiles.length);
                //读取分片数据，进行分片合并
                FileOutputStream fileOutputStream = new FileOutputStream(filePath + pointFileIndex.getFileName());
                //这里如果分片很多的情况下，可以采用多线程来执行
                for (int i = 0; i < pointFileIndex.getPartNum(); i++) {
                    FileInputStream fileInputStream = new FileInputStream(tempPath + "\\" + pointFileIndex.getFileName() + "_" + i + ".part");
                    byte[] buf = new byte[1024 * 8];
                    int length;
                    while ((length = fileInputStream.read(buf)) != -1) {//读取fis文件输入字节流里面的数据
                        fileOutputStream.write(buf, 0, length);//通过fos文件输出字节流写出去
                    }
                    fileInputStream.close();
                }
                fileOutputStream.flush();
                fileOutputStream.close();
//                for (int i = 0; i < pointFileIndex.getPartNum(); i++) {
//                    boolean delete = new File(tempPath + "\\" + pointFileIndex.getFileName() + "_" + i + ".part").delete();
//                    File file = new File(tempPath + "\\" + pointFileIndex.getFileName() + "_" + i + ".part");
//                }
                uploadProgress.remove(pointFileIndex.getFileMd5());
            }

        } catch (Exception e) {
            log.error("单文件分片上传失败!", e);
            return Result.error(pointFileIndex.getFileMd5(), "单文件分片上传失败");
        }
        //通过返回成功的分片值，来验证分片是否有丢失
        return Result.success(pointFileIndex.getFileMd5(), pointFileIndex.getPartIndex().toString());
    }

    @Override
    public Result<Boolean> FileDownLoad(MultipartFile file) {
//        String filename= file.getOriginalFilename();
//        log.info("文件名为:{}",filename);
        return Result.success(true,"test成功");
    }

    @Override
    public void DownLoadFile(String fileName, HttpServletResponse response) throws IOException {

         FileUtil.downloadFile(fileName, response);
    }
}