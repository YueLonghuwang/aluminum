package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.entity.ResourceEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.enums.ResourceStatusEnum;
import com.rengu.project.aluminum.enums.SecurityClassificationEnum;
import com.rengu.project.aluminum.exception.ResourceException;
import com.rengu.project.aluminum.exception.ResourceFileException;
import com.rengu.project.aluminum.exception.SecurityClassificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * com.rengu.project.aluminum.service
 *
 * @author hanchangming
 * @date 2019-06-11
 */

@Slf4j
public abstract class ResourceService<T> {

    // 保存资源
    public abstract T saveResource(T t, UserEntity userEntity);

    // 根据删除资源
    public abstract T deleteResourceById(String resourceId, UserEntity userEntity) throws IOException;

    // 根据id更新资源
    public abstract T updateResourceById(String resourceId, T t, UserEntity userEntity);

    // 根据id更新资源
    public abstract T updateResourceSecurityClassificationById(String resourceId, SecurityClassificationEnum securityClassificationEnum, UserEntity userEntity);

    // 根据id更新资源
    public abstract T updateResourceStatusById(String resourceId, ResourceStatusEnum resourceStatusEnum, UserEntity userEntity);

    // 根据Id查询资源
    public abstract T getResourceById(String resourceId);

    // 根据Id查询资源
    public abstract T getResourceById(String resourceId, UserEntity userEntity);

    // 根据密级查询资源
    public abstract Page<T> getResourcesBysecurityClassification(Pageable pageable, SecurityClassificationEnum securityClassificationEnum);

    // 根据用户查询资源
    public abstract Page<T> getResourcesByUser(Pageable pageable, UserEntity userEntity);

    // 根据全部
    public abstract Page<T> getResources(Pageable pageable);

    // 根据Id下载资源
    public abstract File downloadResourceById(String resourceId, UserEntity userEntity) throws IOException;

    // 根据名称和版本号及状态查询是否存在
    public abstract boolean hasStandardByNameAndVersionAndStatus(String name, String version, int... status);

    // 库的保存逻辑
    void saveResourceCheck(ResourceEntity resourceEntity, UserEntity userEntity) {
        if (StringUtils.isEmpty(resourceEntity.getName())) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_NAME_NOT_FOUND);
        }
        if (StringUtils.isEmpty(resourceEntity.getAuthor())) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_AUTHOR_NOT_FOUND);
        }
        if (StringUtils.isEmpty(resourceEntity.getUnit())) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_UNIT_NOT_FOUND);
        }
        if (StringUtils.isEmpty(resourceEntity.getVersion())) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_VERSION_NOT_FOUND);
        }
        resourceEntity.setCreateUser(userEntity);
        resourceEntity.setModifyUser(userEntity);
    }

    // 库的密级检查逻辑
    void securityCheck(ResourceEntity resourceEntity, UserEntity userEntity) {
        int resourceSecurity = SecurityClassificationEnum.getEnum(resourceEntity.getSecurityClassification()).getCode();
        int userSecurity = SecurityClassificationEnum.getEnum(userEntity.getSecurityClassification()).getCode();
        if (resourceSecurity > userSecurity) {
            throw new SecurityClassificationException(ApplicationMessageEnum.SECURITY_CLASSIFICATION_NOT_ENOUGH);
        }
    }

    // 库的状态检查
    void statusCheck(ResourceEntity resourceEntity) {
        if (resourceEntity.getStatus() != ResourceStatusEnum.PASSED.getCode()) {
            throw new ResourceFileException(ApplicationMessageEnum.RESOURCE_FILE_IS_NOT_PASSED);
        }
    }

    // 入库
    void putInStorage(ResourceEntity resourceEntity) {
        if (resourceEntity.getStatus() == ResourceStatusEnum.PASSED.getCode()) {
            throw new ResourceFileException(ApplicationMessageEnum.RESOURCE_FILE_IS_PASSED);
        } else if (resourceEntity.getStatus() == ResourceStatusEnum.REFUSED.getCode()) {
            throw new ResourceFileException(ApplicationMessageEnum.RESOURCE_FILE_IS_REJECT);
        } else if (resourceEntity.getStatus() == ResourceStatusEnum.REVIEWING.getCode()) {
            resourceEntity.setStatus(ResourceStatusEnum.PASSED.getCode());
        }
    }

}
