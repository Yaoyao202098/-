package com.lw.controller;

import com.lw.service.FileUploadService;
import com.lw.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author lw
 * @data 2023/10/13
 * @周五
 */
@Slf4j
@RestController
@RequestMapping("/file")
@CrossOrigin()
public class FileDownController {
    @Autowired
    private FileUploadService fileUploadService;
    private static final long chunkSize = 1024 * 1024 * 10;
//    @PostMapping("/down")
//    public Result<Boolean> downloadToClient(@RequestParam("file") MultipartFile file){
//        Result<Boolean> result=fileUploadService.FileDownLoad(file);
//        return result;
//    }

    @GetMapping("/download")
    public void downloadFile(@RequestParam("filename") String filename, HttpServletResponse response) {
        try {
            // 指定要下载的文件路径
            String fileDirectory = "D:\\";
            String filePath = fileDirectory + filename;

            File file = new File(filePath);

            if (file.exists()) {
                response.setHeader("Content-Disposition", "attachment; filename=" + filename);
                response.setContentType("application/octet-stream");
                response.setContentLength((int) file.length());

                try (
                        InputStream in = new FileInputStream(file);
                        OutputStream out = response.getOutputStream()   //将文件写入response
                ) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);//404
                log.error("文件不存在");
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        }
        log.info("下载" + filename + "成功");
    }

    @GetMapping("/chunkDownload")
    public ResponseEntity<byte[]> downloadChunk(
            @RequestParam(required = false) Long startByte
    ) throws IOException {
        Path filePath = Paths.get("D:\\League of Legends (TM) Client 2022-05-21 22-27-45.mp4");
        long totalSize = Files.size(filePath);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept-Ranges", "bytes");

        if (startByte != null && startByte > 0) {
            long endByte = totalSize - 1;
            headers.setContentType(MediaType.parseMediaType("application/octet-stream"));
            headers.add("Content-Range", "bytes " + startByte + "-" + endByte + "/" + totalSize);
            headers.add("Content-Length", String.valueOf(endByte - startByte + 1));
            return new ResponseEntity<>(Files.readAllBytes(filePath), headers, HttpStatus.PARTIAL_CONTENT);
        } else {
            headers.add("Content-Range", "bytes 0-" + (totalSize - 1) + "/" + totalSize);
            return new ResponseEntity<>(Files.readAllBytes(filePath), headers, HttpStatus.OK);
        }
    }
}


