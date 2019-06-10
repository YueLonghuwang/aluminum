package com.rengu.project.aluminum;

import com.rengu.project.aluminum.entity.RoleEntity;
import com.rengu.project.aluminum.service.RoleService;
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

    public Applicationinit(ApplicationConfig applicationConfig, RoleService roleService) {
        this.applicationConfig = applicationConfig;
        this.roleService = roleService;
    }

    @Override
    public void run(ApplicationArguments args) {
        // 初始化角色
        if (!roleService.hasRoleByName(applicationConfig.getDEFAULT_ADMIN_ROLE_NAME())) {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(applicationConfig.getDEFAULT_ADMIN_ROLE_NAME());
            roleEntity.setDescription(applicationConfig.getDEFAULT_ADMIN_ROLE_NAME());
            roleService.saveRole(roleEntity);
            log.info("已初始化角色：" + applicationConfig.getDEFAULT_ADMIN_ROLE_NAME());
        }
        if (!roleService.hasRoleByName(applicationConfig.getDEFAULT_AUDIT_ROLE_NAME())) {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(applicationConfig.getDEFAULT_AUDIT_ROLE_NAME());
            roleEntity.setDescription(applicationConfig.getDEFAULT_AUDIT_ROLE_NAME());
            roleService.saveRole(roleEntity);
            log.info("已初始化角色：" + applicationConfig.getDEFAULT_AUDIT_ROLE_NAME());
        }
        if (!roleService.hasRoleByName(applicationConfig.getDEFAULT_SECURITY_ROLE_NAME())) {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(applicationConfig.getDEFAULT_SECURITY_ROLE_NAME());
            roleEntity.setDescription(applicationConfig.getDEFAULT_SECURITY_ROLE_NAME());
            roleService.saveRole(roleEntity);
            log.info("已初始化角色：" + applicationConfig.getDEFAULT_SECURITY_ROLE_NAME());
        }
        if (!roleService.hasRoleByName(applicationConfig.getDEFAULT_USER_ROLE_NAME())) {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(applicationConfig.getDEFAULT_USER_ROLE_NAME());
            roleEntity.setDescription(applicationConfig.getDEFAULT_USER_ROLE_NAME());
            roleService.saveRole(roleEntity);
            log.info("已初始化角色：" + applicationConfig.getDEFAULT_USER_ROLE_NAME());
        }
    }
}