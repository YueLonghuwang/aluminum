package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.ApplicationConfig;
import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.service.RoleService;
import com.rengu.project.aluminum.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * com.rengu.project.aluminum.controller
 *
 * @author hanchangming
 * @date 2019-06-10
 */

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final ApplicationConfig applicationConfig;
    private final UserService userService;
    private final RoleService roleService;

    public UserController(ApplicationConfig applicationConfig, UserService userService, RoleService roleService) {
        this.applicationConfig = applicationConfig;
        this.userService = userService;
        this.roleService = roleService;
    }

    // 保存用户
    @PostMapping
    public ResultEntity<UserEntity> saveUser(UserEntity userEntity) {
        return new ResultEntity<>(userService.saveUser(userEntity, roleService.getRoleByName(applicationConfig.getDEFAULT_USER_ROLE_NAME())));
    }

    // 根据Id删除用户
    @DeleteMapping(value = "/{userId}")
    public ResultEntity<UserEntity> deleteUserById(@PathVariable(name = "userId") String userId) {
        return new ResultEntity<>(userService.deleteUserById(userId));
    }


    // 根据id修改用户密码
    @PatchMapping(value = "/{userId}/password")
    public ResultEntity<UserEntity> updatePasswordById(@PathVariable(name = "userId") String userId, @RequestParam(name = "password") String password) {
        return new ResultEntity<>(userService.updatePasswordById(userId, password));
    }

    // 根据id修改用户密级
    @PreAuthorize(value = "hasRole(#applicationConfig.DEFAULT_SECURITY_ROLE_NAME)")
    @PatchMapping(value = "/{userId}/security-classification")
    public ResultEntity<UserEntity> updateSecurityClassificationById(@PathVariable(name = "userId") String userId, @RequestParam(name = "securityClassification") int securityClassification) {
        return new ResultEntity<>(userService.updateSecurityClassificationById(userId, securityClassification));
    }

    // 根据Id查询用户信息
    @GetMapping(value = "/{userId}")
    public ResultEntity<UserEntity> getUserById(@PathVariable(value = "userId") String userId) {
        return new ResultEntity<>(userService.getUserById(userId));
    }

    // 分页查询全部用户
    @PreAuthorize(value = "hasRole(#applicationConfig.DEFAULT_ADMIN_ROLE_NAME)")
    @GetMapping
    public ResultEntity<Page<UserEntity>> getUsers(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResultEntity<>(userService.getUsers(pageable));
    }
}