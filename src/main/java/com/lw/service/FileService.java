package com.lw.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author lw
 * @data 2023/11/8
 * @周三
 */
public interface FileService {
    /**
     * 阿里云OSS文件上传
     * @param file
     * @return
     */
    String upload(MultipartFile file);
}
