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

    // OAuth2配置
    private String OAUTH_CLIENT_ID;
    private String OAUTH_CLIENT_SECRET;
    private String OAUTH_CLIENT_SCOPES;
    private String OAUTH_JWT_SIGNINGKEY;

    // 本地文件库配置
    private String CHUNKS_SAVE_PATH = FormatUtils.formatPath(FileUtils.getTempDirectoryPath() + File.separator + "ALUMINUM" + File.separator + "CHUNKS");
    private String FILES_SAVE_PATH = FormatUtils.formatPath(FileUtils.getUserDirectoryPath() + File.separator + "ALUMINUM" + File.separator + "FILES");
}