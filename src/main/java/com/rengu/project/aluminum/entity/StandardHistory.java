package com.rengu.project.aluminum.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @Author: yaojiahao
 * @Date: 2019/7/4 0:21
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class StandardHistory extends ResourceEntity {
    @ManyToOne
    private StandardEntity standardEntity;
}
