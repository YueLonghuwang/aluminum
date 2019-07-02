package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.ApplicationConfig;
import com.rengu.project.aluminum.entity.AlgorithmAndServerEntity;
import com.rengu.project.aluminum.entity.ApplicationRecord;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.enums.ResourceStatusEnum;
import com.rengu.project.aluminum.enums.SecurityClassificationEnum;
import com.rengu.project.aluminum.exception.ResourceException;
import com.rengu.project.aluminum.repository.AlgorithmAndServerRepository;
import com.rengu.project.aluminum.repository.ApplicationRecordRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * author : yaojiahao
 * Date: 2019/6/13 14:32
 **/
@Slf4j
@Service
@Transactional
public class AlgorithmAndServerService extends ResourceService<AlgorithmAndServerEntity> {
    private final AlgorithmAndServerRepository algorithmAndServerRepository;
    private final ApplicationRecordRepository applicationRecordRepository;
    private final UserService userService;
    private final ResourceFileService resourceFileService;

    public AlgorithmAndServerService(AlgorithmAndServerRepository algorithmAndServerRepository, ApplicationRecordRepository applicationRecordRepository, UserService userService, ResourceFileService resourceFileService) {
        this.algorithmAndServerRepository = algorithmAndServerRepository;
        this.applicationRecordRepository = applicationRecordRepository;
        this.userService = userService;
        this.resourceFileService = resourceFileService;
    }

    // 保存模型资源
    @Override
    @CachePut(value = "ModelResource_Cache", key = "#algorithmAndServerEntity.getId()")
    public AlgorithmAndServerEntity saveResource(AlgorithmAndServerEntity algorithmAndServerEntity, UserEntity userEntity) {
        super.securityCheck(algorithmAndServerEntity, userEntity);
        super.saveResourceCheck(algorithmAndServerEntity, userEntity);
        if (hasStandardByNameAndVersionAndStatus(algorithmAndServerEntity.getName(), algorithmAndServerEntity.getVersion(), ResourceStatusEnum.PASSED.getCode(), ResourceStatusEnum.REVIEWING.getCode())) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_NAME_AND_VERSION_EXISTS);
        }
        return algorithmAndServerRepository.save(algorithmAndServerEntity);
    }

    // 通过ID删除模型资源
    @Override
    @CacheEvict(value = "ModelResource_Cache", key = "#resourceId")
    public AlgorithmAndServerEntity deleteResourceById(String resourceId, UserEntity userEntity) throws IOException {
        AlgorithmAndServerEntity algorithmAndServerEntity = getResourceById(resourceId);
        super.securityCheck(algorithmAndServerEntity, userEntity);
        resourceFileService.deleteResourceFileById(algorithmAndServerEntity.getId());
        algorithmAndServerRepository.delete(algorithmAndServerEntity);
        return algorithmAndServerEntity;
    }

    // 通过模型Id修改模型资源
    @Override
    @CachePut(value = "AlgorithmAndServer_Cache", key = "#resourceId")
    public AlgorithmAndServerEntity updateResourceById(String resourceId, AlgorithmAndServerEntity algorithmAndServerEntityArgs, UserEntity userEntity) {
        AlgorithmAndServerEntity algorithmAndServerEntity = getResourceById(resourceId);
        super.securityCheck(algorithmAndServerEntity, userEntity);
        if (hasStandardByNameAndVersionAndStatus(algorithmAndServerEntityArgs.getName(), algorithmAndServerEntityArgs.getVersion(), ResourceStatusEnum.PASSED.getCode(), ResourceStatusEnum.REVIEWING.getCode())) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_NAME_AND_VERSION_EXISTS);
        }
        BeanUtils.copyProperties(algorithmAndServerEntityArgs, algorithmAndServerEntity, "id", "createTime", "securityClassification", "status", "createUser", "modifyUser");
        algorithmAndServerRepository.save(algorithmAndServerEntity);
        return algorithmAndServerEntity;
    }

    // 更新资源权限等级
    @Override
    @CachePut(value = "AlgorithmAndServer_Cache", key = "#resourceId")
    public AlgorithmAndServerEntity updateResourceSecurityClassificationById(String resourceId, SecurityClassificationEnum securityClassificationEnum, UserEntity userEntity) {
        AlgorithmAndServerEntity algorithmAndServerEntity = getResourceById(resourceId);
        super.securityCheck(algorithmAndServerEntity, userEntity);
        algorithmAndServerEntity.setSecurityClassification(securityClassificationEnum.getCode());
        return algorithmAndServerRepository.save(algorithmAndServerEntity);
    }

    // 更新资源状态
    @Override
    @CachePut(value = "AlgorithmAndServer_Cache", key = "#resourceId")
    public AlgorithmAndServerEntity updateResourceStatusById(String resourceId, ResourceStatusEnum resourceStatusEnum, UserEntity userEntity) {
        AlgorithmAndServerEntity algorithmAndServerEntity = getResourceById(resourceId);
        super.securityCheck(algorithmAndServerEntity, userEntity);
        algorithmAndServerEntity.setStatus(resourceStatusEnum.getCode());
        return algorithmAndServerRepository.save(algorithmAndServerEntity);
    }

    // 通过资源Id获取资源
    @Override
    @Cacheable(value = "AlgorithmAndServer_Cache", key = "#resourceId")
    public AlgorithmAndServerEntity getResourceById(String resourceId) {
        if (StringUtils.isEmpty(resourceId)) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_ID_NOT_FOUND);
        }
        Optional<AlgorithmAndServerEntity> algorithmAndServerEntity = algorithmAndServerRepository.findById(resourceId);
        if (!algorithmAndServerEntity.isPresent()) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_ID_NOT_EXISTS);
        }
        return algorithmAndServerEntity.get();
    }


    @Override
    @Cacheable(value = "AlgorithmAndServer_Cache", key = "#resourceId")
    public AlgorithmAndServerEntity getResourceById(String resourceId, UserEntity userEntity) {
        AlgorithmAndServerEntity algorithmAndServerEntity = getResourceById(resourceId);
        super.securityCheck(algorithmAndServerEntity, userEntity);
        return algorithmAndServerEntity;
    }

    // 通过密级获取资源
    @Override
    public Page getResourcesBySecurityClassification(Pageable pageable, SecurityClassificationEnum securityClassificationEnum, int status) {
        return algorithmAndServerRepository.findBySecurityClassificationLessThanEqualAndStatus(pageable, securityClassificationEnum.getCode(), status);
    }

    @Override
    public Page getResourcesByUser(Pageable pageable, UserEntity userEntity, int status) {
        return getResourcesBySecurityClassification(pageable, SecurityClassificationEnum.getEnum(userEntity.getSecurityClassification()), status);
    }


    @Override
    public Page getResources(Pageable pageable) {
        return algorithmAndServerRepository.findAll(pageable);
    }

    @Override
    public File downloadResourceById(String resourceId, UserEntity userEntity) throws IOException {
        AlgorithmAndServerEntity algorithmAndServerEntity = getResourceById(resourceId);
        super.securityCheck(algorithmAndServerEntity, userEntity);
        return resourceFileService.downloadResourceFileByResourceId(algorithmAndServerEntity.getId());
    }

    @Override
    public boolean hasStandardByNameAndVersionAndStatus(String name, String version, int... status) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(version) || status.length == 0) {
            return false;
        }
        return algorithmAndServerRepository.existsByNameAndVersionAndStatusIn(name, version, status);
    }

    public List<ApplicationRecord> getPutInStorageResources(String userId) {
        List<ApplicationRecord> applicationRecordList = applicationRecordRepository.findAll();
        List<ApplicationRecord> applicationRecordArrayList = new ArrayList<>();
        for (ApplicationRecord applicationRecord : applicationRecordList) {
            if (applicationRecord.getAlgorithmServer().getSecurityClassification() <= userService.getUserById(userId).getSecurityClassification()) {
                applicationRecordArrayList.add(applicationRecord);
            }
        }
        return applicationRecordArrayList;
    }

    // 根据用户姓名查询入库资源文件
    public Page<ApplicationRecord> getPassResource(UserEntity userEntity, Pageable pageable) {
        // 根据资源类型，资源是否批准完成状态，出库还是入库状态，以及等级权限进行判断
        return applicationRecordRepository.findByResourceTypeAndApplicationStatusAndCurrentStatusAndSecurityClassificationLessThanEqual(pageable, ApplicationConfig.ALGORITHM_RESOURCE, ApplicationConfig.BE_PUT_IN_STORAGE, ApplicationConfig.PASS_ALL_AUDIT, userEntity.getSecurityClassification());
    }

    // 根据用户姓名查询出库资源文件
    public Page<ApplicationRecord> getOutResources(UserEntity userEntity, Pageable pageable) {
        // 根据资源类型，资源是否批准完成状态，出库还是入库状态，以及等级权限进行判断
        return applicationRecordRepository.findByResourceTypeAndApplicationStatusAndCurrentStatusAndSecurityClassificationLessThanEqual(pageable, ApplicationConfig.ALGORITHM_RESOURCE, ApplicationConfig.PUT_IN_STORAGE, ApplicationConfig.PASS_ALL_AUDIT, userEntity.getSecurityClassification());
    }
}
