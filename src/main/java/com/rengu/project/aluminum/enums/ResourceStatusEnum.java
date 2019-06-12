package com.rengu.project.aluminum.enums;

/**
 * com.rengu.project.aluminum.enums
 *
 * @author hanchangming
 * @date 2019-06-11
 */
public enum ResourceStatusEnum {

    PASSED(0, "入库"),
    REVIEWING(1, "审核中"),
    REFUSED(2, "驳回");

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
