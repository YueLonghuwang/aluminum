package com.rengu.project.aluminum.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Author: XYmar
 * Date: 2019/6/28 11:42
 */
@Data
public class TaskEntity implements Serializable {
    private String id;              // 任务id
    private String name;            // 任务名称
    private String processId;       // 任务所属流程id
    private String taskAssignee;     // 任务操作人
    private ResourceEntity resourceEntity;
}
