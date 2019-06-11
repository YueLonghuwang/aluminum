package com.rengu.project.aluminum.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * com.rengu.project.aluminum.entity
 *
 * @author hanchangming
 * @date 2019-06-10
 */

@Data
@Entity
public class FileEntity implements Serializable {

    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private String mD5;
    private String type;
    private long size;
    private String localPath;
}