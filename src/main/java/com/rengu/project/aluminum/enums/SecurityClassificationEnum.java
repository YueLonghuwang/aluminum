package com.rengu.project.aluminum.enums;

import com.rengu.project.aluminum.exception.SecurityClassificationException;

/**
 * com.rengu.project.aluminum.enums
 *
 * @author hanchangming
 * @date 2019-06-10
 */
public enum SecurityClassificationEnum {

    PUBLIC(0, "公开"),
    SECRET(1, "秘密"),
    CONFIDENTIAL(2, "机密");

    private int code;
    private String name;

    SecurityClassificationEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static SecurityClassificationEnum getEnum(int code) {
        for (SecurityClassificationEnum securityClassificationEnum : SecurityClassificationEnum.values()) {
            if (securityClassificationEnum.getCode() == code) {
                return securityClassificationEnum;
            }
        }
        throw new SecurityClassificationException(ApplicationMessageEnum.SECURITY_CLASSIFICATION_NOT_EXISTS);
    }
}