package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.entity.RoleEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.exception.RoleException;
import com.rengu.project.aluminum.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // 保存角色
    @CachePut(value = "role_cache", key = "#roleEntity.getName()")
    public RoleEntity saveRole(RoleEntity roleEntity) {
        if (StringUtils.isEmpty(roleEntity.getName())) {
            throw new RoleException(ApplicationMessageEnum.ROLE_NAME_NOT_FOUND);
        }
        if (hasRoleByName(roleEntity.getName())) {
            throw new RoleException(ApplicationMessageEnum.ROLE_NAME_EXISTS);
        }
        return roleRepository.save(roleEntity);
    }

    // 根据角色名称查询角色
    @Cacheable(value = "role_cache", key = "#name")
    public RoleEntity getRoleByName(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new RoleException(ApplicationMessageEnum.ROLE_NAME_NOT_FOUND);
        }
        Optional<RoleEntity> roleEntityOptional = roleRepository.findByName(name);
        if (!roleEntityOptional.isPresent()) {
            throw new RoleException(ApplicationMessageEnum.ROLE_NAME_NOT_EXISTS);
        }
        return roleEntityOptional.get();
    }

    // 根据角色名称判断角色是否存在
    public boolean hasRoleByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        return roleRepository.existsByName(name);
    }
}

