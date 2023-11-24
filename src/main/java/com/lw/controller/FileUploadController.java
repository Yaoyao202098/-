package com.lw.controller;


import com.lw.service.FileUploadService;
import com.lw.utils.Result;
import com.lw.domain.PointFileIndex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author lw
 * @data 2023/9/27
 * @周三
 */
@Slf4j
@RestController
@RequestMapping("/file")
@CrossOrigin()
public class FileUploadController {
    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 单文件上传
     * @param file
     */
    @PostMapping("/singleFileUpload")
    public Result<Boolean> singleFileUpload(@RequestParam("file") MultipartFile file) {
        log.info("单文件上传操作,文件大小为:{}", file.getSize());
        Result<Boolean> result = fileUploadService.singleFileUpload(file);
        return result;
    }

    /**
     * 多文件上传
     *
     * @param files
     */
    @PostMapping("/multipleFileUpload")
    public Result<Boolean> multipleFileUpload(@RequestParam("files") MultipartFile[] files) {
        log.info("多文件上传操作,文件个数:{}", files.length);
        Result<Boolean> result = fileUploadService.multipleFileUpload(files);
        return result;
    }

    /**
     * 单文件分片上传
     *
     * @param filePart  单次分片的文件
     * @param partIndex 当前分片定位
     * @param partNum   文件分片总数
     * @param fileName  单次分片的文件名称
     */
    @PostMapping("/singleFilePartUpload")
    public Result<Boolean> singleFilePartUpload(@RequestParam("filePart") MultipartFile filePart,
                                                @RequestParam("partIndex") Integer partIndex,
                                                @RequestParam("partNum") Integer partNum,
                                                @RequestParam("fileName") String fileName,
                                                @RequestParam("fileUid") String fileUid) {
        log.info("单文件分片上传,总片数:{},分片数:{},文件名:{},大小:{},uid:{}", partNum, partIndex, fileName, filePart.getSize(), fileUid);
        Result<Boolean> result = fileUploadService.singleFilePartUpload(filePart, partIndex, partNum, fileName, fileUid);
        return result;
    }

    /**
     * 多文件分片上传
     *
     * @param filePart  单次分片的文件
     * @param partIndex 当前分片定位
     * @param partNum   文件分片总数
     * @param fileName  单次分片的文件名称
     */
    @PostMapping("/multipleFilePartUpload")
    public Result<String> multipleFilePartUpload(@RequestParam("filePart") MultipartFile filePart,
                                                 @RequestParam("partIndex") Integer partIndex,
                                                 @RequestParam("partNum") Integer partNum,
                                                 @RequestParam("fileName") String fileName,
                                                 @RequestParam("fileUid") String fileUid) {
        log.info("多文件分片上传,总片数:{},分片数:{},文件名:{},大小:{},uid:{}", partNum, partIndex, fileName, filePart.getSize(), fileUid);
        Result<String> result = fileUploadService.multipleFilePartUpload(filePart, partIndex, partNum, fileName, fileUid);
        return result;
    }

//    /**
//     * 多文件(分片)秒传
//     *
//     * @param filePart  单次分片的文件
//     * @param fileInfo  当前分片相关信息
//     * @param fileOther 所有不需要上传的文件信息，包括文件索引
//     */
//    @PostMapping("/multipleFilePartFlashUpload")
//    public Result<String> multipleFilePartFlashUpload(@RequestParam("filePart") MultipartFile filePart,
//                                                      @RequestParam("fileInfo") String fileInfo,
//                                                      @RequestParam("fileOther") String fileOther) {
//        log.info("多文件(分片)秒传,文件大小:{};文件信息:{};其他信息:{}", filePart.getSize(), fileInfo, fileOther);
//
//        Result<String> result = fileUploadService.multipleFilePartFlashUpload(filePart, fileInfo, fileOther);
//        return result;
//    }

    /**
     * 单文件(分片)断点上传
     *
     * @param filePart 单次分片的文件
     * @param fileInfo 当前相关信息
     */
    @PostMapping("/singleFilePartPointUpload")
    public Result<String> singleFilePartPointUpload(@RequestParam("filePart") MultipartFile filePart,
                                                    @RequestParam("fileInfo") String fileInfo) {
        Result<String> result = fileUploadService.singleFilePartPointUpload(filePart, fileInfo);
        return result;
    }

//    /**
//     * 检测秒传文件上传服务器中存在的文件，即不需要上传的文件
//     */
//    @PostMapping("/checkDiskFile")
//    public Result<List<DiskFileIndex>> checkDiskFile(@RequestBody List<DiskFileIndex> upLoadFileListMd5) {
//        log.info("检测服务器磁盘文件和需要上传的md5值");
//        Result<List<DiskFileIndex>> result = fileUploadService.checkDiskFile(upLoadFileListMd5);
//        return result;
//    }

    /**
     * 检测断点上传文件在务器中上传进度
     */
    @PostMapping("/checkUploadFileIndex")
    public Result<PointFileIndex> checkUploadFileIndex(@RequestBody PointFileIndex pointFileIndex) {
        log.info("检测服务器中文件上传进度，{}", pointFileIndex);
        Result<PointFileIndex> result = fileUploadService.checkUploadFileIndex(pointFileIndex);
        return result;
    }

    /**
     * 测试接口
     */
    @PostMapping("/fileTest")
    public Result<String> fileTest() {
        log.info("测试接口");
        return Result.success("测试通过");
    }
}
