package com.rengu.project.aluminum.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

/**
 * com.rengu.project.aluminum.entity
 *
 * @author hanchangming
 * @date 2019-06-10
 */

@Data
public class ResultEntity<T> {

    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private int code;
    private String message;
    private T data;

    public ResultEntity(ApplicationMessageEnum applicationMessageEnum, T data) {
        this.code = applicationMessageEnum.getCode();
        this.message = applicationMessageEnum.getMessage();
        this.data = data;
    }

    public ResultEntity(T data) {
        this.code = ApplicationMessageEnum.SUCCEED.getCode();
        this.message = ApplicationMessageEnum.SUCCEED.getMessage();
        this.data = data;
    }
}

