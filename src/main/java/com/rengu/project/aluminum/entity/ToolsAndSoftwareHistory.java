package com.rengu.project.aluminum.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @Author: yaojiahao
 * @Date: 2019/7/4 0:22
 */
@Entity
@Data
public class ToolsAndSoftwareHistory extends ResourceEntity {
    @ManyToOne
    private ToolsAndSoftwareEntity toolsAndSoftwareEntity;
}
