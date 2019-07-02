package com.rengu.project.aluminum;

import com.rengu.project.aluminum.util.FormatUtils;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * com.rengu.project.aluminum
 *
 * @author hanchangming
 * @date 2019-06-10
 */

@Data
@Component
@ConfigurationProperties(prefix = "config")
public class ApplicationConfig {


    // 默认角色名称
    private String DEFAULT_ADMIN_ROLE_NAME;
    private String DEFAULT_AUDIT_ROLE_NAME;
    private String DEFAULT_SECURITY_ROLE_NAME;
    private String DEFAULT_USER_ROLE_NAME;

    // 默认用户
    private String DEFAULT_ADMIN_USER_USERNAME;
    private String DEFAULT_ADMIN_USER_PASSWORD;
    private String DEFAULT_SECURITY_USER_USERNAME;
    private String DEFAULT_SECURITY_USER_PASSWORD;
    private String DEFAULT_AUDIT_USER_USERNAME;
    private String DEFAULT_AUDIT_USER_PASSWORD;

    // 默认部门
    private String DEFAULT_PROOF_NAME;
    private String DEFAULT_AUDIT_NAME;
    private String DEFAULT_COUNT_NAME;
    private String DEFAULT_APPROVE_NAME;

    // OAuth2配置
    private String OAUTH_CLIENT_ID;
    private String OAUTH_CLIENT_SECRET;
    private String OAUTH_CLIENT_SCOPES;
    private String OAUTH_JWT_SIGNINGKEY;
    // 入库出库状态
    public static final int BE_PUT_IN_STORAGE = 0; // 入库
    public static final int PUT_IN_STORAGE = 0; // 出库
    // 通过所有审核
    public static final int PASS_ALL_AUDIT = 4;
    // resource类型
    public static final int MODEL_RESOURCE = 0;                          // 模型资源
    public static final int STANDARD_RESOURCE = 1;                       // 标准规范
    public static final int ALGORITHM_RESOURCE = 2;                      // 算法服务
    public static final int TOOLS_RESOURCE = 3;                          // 工具软件

    // 通知的基本操作
    public static final int ARRANGE_NONE_OPERATE = 0;                        // 无操作
    //    public static final int ARRANGE_ROLE_OPERATE = 1;                     // 赋予新角色
    public static final int DELETE_OPERATE = 2;                            // 删除
    public static final int RESTORE_OPERATE = 3;                          // 恢复
    public static final int MODIFY_OPERATE = 4;                          // 修改
    public static final int ADD_OPERATE = 1;                            //新增
    public static final int DOWNLOAD_OPERATE = 5;                      //下载
    // 通知操作的主体
    public static final int MAINBODY_NONE = 0;                       // 无操作主体
    public static final int MAINBODY_USERS = 1;                     // 用户
    public static final int MAINBODY_MODEL = 2;                    // 模型资源管理
    public static final int MAINBODY_STANDARD = 3;                // 标准规范管理
    public static final int MAINBODY_ALGORITHM_SERVER = 4;       // 公共算法服务管理
    public static final int MAINBODY_TOOLS_SOFTWARE = 5;        // 工具软件管理
    public static final int MAINBODY_DEPARTMENT = 6;           // 部门管理

    // 本地文件库配置
    private String CHUNKS_SAVE_PATH = FormatUtils.formatPath(FileUtils.getTempDirectoryPath() + File.separator + "ALUMINUM" + File.separator + "CHUNKS");
    private String FILES_SAVE_PATH = FormatUtils.formatPath(FileUtils.getUserDirectoryPath() + File.separator + "ALUMINUM" + File.separator + "FILES");
}