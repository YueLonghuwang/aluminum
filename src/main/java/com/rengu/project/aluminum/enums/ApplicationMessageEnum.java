package com.rengu.project.aluminum.enums;

/**
 * com.rengu.project.aluminum.enums
 *
 * @author hanchangming
 * @date 2019-06-10
 */

public enum ApplicationMessageEnum {

    SYSTEM_ERROR(0, "系统异常错误"),
    DEFAULT_USER_DELETE_ERROR(1, "禁止删除内置用户"),
    SUCCEED(100, "请求成功"),
    // 角色接口提示信息
    ROLE_NAME_NOT_FOUND(200, "角色名称不存在或不合法"),
    ROLE_NAME_EXISTS(201, "该角色名称已存在"),
    ROLE_NAME_NOT_EXISTS(202, "该名称的角色不存在"),
    // 用户接口提示信息
    USER_USERNAME_NOT_FOUND(300, "用户名称不存在或不合法"),
    USER_USERNAME_EXISTS(301, "该用户名称已存在"),
    USER_USERNAME_NOT_EXISTS(302, "该用户名称的用户不存在"),
    USER_PASSWORD_NOT_FOUND(303, "用户密码不存在或不合法"),
    USER_ID_NOT_FOUND(304, "用户ID参数不存在或不合法"),
    USER_ID_NOT_EXISTS(305, "该ID的用户不存在"),
    // 密级提示信息
    SECURITY_CLASSIFICATION_NOT_EXISTS(400, "该代码的密级不存在"),
    // 文件块提示信息
    CHUNK_NOT_EXISTS(500, "该文件的文件块缺失或不正确"),
    // 文件提示信息
    FILE_MD5_NOT_FOUND(600, "文件MD5不存在或不合法"),
    FILE_MD5_EXISTS(601, "该文件MD5已存在"),
    FILE_MD5_NOT_EXISTS(602, "该文件MD5的文件不存在"),
    // 部门提示信息
    DEPARTMENT_NAME_NOT_FOUND(700, "部门名称不存在或不合法"),
    DEPARTMENT_NAME_EXISTS(701, "该部门名称已存在"),
    DEPARTMENT_NAME_NOT_EXISTS(702, "该名称的部门不存在"),
    DEPARTMENT_ID_NOT_FOUND(703, "部门ID不存在或不合法"),
    DEPARTMENT_ID_EXISTS(704, "该部门ID已存在"),
    DEPARTMENT_ID_NOT_EXISTS(705, "该ID的部门不存在"),
    DEPARTMENT_MEMBERS_NOT_EMPTY(706, "该部门还存在成员");

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
