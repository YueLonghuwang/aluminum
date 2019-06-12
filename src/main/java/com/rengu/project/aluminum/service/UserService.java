package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.ApplicationConfig;
import com.rengu.project.aluminum.entity.DepartmentEntity;
import com.rengu.project.aluminum.entity.RoleEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.enums.SecurityClassificationEnum;
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

    public UserService(UserRepository userRepository, ApplicationConfig applicationConfig) {
        this.userRepository = userRepository;
        this.applicationConfig = applicationConfig;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);
        if (!userEntityOptional.isPresent()) {
            throw new UsernameNotFoundException(username + "不存在或不合法");
        }
        return userEntityOptional.get();
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

    // 根据id修改用户密级
    @CachePut(value = "user_cache", key = "#userId")
    public UserEntity updateDepartmentById(String userId, DepartmentEntity departmentEntity) {
        UserEntity userEntity = getUserById(userId);
        userEntity.setDepartment(departmentEntity);
        return userRepository.save(userEntity);
    }

    // 根据id修改用户密级
    public Set<UserEntity> updateDepartmentByIds(String[] userIds, DepartmentEntity departmentEntity) {
        Set<UserEntity> userEntitySet = new HashSet<>();
        for (String userId : userIds) {
            userEntitySet.add(updateDepartmentById(userId, departmentEntity));
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

    // 分页查询全部用户
    public Page<UserEntity> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    // 安部门查询用户
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


    // 根据用户名判断用户是否存在
    public boolean hasUserByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return false;
        }
        return userRepository.existsByUsername(username);
    }
}
