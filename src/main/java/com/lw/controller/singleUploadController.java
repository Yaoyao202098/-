package com.lw.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author lw
 * @data 2023/10/25
 * @周三
 */
@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/file")
public class singleUploadController {

    private String uploadPath = "D:\\javaProject\\files_Up_down\\file\\";

//    @PostMapping("singleAndMoreUpload")
//    public String singleUpload(@RequestParam("file") MultipartFile file) throws IOException {
//        /**
//         * IO流（InputStream/OutputStream）： 这是一种传统的方式，适用于处理大文件或流式上传。
//         * 可以使用FileInputStream和FileOutputStream来逐块读取和写入文件内容。
//         * 这种方式适用于需要更多控制的场景，例如在上传文件的同时进行数据处理或校验
//         */
//        if (file.isEmpty()) return "上传失败";
//        String fileName = file.getOriginalFilename();
//        try (
//                FileOutputStream os = new FileOutputStream(uploadPath + fileName);
//                InputStream is = file.getInputStream()
//        ) {
//            byte[] buf = new byte[1024 * 8];
//            int length;
//            while ((length = is.read(buf)) != -1) {//读取is文件输入字节流里面的数据
//                os.write(buf, 0, length);//通过os文件输出字节流写出去
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        log.info("IO方式文件上传成功");
//        return "上传成功";
//        /**
//         *transferTo方法： 这是Spring框架提供的一种方便的方式，用于将MultipartFile对象直接保存到文件系统。
//         * 它将上传文件的内容直接传输到指定的文件。这是一个很简单的方法，适用于大多数常见的文件上传场景。
//         */
////        -----------------------------------------------------------------------------
////        if (file.isEmpty()) return "上传失败";
////        String originalFilename = file.getOriginalFilename();
////        //使用transferTo()方法将上传的文件保存到指定的位置
////        file.transferTo(new File(uploadPath+originalFilename));
////        log.info("transferTo方式文件上传成功");
////        return "单文件上传成功";
////        -----------------------------------------------------------------------------
//        /**
//         * 这个方法将整个文件的内容写入文件。这对于小文件来说非常方便。
//         */
//        if (file.isEmpty()) return "上传失败";
//        try{
//            byte[] bytes= file.getBytes();
//            Path path= Paths.get(uploadPath, file.getOriginalFilename());
//            Files.write(path,bytes);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        log.info("write方式文件上传成功");
//        return "单文件上传成功";
//    }

    @PostMapping("singleAndMoreUpload")
    public String moreUpload(@RequestParam("file") MultipartFile[] files) throws IOException {
        if (files.length == 0){
            return "上传失败";
        }
        for (MultipartFile file : files) {
            try {
                String fileName= file.getOriginalFilename();
                File filepath=new File(uploadPath+fileName);
                file.transferTo(filepath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "多文件上传成功!";
    }
}
