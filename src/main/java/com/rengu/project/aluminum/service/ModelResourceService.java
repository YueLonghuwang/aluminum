package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.entity.ModelResourceEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.enums.ResourceStatusEnum;
import com.rengu.project.aluminum.enums.SecurityClassificationEnum;
import com.rengu.project.aluminum.exception.ResourceException;
import com.rengu.project.aluminum.repository.ModelResourceRepository;
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
 * Date: 2019/6/13 14:21
 **/
@Slf4j
@Service
@Transactional
public class ModelResourceService extends ResourceService<ModelResourceEntity> {
    private final ModelResourceRepository modelResourceRepository;
    private final ResourceFileService resourceFileService;

    public ModelResourceService(ModelResourceRepository modelResourceRepository, ResourceFileService resourceFileService) {
        this.modelResourceRepository = modelResourceRepository;
        this.resourceFileService = resourceFileService;
    }

    // 保存模型资源
    @Override
    @CachePut(value = "ModelResource_Cache", key = "#modelResourceEntity.getId()")
    public ModelResourceEntity saveResource(ModelResourceEntity modelResourceEntity, UserEntity userEntity) {
        super.securityCheck(modelResourceEntity, userEntity);
        super.saveResourceCheck(modelResourceEntity, userEntity);
        if (hasStandardByNameAndVersionAndStatus(modelResourceEntity.getName(), modelResourceEntity.getVersion(), ResourceStatusEnum.PASSED.getCode(), ResourceStatusEnum.REVIEWING.getCode())) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_NAME_AND_VERSION_EXISTS);
        }
        return modelResourceRepository.save(modelResourceEntity);
    }

    // 通过ID删除模型资源
    @Override
    @CacheEvict(value = "ModelResource_Cache", key = "#resourceId")
    public ModelResourceEntity deleteResourceById(String resourceId, UserEntity userEntity) throws IOException {
        ModelResourceEntity modelResourceEntity = getResourceById(resourceId);
        super.securityCheck(modelResourceEntity, userEntity);
        resourceFileService.deleteResourceFileById(modelResourceEntity.getId());
        modelResourceRepository.delete(modelResourceEntity);
        return modelResourceEntity;
    }

    // 通过模型Id修改模型资源
    @Override
    @CachePut(value = "ModelResource_Cache", key = "#resourceId")
    public ModelResourceEntity updateResourceById(String resourceId, ModelResourceEntity modelResourceEntityArgs, UserEntity userEntity) {
        ModelResourceEntity modelResourceEntity = getResourceById(resourceId);
        super.securityCheck(modelResourceEntity, userEntity);
        if (hasStandardByNameAndVersionAndStatus(modelResourceEntityArgs.getName(), modelResourceEntityArgs.getVersion(), ResourceStatusEnum.PASSED.getCode(), ResourceStatusEnum.REVIEWING.getCode())) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_NAME_AND_VERSION_EXISTS);
        }
        BeanUtils.copyProperties(modelResourceEntityArgs, modelResourceEntity, "id", "createTime", "securityClassification", "status", "createUser", "modifyUser");
        modelResourceRepository.save(modelResourceEntity);
        return modelResourceEntity;
    }

    // 更新资源权限等级
    @Override
    @CachePut(value = "ModelResource_Cache", key = "#resourceId")
    public ModelResourceEntity updateResourceSecurityClassificationById(String resourceId, SecurityClassificationEnum securityClassificationEnum, UserEntity userEntity) {
        ModelResourceEntity modelResourceEntity = getResourceById(resourceId);
        super.securityCheck(modelResourceEntity, userEntity);
        modelResourceEntity.setSecurityClassification(securityClassificationEnum.getCode());
        return modelResourceRepository.save(modelResourceEntity);
    }

    // 更新资源状态
    @Override
    @CachePut(value = "ModelResource_Cache", key = "#resourceId")
    public ModelResourceEntity updateResourceStatusById(String resourceId, ResourceStatusEnum resourceStatusEnum, UserEntity userEntity) {
        ModelResourceEntity modelResourceEntity = getResourceById(resourceId);
        super.securityCheck(modelResourceEntity, userEntity);
        modelResourceEntity.setStatus(resourceStatusEnum.getCode());
        return modelResourceRepository.save(modelResourceEntity);
    }

    // 通过资源Id获取资源
    @Override
    @Cacheable(value = "ModelResource_Cache", key = "#resourceId")
    public ModelResourceEntity getResourceById(String resourceId) {
        if (StringUtils.isEmpty(resourceId)) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_ID_NOT_FOUND);
        }
        Optional<ModelResourceEntity> modelResourceEntity = modelResourceRepository.findById(resourceId);
        if (!modelResourceEntity.isPresent()) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_ID_NOT_EXISTS);
        }
        return modelResourceEntity.get();
    }


    @Override
    @Cacheable(value = "ModelResource_Cache", key = "#resourceId")
    public ModelResourceEntity getResourceById(String resourceId, UserEntity userEntity) {
        ModelResourceEntity modelResourceEntity = getResourceById(resourceId);
        super.securityCheck(modelResourceEntity, userEntity);
        return modelResourceEntity;
    }

    // 通过密级获取资源
    @Override
    public Page getResourcesBysecurityClassification(Pageable pageable, SecurityClassificationEnum securityClassificationEnum) {
        return modelResourceRepository.findBySecurityClassificationLessThanEqualAndStatus(pageable, securityClassificationEnum.getCode(), ResourceStatusEnum.PASSED.getCode());
    }

    //
    @Override
    public Page getResourcesByUser(Pageable pageable, UserEntity userEntity) {
        return getResourcesBysecurityClassification(pageable, SecurityClassificationEnum.getEnum(userEntity.getSecurityClassification()));
    }

    @Override
    public Page getResources(Pageable pageable) {
        return modelResourceRepository.findAll(pageable);
    }

    @Override
    public File downloadResourceById(String resourceId, UserEntity userEntity) throws IOException {
        ModelResourceEntity modelResourceEntity = getResourceById(resourceId);
        super.securityCheck(modelResourceEntity, userEntity);
        return resourceFileService.downloadResourceFileByResourceId(modelResourceEntity.getId());
    }

    @Override
    public boolean hasStandardByNameAndVersionAndStatus(String name, String version, int... status) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(version) || status.length == 0) {
            return false;
        }
        return modelResourceRepository.existsByNameAndVersionAndStatusIn(name, version, status);
    }
}
