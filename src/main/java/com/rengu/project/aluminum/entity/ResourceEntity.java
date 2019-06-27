package com.rengu.project.aluminum.entity;

import com.rengu.project.aluminum.enums.SecurityClassificationEnum;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.UUID;

/**
 * com.rengu.project.aluminum.entity
 *
 * @author hanchangming
 * @date 2019-06-11
 */

@Data
@MappedSuperclass
public abstract class ResourceEntity implements Serializable {

    @Id
    private String id = UUID.randomUUID().toString();
    private String createTime;
    private String name;
    private String author;
    private String unit;
    private String version;
    private String description;
    private String tag;
    private int securityClassification = SecurityClassificationEnum.PUBLIC.getCode();
    private int status;
    @ManyToOne
    private UserEntity createUser;
    @ManyToOne
    private UserEntity modifyUser;
}
