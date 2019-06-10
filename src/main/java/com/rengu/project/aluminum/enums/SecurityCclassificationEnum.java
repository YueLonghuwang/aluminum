package com.rengu.project.aluminum.enums;

/**
 * com.rengu.project.aluminum.enums
 *
 * @author hanchangming
 * @date 2019-06-10
 */
public enum SecurityCclassificationEnum {

    PUBLIC(0, "公开"),
    SECRET(1, "秘密"),
    CONFIDENTIAL(2, "机密");

    private int code;
    private String name;

    SecurityCclassificationEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}