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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    // 管理员创建用户
    @PostMapping("/saveByAdmin")
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    public ResultEntity<UserEntity> saveUserByAdmin(UserEntity userEntity) {
        return new ResultEntity<>(userService.saveUserByAdmin(userEntity));
    }

    // 管理员修改密码
    @PostMapping("/{userId}/updatePwdByAdmin")
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    public ResultEntity<UserEntity> updatePwdByAdmin(@PathVariable(name = "userId") String userId, @RequestParam(name = "password") String password) {
        return new ResultEntity<>(userService.updatePasswordById(userId, password));
    }

    // 管理员修改部门
    @PostMapping("/{userId}/updateUserByAdmin")
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    public ResultEntity<UserEntity> updateUserByAdmin(@PathVariable(name = "userId") String userId, String departmentName) {
        return new ResultEntity<>(userService.modifyRoleByAdmin(userId, departmentName));
    }

    // 保存用户
    @PostMapping
    public ResultEntity<UserEntity> saveUser(UserEntity userEntity) {
        return new ResultEntity<>(userService.saveUser(userEntity, roleService.getRoleByName(applicationConfig.getDEFAULT_USER_ROLE_NAME())));
    }

    // 根据Id删除用户
    @DeleteMapping(value = "/{userId}")
    @PreAuthorize(value = "hasAnyRole('ROLE_ADMIN')")
    public ResultEntity<UserEntity> deleteUserById(@PathVariable(name = "userId") String userId) {
        return new ResultEntity<>(userService.deleteUserById(userId));
    }


    // 根据id修改用户密码
    @PatchMapping(value = "/{userId}/password")
    public ResultEntity<UserEntity> updatePasswordById(@PathVariable(name = "userId") String userId, @RequestParam(name = "password") String password) {
        return new ResultEntity<>(userService.updatePasswordById(userId, password));
    }

    // 根据id修改用户密级
    @PreAuthorize(value = "hasAnyRole('ROLE_SECURITY')")
    @PatchMapping(value = "/{userId}/security-classification")
    public ResultEntity<UserEntity> updateSecurityClassificationById(@PathVariable(name = "userId") String userId, @RequestParam(name = "securityClassification") int securityClassification) {
        return new ResultEntity<>(userService.updateSecurityClassificationById(userId, securityClassification));
    }

    // 根据id修改多用户密级
    @PreAuthorize(value = "hasAnyRole('ROLE_SECURITY')")
    @PatchMapping(value = "/security-classification")
    public ResultEntity updateSecurityClassificationByIds(@RequestBody String[] userIds, @RequestParam(name = "securityClassification") int securityClassification) {
        return new ResultEntity<>(userService.updateSecurityClassificationByIds(userIds, securityClassification));
    }

    // 根据Id查询用户信息
    @GetMapping(value = "/{userId}")
    public ResultEntity<UserEntity> getUserById(@PathVariable(value = "userId") String userId) {
        return new ResultEntity<>(userService.getUserById(userId));
    }

    // 分页查询全部用户
    @GetMapping
    @PreAuthorize(value = "hasAnyRole('ROLE_ADMIN','ROLE_SECURITY')")
    public ResultEntity<Page<UserEntity>> getUsers(@AuthenticationPrincipal String username, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResultEntity<>(userService.getUsers(username, pageable));
    }
}