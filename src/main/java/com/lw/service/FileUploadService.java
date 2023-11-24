package com.lw.service;

import com.lw.domain.PointFileIndex;
import com.lw.utils.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lw
 * @data 2023/9/27
 * @周三
 */
public interface FileUploadService {
    Result<Boolean> singleFileUpload(MultipartFile file);

    Result<Boolean> multipleFileUpload(MultipartFile[] files);

    Result<Boolean> singleFilePartUpload(MultipartFile filePart, Integer partIndex, Integer partNum, String fileName, String fileUid);

    Result<String> multipleFilePartUpload(MultipartFile filePart, Integer partIndex, Integer partNum, String fileName, String fileUid);

    Result<PointFileIndex> checkUploadFileIndex(PointFileIndex pointFileIndexVo);

    Result<String> singleFilePartPointUpload(MultipartFile filePart, String fileInfo);

    Result<Boolean> FileDownLoad(MultipartFile file);
    void DownLoadFile(String fileName, HttpServletResponse response) throws IOException;
}
