package com.lw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lw
 * @data 2023/10/24
 * @周二
 */
@CrossOrigin
@RestController
@RequestMapping("/file")
public class ChunkUploadController {
    @Autowired
    private ResourceLoader resourceLoader;
    private String uploadPath = "D:\\javaProject\\files_Up_down\\file\\";
    private Map<String, List<File>> chunksMap = new ConcurrentHashMap<>();

    /**
     * @param currentChunk 当前块数
     * @param totalChunks
     * @param chunk        当前分片文件
     * @param fileName     文件名
     * @throws IOException
     */
    @PostMapping("/chunkUpload")
    public void upload(@RequestParam int currentChunk,
                       @RequestParam int totalChunks,
                       @RequestParam MultipartFile chunk,
                       @RequestParam String fileName) throws IOException {
        // 将分片保存到临时文件夹中
        String chunkName = chunk.getOriginalFilename() + "." + currentChunk;  //如chunkName.0
        File chunkFile = new File(uploadPath, chunkName);
        chunk.transferTo(chunkFile);

        // 记录分片上传状态
        List<File> chunkList = chunksMap.get(fileName);
        if (chunkList == null) {
            chunkList = new ArrayList<>(totalChunks);
            chunksMap.put(fileName, chunkList);
        }

        chunkList.add(chunkFile);
    }

    @PostMapping("/chunkMerge")
    public String merge(@RequestParam String fileName) throws IOException {

        // 获取所有分片，并按照分片的顺序将它们合并成一个文件
        List<File> chunkList = chunksMap.get(fileName);
        if (chunkList == null || chunkList.isEmpty()) {
            throw new RuntimeException("分片不存在");
        }

        File outputFile = new File(uploadPath, fileName);
        try (
                FileChannel outChannel = new FileOutputStream(outputFile).getChannel()
        ) {
            for (File file : chunkList) {
                try (
                        FileChannel inChannel = new FileInputStream(file).getChannel()
                ) {
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                }
                //file.delete(); // 删除分片
            }
        }


        chunksMap.remove(fileName); // 删除记录
        // 获取文件的访问URL
        Resource resource = resourceLoader.getResource("file:" + uploadPath + fileName); //由于是本地文件，所以开头是"file",表示资源类型为文件类型，如果是服务器，请改成自己服务器前缀
        return resource.getURI().toString();
    }
}
