package com.rengu.project.aluminum.enums;

/**
 * com.rengu.project.aluminum.enums
 *
 * @author hanchangming
 * @date 2019-06-11
 */
public enum ResourceStatusEnum {

    STATELESS(0,"无状态"),
    REVIEWING(1, "入库审核中"),
    PASSED(2, "入库"),
    REFUSED(3, "驳回"),
    PUTINSTORAGEREVIEWING(4, "出库审核中"),
    PUTINSTORAGE(5, "出库");

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
