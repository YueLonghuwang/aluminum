package com.rengu.project.aluminum.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * com.rengu.project.aluminum.entity
 *
 * @author hanchangming
 * @date 2019-06-11
 */

@Data
public class FileMetaEntity implements Serializable {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private String fileId;
    private String name;
    private String relativePath;
}
