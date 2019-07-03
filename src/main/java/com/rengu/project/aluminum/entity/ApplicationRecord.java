package com.rengu.project.aluminum.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * author : yaojiahao
 * Date: 2019/7/1 13:56
 **/

@Data
@Entity
public class ApplicationRecord implements Serializable {
    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    @ManyToOne
    private UserEntity users;
    @ManyToOne
    private ModelResourceEntity modelResource;
    @ManyToOne
    private StandardEntity standard;
    @ManyToOne
    private AlgorithmAndServerEntity algorithmServer;
    @ManyToOne
    private ToolsAndSoftwareEntity toolsSoftware;
    private int applicationStatus; // 0:入库 1:出库
    private int approvalStatus;   // 审批状态
    private int currentStatus;  // 当前状态
    private int resourceType; // 资源类型
    private int securityClassification; // 资源密级
    private String explainInfo; // 入库说明
}
