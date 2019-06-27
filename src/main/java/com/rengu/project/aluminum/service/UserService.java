package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.ApplicationConfig;
import com.rengu.project.aluminum.entity.DepartmentEntity;
import com.rengu.project.aluminum.entity.RoleEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.enums.SecurityClassificationEnum;
import com.rengu.project.aluminum.exception.DepartmentException;
import com.rengu.project.aluminum.exception.RoleException;
import com.rengu.project.aluminum.exception.UserException;
import com.rengu.project.aluminum.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * com.rengu.project.aluminum.service
 *
 * @author hanchangming
 * @date 2019-06-10
 */

@Slf4j
@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ApplicationConfig applicationConfig;
    private final RoleService roleService;
    private final DepartmentService departmentService;

    public UserService(UserRepository userRepository, ApplicationConfig applicationConfig, RoleService roleService, DepartmentService departmentService) {
        this.userRepository = userRepository;
        this.applicationConfig = applicationConfig;
        this.roleService = roleService;
        this.departmentService = departmentService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);
        if (!userEntityOptional.isPresent()) {
            throw new UsernameNotFoundException(username + "不存在或不合法");
        }
        return userEntityOptional.get();
    }

    // 管理员新建用户
    @CachePut(value = "user_cache", key = "#userEntity.getId()")
    public UserEntity saveUserByAdmin(UserEntity userEntity, String departmentName, String... roleEntitys) {
        verificationInfo(userEntity, departmentName);
        RoleEntity roleEntity = null;
        for (String roleNames : roleEntitys) {
            if (!roleService.hasRoleByName(roleNames)) {
                throw new RoleException(ApplicationMessageEnum.ROLE_NAME_NOT_FOUND);
            } else {
                roleEntity = roleService.getRoleByName(roleNames);
            }

        }
        userEntity.setDepartment(departmentService.getDepartmentByName(departmentName));
        userEntity.setPassword(new BCryptPasswordEncoder().encode(userEntity.getPassword()));
        userEntity.setRoleEntities(new HashSet<>(Arrays.asList(roleEntity)));
        return userRepository.save(userEntity);
    }

    // 保存用户
    @CachePut(value = "user_cache", key = "#userEntity.getId()")
    public UserEntity saveUser(UserEntity userEntity, RoleEntity... roleEntities) {
        if (StringUtils.isEmpty(userEntity.getUsername())) {
            throw new UserException(ApplicationMessageEnum.USER_USERNAME_NOT_FOUND);
        }
        if (hasUserByUsername(userEntity.getUsername())) {
            throw new UserException(ApplicationMessageEnum.USER_USERNAME_EXISTS);
        }
        if (StringUtils.isEmpty(userEntity.getPassword())) {
            throw new UserException(ApplicationMessageEnum.USER_PASSWORD_NOT_FOUND);
        }
        userEntity.setPassword(new BCryptPasswordEncoder().encode(userEntity.getPassword()));
        userEntity.setRoleEntities(new HashSet<>(Arrays.asList(roleEntities)));
        return userRepository.save(userEntity);
    }

    // 根据Id删除用户
    @CacheEvict(value = "user_cache", key = "#userId")
    public UserEntity deleteUserById(String userId) {
        UserEntity userEntity = getUserById(userId);
        if (userEntity.getUsername().equals(applicationConfig.getDEFAULT_ADMIN_USER_USERNAME()) || userEntity.getUsername().equals(applicationConfig.getDEFAULT_ADMIN_USER_USERNAME()) || userEntity.getUsername().equals(applicationConfig.getDEFAULT_ADMIN_USER_USERNAME())) {
            throw new UserException(ApplicationMessageEnum.DEFAULT_USER_DELETE_ERROR);
        }
        userRepository.deleteById(userId);
        return userEntity;
    }

    // 根据id修改用户密码
    @CachePut(value = "user_cache", key = "#userId")
    public UserEntity updatePasswordById(String userId, String password) {
        if (StringUtils.isEmpty(password)) {
            throw new UserException(ApplicationMessageEnum.USER_PASSWORD_NOT_FOUND);
        }
        UserEntity userEntity = getUserById(userId);
        userEntity.setPassword(new BCryptPasswordEncoder().encode(password));
        return userRepository.save(userEntity);
    }

    // 根据id修改用户密级
    @CachePut(value = "user_cache", key = "#userId")
    public UserEntity updateSecurityClassificationById(String userId, int securityClassification) {
        SecurityClassificationEnum securityClassificationEnum = SecurityClassificationEnum.getEnum(securityClassification);
        UserEntity userEntity = getUserById(userId);
        userEntity.setSecurityClassification(securityClassificationEnum.getCode());
        return userRepository.save(userEntity);
    }

    // 根据id修改用户部门
    @CachePut(value = "user_cache", key = "#userId")
    public UserEntity updateDepartmentById(String userId, String departmentId) {
        if (!departmentService.hasDepartmentById(departmentId)) {
            throw new DepartmentException(ApplicationMessageEnum.DEPARTMENT_NAME_NOT_EXISTS);
        }
        UserEntity userEntity = getUserById(userId);
        userEntity.setDepartment(departmentService.getDepartmentById(departmentId));
        return userRepository.save(userEntity);
    }

    // 根据id修改用户部门
    public Set<UserEntity> updateDepartmentByIds(String[] userIds, DepartmentEntity departmentEntity) {
        Set<UserEntity> userEntitySet = new HashSet<>();
        for (String userId : userIds) {
            userEntitySet.add(updateDepartmentById(userId, departmentEntity.getId()));
        }
        return userEntitySet;
    }


    // 根据Id查询用户
    @Cacheable(value = "user_cache", key = "#userId")
    public UserEntity getUserById(String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new UserException(ApplicationMessageEnum.USER_ID_NOT_FOUND);
        }
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
        if (!userEntityOptional.isPresent()) {
            throw new UserException(ApplicationMessageEnum.USER_ID_NOT_EXISTS);
        }
        return userEntityOptional.get();
    }

    // 根据用户名查询用户
    public UserEntity getUserByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            throw new UserException(ApplicationMessageEnum.USER_USERNAME_NOT_FOUND);
        }
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);
        if (!userEntityOptional.isPresent()) {
            throw new UserException(ApplicationMessageEnum.USER_USERNAME_NOT_EXISTS);
        }
        return userEntityOptional.get();
    }

    // 分页查询全部用户(根据权限查询)
    public Page<UserEntity> getUsers(String username, Pageable pageable) {
        UserEntity userEntity = getUserByUsername(username);
        for (RoleEntity roleEntity : userEntity.getRoleEntities()) {
            if (roleEntity.getName().equals(applicationConfig.getDEFAULT_ADMIN_ROLE_NAME())) {
                return userRepository.findAllByUsernameNot(applicationConfig.getDEFAULT_ADMIN_ROLE_NAME(), pageable);
            }
        }
        return userRepository.findAllByUsernameNotAndUsernameNot(applicationConfig.getDEFAULT_ADMIN_ROLE_NAME(), applicationConfig.getDEFAULT_AUDIT_ROLE_NAME(), pageable);
    }

    // 按部门查询用户
    public Page<UserEntity> getUsersByDepartment(Pageable pageable, DepartmentEntity departmentEntity) {
        return userRepository.findByDepartment(pageable, departmentEntity);
    }

    // 分页查询全部用户
    public Set<UserEntity> getUsersByDepartment(DepartmentEntity departmentEntity) {
        return userRepository.findByDepartment(departmentEntity);
    }

    // 根据多个Id查询用户
    public Set<UserEntity> getUsers(String[] userIds) {
        Set<UserEntity> userEntitySet = new HashSet<>();
        for (String userId : userIds) {
            UserEntity userEntity = getUserById(userId);
            userEntitySet.add(userEntity);
        }
        return userEntitySet;
    }

    // 管理员修改用户权限
    public UserEntity modifyRoleByAdmin(String userId, String departmentName, String... roleEntities) {
        UserEntity userEntity = getUserById(userId);
        if (StringUtils.isEmpty(userEntity.getUsername())) {
            throw new UserException(ApplicationMessageEnum.USER_USERNAME_NOT_FOUND);
        }
        if (!hasUserByUsername(userEntity.getUsername())) {
            throw new UserException(ApplicationMessageEnum.USER_USERNAME_NOT_FOUND);
        }
        if (StringUtils.isEmpty(userEntity.getPassword())) {
            throw new UserException(ApplicationMessageEnum.USER_PASSWORD_NOT_FOUND);
        }
        if (!departmentService.hasDepartmentByName(departmentName)) {
            throw new DepartmentException(ApplicationMessageEnum.DEPARTMENT_NAME_NOT_FOUND);
        }
        RoleEntity roleEntity = null;
        for (String roleNames : roleEntities) {
            if (!roleService.hasRoleByName(roleNames)) {
                throw new RoleException(ApplicationMessageEnum.ROLE_NAME_NOT_FOUND);
            } else {
                if (roleNames.equals(applicationConfig.getDEFAULT_ADMIN_ROLE_NAME())) {
                    throw new RoleException(ApplicationMessageEnum.ERROR_PERMISSION_DENIED);
                } else {
                    roleEntity = roleService.getRoleByName(roleNames);
                }

            }
        }
        userEntity.setDepartment(departmentService.getDepartmentByName(departmentName));
        userEntity.setRoleEntities(new HashSet<>(Arrays.asList(roleEntity)));
        return userRepository.save(userEntity);
    }

    private void verificationInfo(UserEntity userEntity, String departmentName) {
        if (StringUtils.isEmpty(userEntity.getUsername())) {
            throw new UserException(ApplicationMessageEnum.USER_USERNAME_NOT_FOUND);
        }
        if (hasUserByUsername(userEntity.getUsername())) {
            throw new UserException(ApplicationMessageEnum.USER_USERNAME_EXISTS);
        }
        if (StringUtils.isEmpty(userEntity.getPassword())) {
            throw new UserException(ApplicationMessageEnum.USER_PASSWORD_NOT_FOUND);
        }
        if (!departmentService.hasDepartmentByName(departmentName)) {
            throw new DepartmentException(ApplicationMessageEnum.DEPARTMENT_NAME_NOT_FOUND);
        }
    }

    // 根据用户名判断用户是否存在
    public boolean hasUserByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return false;
        }
        return userRepository.existsByUsername(username);
    }

    public UserEntity updateDepartmentByAdmin(String departmentName, UserEntity userEntity) {
        if (!departmentService.hasDepartmentByName(departmentName)) {
            throw new DepartmentException(ApplicationMessageEnum.DEPARTMENT_NAME_NOT_FOUND);
        }
        DepartmentEntity departmentEntity = departmentService.getDepartmentByName(departmentName);
        userEntity.setDepartment(departmentEntity);
        return userRepository.save(userEntity);
    }

}
