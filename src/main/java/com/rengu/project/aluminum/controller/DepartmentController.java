package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.DepartmentEntity;
import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.exception.DepartmentException;
import com.rengu.project.aluminum.service.DepartmentService;
import com.rengu.project.aluminum.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * com.rengu.project.aluminum.controller
 *
 * @author hanchangming
 * @date 2019-06-11
 */

@Slf4j
@RestController
@PreAuthorize(value = "hasRole('ADMIN')")
@RequestMapping(path = "/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final UserService userService;

    public DepartmentController(DepartmentService departmentService, UserService userService) {
        this.departmentService = departmentService;
        this.userService = userService;
    }

    // 保存部门
    @PostMapping
    public ResultEntity<DepartmentEntity> saveDepartment(DepartmentEntity departmentEntity) {
        return new ResultEntity<>(departmentService.saveDepartment(departmentEntity));
    }

    // 根据id删除部门
    @DeleteMapping(value = "/{departmentId}")
    public ResultEntity<DepartmentEntity> deleteDepartmentById(@PathVariable(name = "departmentId") String departmentId) {
        if (!userService.getUsersByDepartment(departmentService.getDepartmentById(departmentId)).isEmpty()) {
            throw new DepartmentException(ApplicationMessageEnum.DEPARTMENT_MEMBERS_NOT_EMPTY);
        }
        return new ResultEntity<>(departmentService.deleteDepartmentById(departmentId));
    }

    // 根据id修改部门属性
    @PatchMapping(value = "/{departmentId}")
    public ResultEntity<DepartmentEntity> updateDepartmentById(@PathVariable(name = "departmentId") String departmentId, DepartmentEntity departmentEntity) {
        return new ResultEntity<>(departmentService.updateDepartmentById(departmentId, departmentEntity));
    }

    // 根据id添加成员
    @PatchMapping(value = "/{departmentId}/add/users")
    public ResultEntity<Set<UserEntity>> departmentAddUsersById(@PathVariable(name = "departmentId") String departmentId, @RequestParam(value = "userIds") String[] userIds) {
        return new ResultEntity<>(userService.updateDepartmentByIds(userIds, departmentService.getDepartmentById(departmentId)));
    }

    // 根据id添加成员
    @PatchMapping(value = "/{departmentId}/remove/users")
    public ResultEntity<Set<UserEntity>> departmentRemoveUsersById(@PathVariable(name = "departmentId") String departmentId, @RequestParam(value = "userIds") String[] userIds) {
        return new ResultEntity<>(userService.updateDepartmentByIds(userIds, null));
    }

    // 根据id查询部门
    @GetMapping(value = "/{departmentId}/users")
    public ResultEntity<Page<UserEntity>> getDepartmentById(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable(name = "departmentId") String departmentId) {
        return new ResultEntity<>(userService.getUsersByDepartment(pageable, departmentService.getDepartmentById(departmentId)));
    }

    // 根据id查询部门
    @GetMapping(value = "/{departmentId}")
    public ResultEntity<DepartmentEntity> getDepartmentById(@PathVariable(name = "departmentId") String departmentId) {
        return new ResultEntity<>(departmentService.getDepartmentById(departmentId));
    }

    // 查询所有部门
    @GetMapping
    public ResultEntity<Page<DepartmentEntity>> getDepartments(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResultEntity<>(departmentService.getDepartments(pageable));
    }

}
