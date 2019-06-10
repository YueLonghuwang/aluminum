package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.entity.RoleEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.exception.UserException;
import com.rengu.project.aluminum.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
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

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    // 根据用户名判断用户是否存在
    public boolean hasUserByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return false;
        }
        return userRepository.existsByUsername(username);
    }
}
