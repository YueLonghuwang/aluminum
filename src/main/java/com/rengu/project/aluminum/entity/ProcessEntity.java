package com.rengu.project.aluminum.entity;

import lombok.Data;

import javax.persistence.OneToOne;
import java.io.Serializable;

/**
 * Author: XYmar
 * Date: 2019/6/28 11:26
 */
@Data
public class ProcessEntity implements Serializable {
    private String id;             // 流程id
    private ResourceEntity resourceEntity;
    private int resourceType;
    private UserEntity userEntity;  //流程提交
}
