package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.entity.FileEntity;
import com.rengu.project.aluminum.entity.PreviewFile;
import com.rengu.project.aluminum.entity.StandardEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.enums.ResourceStatusEnum;
import com.rengu.project.aluminum.enums.SecurityClassificationEnum;
import com.rengu.project.aluminum.exception.ResourceException;
import com.rengu.project.aluminum.repository.ResourceFileRepository;
import com.rengu.project.aluminum.repository.StandardRepository;
import com.rengu.project.aluminum.util.PdfFileConventer;
import com.rengu.project.aluminum.util.PreviewFileInit;
import com.rengu.project.aluminum.util.SHAUtil;
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

//import com.rengu.project.aluminum.util.PreviewFileInit;

/**
 * com.rengu.project.aluminum.service
 *
 * @author hanchangming
 * @date 2019-06-11
 */

@Slf4j
@Service
@Transactional
public class StandardService extends ResourceService<StandardEntity> {

    private final StandardRepository standardRepository;
    private final ResourceFileService resourceFileService;
    private final PreviewFileInit previewFileInit;
    private final PdfFileConventer pdfFileConventer;
    private final FileService fileService;
    private final ResourceFileRepository resourceFileRepository;

    public StandardService(StandardRepository standardRepository, ResourceFileService resourceFileService, PreviewFileInit previewFileInit, PdfFileConventer pdfFileConventer, FileService fileService, ResourceFileRepository resourceFileRepository) {
        this.standardRepository = standardRepository;
        this.resourceFileService = resourceFileService;
        this.previewFileInit = previewFileInit;
        this.pdfFileConventer = pdfFileConventer;
        this.fileService = fileService;
        this.resourceFileRepository = resourceFileRepository;
    }

    @Override
    @CachePut(value = "Standard_Cache", key = "#standardEntity.getId()")
    public StandardEntity saveResource(StandardEntity standardEntity, UserEntity userEntity) {
        super.securityCheck(standardEntity, userEntity);
        super.saveResourceCheck(standardEntity, userEntity);
        if (hasStandardByNameAndVersionAndStatus(standardEntity.getName(), standardEntity.getVersion(), ResourceStatusEnum.PASSED.getCode(), ResourceStatusEnum.REVIEWING.getCode())) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_NAME_AND_VERSION_EXISTS);
        }
        return standardRepository.save(standardEntity);
    }

    @Override
    @CacheEvict(value = "Standard_Cache", key = "#resourceId")
    public StandardEntity deleteResourceById(String resourceId, UserEntity userEntity) throws IOException {
        StandardEntity standardEntity = getResourceById(resourceId);
        super.securityCheck(standardEntity, userEntity);
        resourceFileService.deleteResourceFileByResourceId(standardEntity.getId());
        standardRepository.delete(standardEntity);
        return standardEntity;
    }

    @Override
    @CachePut(value = "Standard_Cache", key = "#resourceId")
    public StandardEntity updateResourceById(String resourceId, StandardEntity standardArgs, UserEntity userEntity) {
        StandardEntity standardEntity = getResourceById(resourceId);
        super.securityCheck(standardEntity, userEntity);
        if (hasStandardByNameAndVersionAndStatus(standardArgs.getName(), standardArgs.getVersion(), ResourceStatusEnum.PASSED.getCode(), ResourceStatusEnum.REVIEWING.getCode())) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_NAME_AND_VERSION_EXISTS);
        }
        BeanUtils.copyProperties(standardArgs, standardEntity, "id", "createTime", "securityClassification", "status", "createUser", "modifyUser");
        return standardRepository.save(standardEntity);
    }

    @Override
    @CachePut(value = "Standard_Cache", key = "#resourceId")
    public StandardEntity updateResourceSecurityClassificationById(String resourceId, SecurityClassificationEnum securityClassificationEnum, UserEntity userEntity) {
        StandardEntity standardEntity = getResourceById(resourceId);
        super.securityCheck(standardEntity, userEntity);
        standardEntity.setSecurityClassification(securityClassificationEnum.getCode());
        return standardRepository.save(standardEntity);
    }

    @Override
    @CachePut(value = "Standard_Cache", key = "#resourceId")
    public StandardEntity updateResourceStatusById(String resourceId, ResourceStatusEnum resourceStatusEnum, UserEntity userEntity) {
        StandardEntity standardEntity = getResourceById(resourceId);
        super.securityCheck(standardEntity, userEntity);
        standardEntity.setStatus(resourceStatusEnum.getCode());
        return standardRepository.save(standardEntity);
    }

    @Override
    @Cacheable(value = "Standard_Cache", key = "#resourceId")
    public StandardEntity getResourceById(String resourceId) {
        if (StringUtils.isEmpty(resourceId)) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_ID_NOT_FOUND);
        }
        Optional<StandardEntity> standardEntityOptional = standardRepository.findById(resourceId);
        if (!standardEntityOptional.isPresent()) {
            throw new ResourceException(ApplicationMessageEnum.RESOURCE_ID_NOT_EXISTS);
        }
        return standardEntityOptional.get();
    }

    @Override
    public StandardEntity getResourceById(String resourceId, UserEntity userEntity) {
        StandardEntity standardEntity = getResourceById(resourceId);
        super.securityCheck(standardEntity, userEntity);
        return standardEntity;
    }

    @Override
    public Page<StandardEntity> getResourcesBySecurityClassification(Pageable pageable, SecurityClassificationEnum securityClassificationEnum, int status) {
        return null;
    }

    @Override
    public Page<StandardEntity> getResourcesByUser(Pageable pageable, UserEntity userEntity, int status) {
        return null;
    }

    public Page<StandardEntity> getResourcesBySecurityClassifications(Pageable pageable, SecurityClassificationEnum securityClassificationEnum) {
        return standardRepository.findBySecurityClassificationLessThanEqual(pageable, securityClassificationEnum.getCode());
    }

    public Page<StandardEntity> getResourcesByUsers(Pageable pageable, UserEntity userEntity) {
        return getResourcesBySecurityClassifications(pageable, SecurityClassificationEnum.getEnum(userEntity.getSecurityClassification()));
    }

    @Override
    public Page<StandardEntity> getResources(Pageable pageable) {
        return standardRepository.findAll(pageable);
    }

    @Override
    public File downloadResourceById(String resourceId, UserEntity userEntity) throws IOException {
        StandardEntity standardEntity = getResourceById(resourceId);
        super.securityCheck(standardEntity, userEntity);
        return resourceFileService.downloadResourceFileByResourceId(standardEntity.getId());
    }

    @Override
    public boolean hasStandardByNameAndVersionAndStatus(String name, String version, int... status) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(version) || status.length == 0) {
            return false;
        }
        return standardRepository.existsByNameAndVersionAndStatusIn(name, version, status);
    }

    // 根据资源ID查询该资源所有的文件
    public List<Object> getAllFilesById(String resourceId) {
        return resourceFileService.getAllFilesById(resourceId);
    }

    public PreviewFile readPDF(String fileId) {
        FileEntity fileEntity = fileService.getFileById(fileId);
//        ResourceFileEntity fileEntity = resourceFileService.getResourceFileById(fileId);
//        String filePath = fileEntity.getFileEntity().getLocalPath();
        return localAllFiles(fileEntity.getLocalPath());
    }

    private PreviewFile localAllFiles(String filePath) {
        PreviewFile previewFile = new PreviewFile();
        previewFile.setFilePath(filePath);
        previewFile.setFileId(SHAUtil.SHAHashCode(filePath));
        // 判断文件是否存在
        //  拿到文件地址转换文件
        previewFileInit.saveFile(previewFile);
        pdfFileConventer.conventer(previewFile);
        return previewFile;
    }

}