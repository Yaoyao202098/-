package com.lw.domain;

import lombok.Data;

/**
 * @author lw
 * @data 2023/9/28
 * @周四
 */
@Data
public class PartFileIndex {

    private String fileName;            // 文件名称
    private String fileMd5;            //文件md5值
    private String fileIndex;          //文件服务器索引
    private String fileUid;              //需要上下传文件uid
    private Integer partIndex;          //当前分片
    private Integer partTotalNum;      //总分片
}
