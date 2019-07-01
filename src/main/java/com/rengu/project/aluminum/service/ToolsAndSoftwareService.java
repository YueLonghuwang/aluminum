package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.entity.ToolsAndSoftwareEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.enums.ResourceStatusEnum;
import com.rengu.project.aluminum.enums.SecurityClassificationEnum;
import com.rengu.project.aluminum.exception.ResourceException;
import com.rengu.project.aluminum.repository.ToolsAndSoftwareRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * author : yaojiahao
 * Date: 2019/6/13 14:46
 **/

@Slf4j
@Service
@Transactional
public class ToolsAndSoftwareService extends ResourceService<ToolsAndSoftwareEntity> {
    private final ToolsAndSoftwareRepository toolsAndSoftwareRepository;
    private final ResourceFileService resourceFileService;

    public ToolsAndSoftwareService(ToolsAndSoftwareRepository toolsAndSoftwareRepository, ResourceFileService resourceFileService) {
        this.toolsAndSoftwareRepository = toolsAndSoftwareRepository;
        this.resourceFileService = resourceFileService;
    }

    // 保存模型资源
    @Override
    @CachePut(value = "ToolsAndSofware_Cache", key = "#toolsAndSoftwareEntity.getId()")
    public ToolsAndSoftwareEntity saveResource(ToolsAndSoftwareEntity toolsAndSoftwareEntity, UserEntity userEntity) {
        super.securityCheck(toolsAndSoftwareEntity, userEntity);
        super.saveResourceCheck(toolsAndSoftwareEntity, userEntity);
        if (hasStandardByNameAndVersionAndStatus(toolsAndSoftwareEntity.getName(), toolsAndSoftwareEntity.getVersion(), ResourceStatusEnum.PASSED.getCode(), ResourceStatusEnum.REVIEWING.getCode())) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_NAME_AND_VERSION_EXISTS);
        }
        return toolsAndSoftwareRepository.save(toolsAndSoftwareEntity);
    }

    // 通过ID删除模型资源
    @Override
    @CacheEvict(value = "ToolsAndSofware_Cache", key = "#resourceId")
    public ToolsAndSoftwareEntity deleteResourceById(String resourceId, UserEntity userEntity) throws IOException {
        ToolsAndSoftwareEntity toolsAndSoftwareEntity = getResourceById(resourceId);
        super.securityCheck(toolsAndSoftwareEntity, userEntity);
        resourceFileService.deleteResourceFileById(toolsAndSoftwareEntity.getId());
        toolsAndSoftwareRepository.delete(toolsAndSoftwareEntity);
        return toolsAndSoftwareEntity;
    }

    // 通过模型Id修改模型资源
    @Override
    @CachePut(value = "ToolsAndSofware_Cache", key = "#resourceId")
    public ToolsAndSoftwareEntity updateResourceById(String resourceId, ToolsAndSoftwareEntity toolsAndSoftwareEntityArgs, UserEntity userEntity) {
        ToolsAndSoftwareEntity toolsAndSoftwareEntity = getResourceById(resourceId);
        super.securityCheck(toolsAndSoftwareEntity, userEntity);
        if (hasStandardByNameAndVersionAndStatus(toolsAndSoftwareEntityArgs.getName(), toolsAndSoftwareEntityArgs.getVersion(), ResourceStatusEnum.PASSED.getCode(), ResourceStatusEnum.REVIEWING.getCode())) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_NAME_AND_VERSION_EXISTS);
        }
        BeanUtils.copyProperties(toolsAndSoftwareEntityArgs, toolsAndSoftwareEntity, "id", "createTime", "securityClassification", "status", "createUser", "modifyUser");
        toolsAndSoftwareRepository.save(toolsAndSoftwareEntity);
        return toolsAndSoftwareEntity;
    }

    // 更新资源权限等级
    @Override
    @CachePut(value = "ToolsAndSofware_Cache", key = "#resourceId")
    public ToolsAndSoftwareEntity updateResourceSecurityClassificationById(String resourceId, SecurityClassificationEnum securityClassificationEnum, UserEntity userEntity) {
        ToolsAndSoftwareEntity toolsAndSoftwareEntity = getResourceById(resourceId);
        super.securityCheck(toolsAndSoftwareEntity, userEntity);
        toolsAndSoftwareEntity.setSecurityClassification(securityClassificationEnum.getCode());
        return toolsAndSoftwareRepository.save(toolsAndSoftwareEntity);
    }

    // 更新资源状态
    @Override
    @CachePut(value = "ToolsAndSofware_Cache", key = "#resourceId")
    public ToolsAndSoftwareEntity updateResourceStatusById(String resourceId, ResourceStatusEnum resourceStatusEnum, UserEntity userEntity) {
        ToolsAndSoftwareEntity toolsAndSoftwareEntity = getResourceById(resourceId);
        super.securityCheck(toolsAndSoftwareEntity, userEntity);
        toolsAndSoftwareEntity.setStatus(resourceStatusEnum.getCode());
        return toolsAndSoftwareRepository.save(toolsAndSoftwareEntity);
    }

    // 通过资源Id获取资源
    @Override
    @Cacheable(value = "ToolsAndSofware_Cache", key = "#resourceId")
    public ToolsAndSoftwareEntity getResourceById(String resourceId) {
        if (StringUtils.isEmpty(resourceId)) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_ID_NOT_FOUND);
        }
        Optional<ToolsAndSoftwareEntity> toolsAndSoftwareEntity = toolsAndSoftwareRepository.findById(resourceId);
        if (!toolsAndSoftwareEntity.isPresent()) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_ID_NOT_EXISTS);
        }
        return toolsAndSoftwareEntity.get();
    }


    @Override
    @Cacheable(value = "ToolsAndSofware_Cache", key = "#resourceId")
    public ToolsAndSoftwareEntity getResourceById(String resourceId, UserEntity userEntity) {
        ToolsAndSoftwareEntity toolsAndSoftwareEntity = getResourceById(resourceId);
        super.securityCheck(toolsAndSoftwareEntity, userEntity);
        return toolsAndSoftwareEntity;
    }

    // 通过密级获取资源
    @Override
    public Page getResourcesBysecurityClassification(Pageable pageable, SecurityClassificationEnum securityClassificationEnum) {
        return toolsAndSoftwareRepository.findBySecurityClassificationLessThanEqualAndStatus(pageable, securityClassificationEnum.getCode(), ResourceStatusEnum.PASSED.getCode());
    }

    //
    @Override
    public Page getResourcesByUser(Pageable pageable, UserEntity userEntity) {
        return getResourcesBysecurityClassification(pageable, SecurityClassificationEnum.getEnum(userEntity.getSecurityClassification()));
    }

    @Override
    public Page getResources(Pageable pageable) {
        return toolsAndSoftwareRepository.findAll(pageable);
    }

    @Override
    public File downloadResourceById(String resourceId, UserEntity userEntity) throws IOException {
        ToolsAndSoftwareEntity toolsAndSoftwareEntity = getResourceById(resourceId);
        super.securityCheck(toolsAndSoftwareEntity, userEntity);
        return resourceFileService.downloadResourceFileByResourceId(toolsAndSoftwareEntity.getId());
    }

    @Override
    public boolean hasStandardByNameAndVersionAndStatus(String name, String version, int... status) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(version) || status.length == 0) {
            return false;
        }
        return toolsAndSoftwareRepository.existsByNameAndVersionAndStatusIn(name, version, status);
    }
}
