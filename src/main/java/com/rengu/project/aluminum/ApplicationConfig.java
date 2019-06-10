package com.rengu.project.aluminum;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
}