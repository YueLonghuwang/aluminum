package com.rengu.project.aluminum.enums;

/**
 * com.rengu.project.aluminum.enums
 *
 * @author hanchangming
 * @date 2019-06-11
 */
public enum ResourceStatusEnum {

    REVIEWING(1, "审核中"),
    PASSED(2, "通过"),
    REFUSED(3, "驳回");

    private int code;
    private String name;

    ResourceStatusEnum(int code, String name) {
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
