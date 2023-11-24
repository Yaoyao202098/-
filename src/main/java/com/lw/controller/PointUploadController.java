package com.lw.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author lw
 * @data 2023/10/25
 * @周三
 */

@RestController
@CrossOrigin
@RequestMapping("/file")
public class PointUploadController {
    private final String uploadDirectory = "D:\\javaProject\\files_Up_down\\file\\";

    @PostMapping("/pointUpload")
    public String upload(
            @RequestParam MultipartFile chunk,
            @RequestParam String fileName,
            @RequestParam int currentChunk,
            @RequestParam int totalChunks
    ) throws IOException {
        String chunkName = fileName + "." + currentChunk;
        File chunkFile = new File(uploadDirectory, chunkName);
        chunk.transferTo(chunkFile);

        if (currentChunk == totalChunks - 1) {
            mergeChunks(fileName, totalChunks);
        }
        return "分片上传成功";
    }

    private void mergeChunks(String fileName, int totalChunks) throws IOException {
        File outputFile = new File(uploadDirectory, fileName);  //实例化整个文件对象
        try (
                FileOutputStream outputStream = new FileOutputStream(outputFile, true)  //写入到outputFile,true代表当文件存在时,追加数据到outputFile
        ){
            for (int i = 0; i < totalChunks; i++) {
                String chunkName = fileName + "." + i;
                File chunkFile = new File(uploadDirectory, chunkName);
                byte[] chunkData = FileUtils.readFileToByteArray(chunkFile);//读取chunk所有内容并将其存储为字节数组
                outputStream.write(chunkData);
//                chunkFile.delete(); // 删除分片
            }
        }
    }
}
