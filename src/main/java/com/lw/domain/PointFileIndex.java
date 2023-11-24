package com.lw.domain;

import lombok.Data;

import java.util.List;

/**
 * @author lw
 * @data 2023/9/27
 * @周三
 */
@Data
public class PointFileIndex {

    private String fileName;
    private String fileMd5;
    private String fileSize;

    private String fileUid;
    private Integer partIndex;       //当前分片值
    private Integer partNum;         //分片数
    private List<String> parts;      //分片集合
}
