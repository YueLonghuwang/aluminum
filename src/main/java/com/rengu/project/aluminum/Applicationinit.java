package com.rengu.project.aluminum;

import com.rengu.project.aluminum.entity.RoleEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.enums.SecurityClassificationEnum;
import com.rengu.project.aluminum.service.RoleService;
import com.rengu.project.aluminum.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * com.rengu.project.aluminum
 *
 * @author hanchangming
 * @date 2019-06-10
 */

@Slf4j
@Component
public class Applicationinit implements ApplicationRunner {

    private final ApplicationConfig applicationConfig;
    private final RoleService roleService;
    private final UserService userService;

    public Applicationinit(ApplicationConfig applicationConfig, RoleService roleService, UserService userService) {
        this.applicationConfig = applicationConfig;
        this.roleService = roleService;
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        // 初始化角色
        initApplicationRole(applicationConfig.getDEFAULT_ADMIN_ROLE_NAME());
        initApplicationRole(applicationConfig.getDEFAULT_AUDIT_ROLE_NAME());
        initApplicationRole(applicationConfig.getDEFAULT_SECURITY_ROLE_NAME());
        initApplicationRole(applicationConfig.getDEFAULT_USER_ROLE_NAME());

        // 初始化用户
        initApplicationUser(applicationConfig.getDEFAULT_ADMIN_USER_USERNAME(), applicationConfig.getDEFAULT_ADMIN_USER_PASSWORD(), roleService.getRoleByName(applicationConfig.getDEFAULT_ADMIN_ROLE_NAME()));
        initApplicationUser(applicationConfig.getDEFAULT_AUDIT_USER_USERNAME(), applicationConfig.getDEFAULT_AUDIT_USER_PASSWORD(), roleService.getRoleByName(applicationConfig.getDEFAULT_AUDIT_ROLE_NAME()));
        initApplicationUser(applicationConfig.getDEFAULT_SECURITY_USER_USERNAME(), applicationConfig.getDEFAULT_SECURITY_USER_PASSWORD(), roleService.getRoleByName(applicationConfig.getDEFAULT_SECURITY_ROLE_NAME()));
    }

    // 初始化角色方法
    private void initApplicationRole(String name) {
        if (!roleService.hasRoleByName(name)) {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(name);
            roleEntity.setDescription(name);
            roleService.saveRole(roleEntity);
            log.info("已初始化角色：" + name);
        }
    }

    // 初始化用户方法
    private void initApplicationUser(String username, String password, RoleEntity... roleEntities) {
        if (!userService.hasUserByUsername(username)) {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setPassword(password);
            userEntity.setSecurityClassification(SecurityClassificationEnum.CONFIDENTIAL.getCode());
            userService.saveUser(userEntity, roleEntities);
            log.info("已初始化用户：" + username);
        }
    }
}