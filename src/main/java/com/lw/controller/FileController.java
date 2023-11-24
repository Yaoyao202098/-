package com.lw.controller;

import com.lw.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lw
 * @data 2023/11/8
 * @周三
 */
@Slf4j
@Controller
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;
    @RequestMapping("/toPage")
    public String toPage() {
        return "singleupload";
    }

    /**
     * 文件上传接口
     * @param file
     * @return
     */
    @PostMapping("/ossUpload")
    public String upload(@RequestPart("file") MultipartFile file){
        String imgFileStr = fileService.upload(file);
        log.info(imgFileStr);
        return "singleupload";
    }
}
