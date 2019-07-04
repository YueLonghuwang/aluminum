package com.rengu.project.aluminum.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @Author: yaojiahao
 * @Date: 2019/7/4 0:20
 */
@Entity
@Data
public class ModelResourceHistory extends ResourceEntity {
    @ManyToOne
    private ModelResourceEntity modelResourceEntity;
}
