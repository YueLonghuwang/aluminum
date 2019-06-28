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
    COMPRESS_FILE_TYPE_ERROR(2, "文件压缩失败"),
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
    SECURITY_CLASSIFICATION_NOT_ENOUGH(401, "用户密级不足"),
    // 文件块提示信息
    CHUNK_NOT_EXISTS(500, "该文件的文件块缺失或不正确"),
    // 文件提示信息
    FILE_MD5_NOT_FOUND(600, "文件MD5不存在或不合法"),
    FILE_MD5_EXISTS(601, "该文件MD5已存在"),
    FILE_MD5_NOT_EXISTS(602, "该文件MD5的文件不存在"),
    FILE_ID_NOT_FOUND(600, "文件ID不存在或不合法"),
    FILE_ID_EXISTS(601, "该文件ID已存在"),
    FILE_ID_NOT_EXISTS(602, "该文件ID的文件不存在"),
    // 部门提示信息
    DEPARTMENT_NAME_NOT_FOUND(700, "部门名称不存在或不合法"),
    DEPARTMENT_NAME_EXISTS(701, "该部门名称已存在"),
    DEPARTMENT_NAME_NOT_EXISTS(702, "该名称的部门不存在"),
    DEPARTMENT_ID_NOT_FOUND(703, "部门ID不存在或不合法"),
    DEPARTMENT_ID_EXISTS(704, "该部门ID已存在"),
    DEPARTMENT_ID_NOT_EXISTS(705, "该ID的部门不存在"),
    DEPARTMENT_MEMBERS_NOT_EMPTY(706, "该部门还存在成员"),
    // 抽象资源提示信息
    RESOURCE_ID_NOT_FOUND(700, "资源ID不存在或不合法"),
    RESOURCE_ID_EXISTS(701, "该资源ID已存在"),
    RESOURCE_ID_NOT_EXISTS(702, "该ID的资源不存在"),
    RESOURCE_NAME_NOT_FOUND(700, "资源名称不存在或不合法"),
    RESOURCE_NAME_EXISTS(701, "该资源名称已存在"),
    RESOURCE_NAME_NOT_EXISTS(702, "该名称的资源不存在"),
    RESOURCE_AUTHOR_NOT_FOUND(700, "资源作者不存在或不合法"),
    RESOURCE_AUTHOR_EXISTS(701, "该资源作者已存在"),
    RESOURCE_AUTHOR_NOT_EXISTS(702, "该作者的资源不存在"),
    RESOURCE_UNIT_NOT_FOUND(700, "资源单位不存在或不合法"),
    RESOURCE_UNIT_EXISTS(701, "该资源单位已存在"),
    RESOURCE_UNIT_NOT_EXISTS(702, "该单位的资源不存在"),
    RESOURCE_VERSION_NOT_FOUND(700, "资源版本号不存在或不合法"),
    RESOURCE_VERSION_EXISTS(701, "该资源版本号已存在"),
    RESOURCE_VERSION_NOT_EXISTS(702, "该版本号的资源不存在"),
    RESOURCE_NAME_AND_VERSION_EXISTS(702, "该名称及版本号的的资源已存在"),
    // 抽象资源文件提示信息
    RESOURCE_FILE_ID_NOT_FOUND(800, "资源文件ID不存在或不合法"),
    RESOURCE_FILE_ID_EXISTS(801, "该资源文件ID已存在"),
    RESOURCE_FILE_ID_NOT_EXISTS(802, "该ID的资源文件不存在"),
    RESOURCE_FILE_NAME_NOT_FOUND(800, "资源文件名称不存在或不合法"),
    RESOURCE_FILE_NAME_EXISTS(801, "该资源文件名称已存在"),
    RESOURCE_FILE_NAME_NOT_EXISTS(802, "该名称的资源文件不存在"),
    RESOURCE_FILE_RESOURCE_ID_NOT_FOUND(800, "资源ID不存在或不合法"),
    RESOURCE_FILE_RESOURCE_ID_EXISTS(801, "该资源ID已存在"),
    RESOURCE_FILE_RESOURCE_ID_NOT_EXISTS(802, "该资源ID的资源文件不存在"),
    RESOURCE_FILE_NOT_EXISTS(803, "该资源文件不存在"),
    RESOURCE_FILE_EXISTS(803, "该资源文件已存在"),
    MESSAGE_ID_NOT_FOUND_ERROR(50015, "未发现该消息ID"),
    // 权限验证
    ERROR_PERMISSION_DENIED(901, "权限不足"),
    // 资源类型
    RESOURCE_TYPE_NOT_FOUND(1001, "该资源类型不存在"),
    PROCESSID_NOT_FOUND(1002, "流程不存在");
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
