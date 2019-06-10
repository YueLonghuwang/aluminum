package com.rengu.project.aluminum.enums;

/**
 * com.rengu.project.aluminum.enums
 *
 * @author hanchangming
 * @date 2019-06-10
 */

public enum ApplicationMessageEnum {

    SYSTEM_ERROR(0, "系统异常错误"),
    SUCCEED(100, "请求成功"),
    // 角色接口提示信息
    ROLE_NAME_NOT_FOUND(200, "角色名称参数不存在或不合法"),
    ROLE_NAME_EXISTS(201, "该角色名称已存在"),
    ROLE_NAME_NOT_EXISTS(202, "该名称的角色不存在");

    private int code;
    private String message;

    ApplicationMessageEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
