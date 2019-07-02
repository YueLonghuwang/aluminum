package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.DepartmentEntity;
import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.exception.DepartmentException;
import com.rengu.project.aluminum.repository.DepartmentRepository;
import com.rengu.project.aluminum.repository.UserRepository;
import com.rengu.project.aluminum.service.DepartmentService;
import com.rengu.project.aluminum.service.UserService;
import com.rengu.project.aluminum.specification.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.rengu.project.aluminum.specification.SpecificationBuilder.selectFrom;

/**
 * com.rengu.project.aluminum.controller
 *
 * @author hanchangming
 * @date 2019-06-11
 */

@Slf4j
@RestController
@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN')")
@RequestMapping(path = "/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentService departmentService, UserService userService, UserRepository userRepository, DepartmentRepository departmentRepository) {
        this.departmentService = departmentService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }


    /*  // 根据关键字查询
      @PostMapping("/KeyWord")
      public ResultEntity findByKeyWord(@RequestBody Filter filter) {
          return new ResultEntity(selectFrom(modelResourceRepository).where(filter).findAll());
      }
  */
    // 根据部门名字和用户名模糊查询用户信息
    @PostMapping("/keyWord")
    public ResultEntity getUserByDepartmentAndUserName(@RequestBody Filter filter) {
        return new ResultEntity(selectFrom(userRepository).where(filter).findAll());
    }

    @PostMapping("/departmentName/keyWord")
    public ResultEntity getDepartmentNameAndUserName(@RequestBody Filter filter) {
        List<DepartmentEntity> departmentEntityList = selectFrom(departmentRepository).where(filter).findAll();
        List<UserEntity> userEntityList = new ArrayList<>();
        for (DepartmentEntity departmentEntity : departmentEntityList) {
            userEntityList.addAll(userService.getUsersByDepartment(departmentEntity));
        }
        return new ResultEntity(userEntityList);
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

    // 根据id删除成员
    @PatchMapping(value = "/{departmentId}/remove/users")
    public ResultEntity<Map> departmentRemoveUsersById(@PathVariable(name = "departmentId") String departmentId, @RequestParam(value = "userId") String userId) {
        return new ResultEntity<>(userService.removeUserByDepartment(userId, departmentId));
    }

    //  按部门ID查询用户
    @GetMapping(value = "/{departmentId}/users")
    public ResultEntity<Page<UserEntity>> getDepartmentById(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable(name = "departmentId") String departmentId) {
        return new ResultEntity<>(userService.getUsersByDepartment(pageable, departmentService.getDepartmentById(departmentId)));
    }

    // 根据部门id查询部门
    @GetMapping(value = "/{departmentId}")
    public ResultEntity<DepartmentEntity> getDepartmentById(@PathVariable(name = "departmentId") String departmentId) {
        return new ResultEntity<>(departmentService.getDepartmentById(departmentId));
    }

    // 查询所有部门
    @GetMapping
    public ResultEntity<Page<DepartmentEntity>> getDepartments(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResultEntity<>(departmentService.getDepartments(pageable));
    }

    // 根据用户Id修改该用户的所属部门
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @PatchMapping("/{userId}/updateUserForDepartment")
    public ResultEntity<UserEntity> updateUserForDepartmentByAudit(@PathVariable(name = "userId") String userId, String departmentId) {
        return new ResultEntity<>(userService.updateDepartmentById(userId, departmentId));
    }

}
