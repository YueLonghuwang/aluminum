package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.ApplicationConfig;
import com.rengu.project.aluminum.entity.ApplicationRecord;
import com.rengu.project.aluminum.entity.ModelResourceEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.enums.ResourceStatusEnum;
import com.rengu.project.aluminum.enums.SecurityClassificationEnum;
import com.rengu.project.aluminum.exception.ResourceException;
import com.rengu.project.aluminum.repository.ApplicationRecordRepository;
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
import java.util.List;
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
    private final UserService userService;
    private final ApplicationRecordRepository applicationRecordRepository;

    public ModelResourceService(ModelResourceRepository modelResourceRepository, ResourceFileService resourceFileService, UserService userService, ApplicationRecordRepository applicationRecordRepository) {
        this.modelResourceRepository = modelResourceRepository;
        this.resourceFileService = resourceFileService;
        this.userService = userService;
        this.applicationRecordRepository = applicationRecordRepository;
    }

    // 保存模型资源
    @Override
    @CachePut(value = "ModelResource_Cache", key = "#modelResourceEntity.getId()")
    public ModelResourceEntity saveResource(ModelResourceEntity modelResourceEntity, UserEntity userEntity) {
        super.securityCheck(modelResourceEntity, userEntity);
        super.saveResourceCheck(modelResourceEntity, userEntity);
        if (hasStandardByNameAndVersionAndStatus(modelResourceEntity.getName(), modelResourceEntity.getVersion(), ResourceStatusEnum.PASSED.getCode(), ResourceStatusEnum.REVIEWING.getCode())) {
            String modelId = getResourceByNameAndVersionAndStatus(modelResourceEntity.getName(), modelResourceEntity.getVersion(), ResourceStatusEnum.PASSED.getCode(), ResourceStatusEnum.REVIEWING.getCode()).getId();
            if (resourceFileService.existsByResourceId(modelId)) {
                return modelResourceRepository.findById(modelId).get();
//                throw new ResourceException(ApplicationMessageEnum.RESOURCE_NAME_AND_VERSION_EXISTS);
            }
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
    public Page getResourcesBySecurityClassification(Pageable pageable, SecurityClassificationEnum securityClassificationEnum, int[] status) {
        return modelResourceRepository.findBySecurityClassificationLessThanEqualAndStatusIn(pageable, securityClassificationEnum.getCode(), status);
    }

    //
    @Override
    public Page getResourcesByUser(Pageable pageable, UserEntity userEntity, int[] status) {
        return getResourcesBySecurityClassification(pageable, SecurityClassificationEnum.getEnum(userEntity.getSecurityClassification()), status);
    }

    @Override
    public Page getResources(Pageable pageable) {
        return modelResourceRepository.findAll(pageable);
    }

    @Override
    public File downloadResourceById(String resourceId, UserEntity userEntity) throws IOException {
        ModelResourceEntity modelResourceEntity = getResourceById(resourceId);
        super.securityCheck(modelResourceEntity, userEntity);
//        super.statusCheck(modelResourceEntity);
        return resourceFileService.downloadResourceFileByResourceId(modelResourceEntity.getId());
    }

    @Override
    public boolean hasStandardByNameAndVersionAndStatus(String name, String version, int... status) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(version) || status.length == 0) {
            return false;
        }
        return modelResourceRepository.existsByNameAndVersionAndStatusIn(name, version, status);
    }

    public ModelResourceEntity getResourceByNameAndVersionAndStatus(String name, String version, int... status) {
        return modelResourceRepository.findByNameAndVersionAndStatusIn(name, version, status).get();
    }


/*    public List<ApplicationRecord> getPutInStorageResources(String userId) {
        List<ApplicationRecord> applicationRecordList = applicationRecordRepository.findAll();
        List<ApplicationRecord> applicationRecordArrayList = new ArrayList<>();
        for (ApplicationRecord applicationRecord : applicationRecordList) {
            if (applicationRecord.getModelResource().getSecurityClassification() <= userService.getUserById(userId).getSecurityClassification()) {
                applicationRecordArrayList.add(applicationRecord);
            }
        }
        return applicationRecordArrayList;
    }*/

    // 根据用户姓名查询入库资源文件
    public Page<ApplicationRecord> getPassResource(UserEntity userEntity, Pageable pageable) {
        // 根据资源类型，资源是否批准完成状态，出库还是入库状态，以及等级权限进行判断
        return applicationRecordRepository.findByResourceTypeAndApplicationStatusAndCurrentStatusAndSecurityClassificationLessThanEqual(pageable, ApplicationConfig.MODEL_RESOURCE, ApplicationConfig.BE_PUT_IN_STORAGE, ApplicationConfig.PASS_ALL_AUDIT, userEntity.getSecurityClassification());
    }

    // 根据用户姓名查询出库资源文件
    public Page<ApplicationRecord> getOutResources(UserEntity userEntity, Pageable pageable) {
        // 根据资源类型，资源是否批准完成状态，出库还是入库状态，以及等级权限进行判断
        return applicationRecordRepository.findByResourceTypeAndApplicationStatusAndCurrentStatusAndSecurityClassificationLessThanEqual(pageable, ApplicationConfig.MODEL_RESOURCE, ApplicationConfig.PUT_IN_STORAGE, ApplicationConfig.PASS_ALL_AUDIT, userEntity.getSecurityClassification());
    }

    // 根据资源ID查询该资源所有的文件
    public List<Object> getAllFilesById(String resourceId) {
        return resourceFileService.getAllFilesById(resourceId);
    }
}
