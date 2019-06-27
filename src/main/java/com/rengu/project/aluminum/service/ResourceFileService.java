package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.entity.FileEntity;
import com.rengu.project.aluminum.entity.FileMetaEntity;
import com.rengu.project.aluminum.entity.ResourceFileEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.exception.FileException;
import com.rengu.project.aluminum.exception.ResourceFileException;
import com.rengu.project.aluminum.repository.FileRepository;
import com.rengu.project.aluminum.repository.ResourceFileRepository;
import com.rengu.project.aluminum.util.CompressUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * com.rengu.project.aluminum.service
 *
 * @author hanchangming
 * @date 2019-06-11
 */

@Slf4j
@Service
@Transactional
public class ResourceFileService {

    private final ResourceFileRepository resourceFileRepository;
    private final FileService fileService;
    private final FileRepository fileRepository;

    public ResourceFileService(ResourceFileRepository resourceFileRepository, FileService fileService, FileRepository fileRepository) {
        this.resourceFileRepository = resourceFileRepository;
        this.fileService = fileService;
        this.fileRepository = fileRepository;
    }

    // 根据资源ID查询文件
    public boolean existsByResourceId(String resourceId) {
        return resourceFileRepository.existsByResourceId(resourceId);
    }

    // 根据资源Id上传文件
    public Set<ResourceFileEntity> uploadFiles(String resourceId, String parentNodeId, List<FileMetaEntity> fileMetaEntities) throws InterruptedException {
        Thread.sleep(500);
        Set<ResourceFileEntity> resourceFileEntitySet = new HashSet<>();
        String splitter = File.separator.replace("\\", "\\\\");
        for (FileMetaEntity fileMetaEntity : fileMetaEntities) {
            ResourceFileEntity parentNode = hasResourceFileById(parentNodeId) ? getResourceFileById(parentNodeId) : null;
            String[] paths;
            if (FilenameUtils.separatorsToSystem(fileMetaEntity.getRelativePath()).contains(File.separator)) {
                paths = FilenameUtils.separatorsToSystem(fileMetaEntity.getRelativePath()).split(splitter);
            } else {
                paths = new String[1];
                paths[0] = fileMetaEntity.getRelativePath();
            }
            for (int i = 0; i < paths.length; i++) {
                String path = paths[i];
                if (!StringUtils.isEmpty(path)) {
                    if (i == paths.length - 1) {
                        // 最后一级路径-文件
                        ResourceFileEntity resourceFileEntity;
                        // FilenameUtils.getBaseName 文件名 FilenameUtils.getExtension文件后缀名
                        if (hasResourceFileByNameAndExtensionAndParentNodeAndResourceIdAndFolder(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, resourceId, false)) {
                            resourceFileEntity = getResourceFileByNameAndExtensionAndParentNodeAndResourceIdAndFolder(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, resourceId, false);
                            resourceFileEntity.setCreateTime(new Date());
                        } else {
                            resourceFileEntity = new ResourceFileEntity();
                            resourceFileEntity.setFolder(false);
                            resourceFileEntity.setParentNode(parentNode);
                            resourceFileEntity.setResourceId(resourceId);
                        }
                        resourceFileEntity.setName(FilenameUtils.getBaseName(path));
                        resourceFileEntity.setExtension(FilenameUtils.getExtension(path));
                        resourceFileEntity.setFileEntity(getFileByMD5(fileMetaEntity.getFileId()));
                        resourceFileEntitySet.add(resourceFileRepository.save(resourceFileEntity));
                    } else {
                        // 中间路径-文件夹
                        if (hasResourceFileByNameAndExtensionAndParentNodeAndResourceIdAndFolder(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, resourceId, true)) {
                            parentNode = getResourceFileByNameAndExtensionAndParentNodeAndResourceIdAndFolder(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, resourceId, true);
                        } else {
                            ResourceFileEntity resourceFileEntity = new ResourceFileEntity();
                            resourceFileEntity.setName(FilenameUtils.getBaseName(path));
                            resourceFileEntity.setExtension(FilenameUtils.getExtension(path));
                            resourceFileEntity.setFolder(true);
                            resourceFileEntity.setResourceId(resourceId);
                            resourceFileEntity.setParentNode(parentNode);
                            parentNode = resourceFileEntity;
                            resourceFileEntitySet.add(resourceFileRepository.save(resourceFileEntity));
                        }
                    }
                }
            }
        }
        return resourceFileEntitySet;
    }

    // 查询md5是否存在
    public FileEntity getFileByMD5(String MD5) throws InterruptedException {
        log.info(MD5);
        if (!fileRepository.findByMD5(MD5).isPresent()) {
            throw new FileException(ApplicationMessageEnum.FILE_MD5_NOT_EXISTS);
        }
        return fileRepository.findByMD5(MD5).get();
    }

    // 根据资源Id创建文件夹
    public ResourceFileEntity createFolder(String resourceId, String parentNodeId, ResourceFileEntity resourceFileArgs) {
        if (StringUtils.isEmpty(resourceFileArgs.getName())) {
            throw new ResourceFileException(ApplicationMessageEnum.RESOURCE_FILE_NAME_NOT_FOUND);
        }
        ResourceFileEntity parentNode = hasResourceFileById(parentNodeId) ? getResourceFileById(parentNodeId) : null;
        if (hasResourceFileByNameAndExtensionAndParentNodeAndResourceIdAndFolder(resourceFileArgs.getName(), null, parentNode, resourceId, true)) {
            throw new ResourceFileException(ApplicationMessageEnum.RESOURCE_FILE_EXISTS);
        }
        ResourceFileEntity resourceFileEntity = new ResourceFileEntity();
        resourceFileEntity.setName(resourceFileArgs.getName());
        resourceFileEntity.setFolder(true);
        resourceFileEntity.setResourceId(resourceId);
        resourceFileEntity.setParentNode(parentNode);
        return resourceFileRepository.save(resourceFileEntity);
    }

    // 根据资源ID删除资源文件
    public Set<ResourceFileEntity> deleteResourceFileByResourceId(String resourceId) throws IOException {
        Set<ResourceFileEntity> resourceFileEntitySet = getResourceFileByParentNodeAndResourceId(null, resourceId);
        for (ResourceFileEntity resourceFileEntity : resourceFileEntitySet) {
            deleteResourceFile(resourceFileEntity);
        }
        return resourceFileEntitySet;
    }

    // 根据ID删除资源文件
    public ResourceFileEntity deleteResourceFileById(String resourceFileId) throws IOException {
        ResourceFileEntity resourceFileEntity = getResourceFileById(resourceFileId);
        deleteResourceFile(resourceFileEntity);
        return resourceFileEntity;
    }

    public ResourceFileEntity deleteResourceFile(ResourceFileEntity resourceFileEntity) throws IOException {
        if (resourceFileEntity.isFolder()) {
            // 是文件夹, 获取子文件遍历递归
            for (ResourceFileEntity tempComponentFile : getResourceFileByParentNodeAndResourceId(resourceFileEntity, resourceFileEntity.getResourceId())) {
                deleteResourceFile(tempComponentFile);
            }
            resourceFileRepository.deleteById(resourceFileEntity.getId());
        } else {
            resourceFileRepository.deleteById(resourceFileEntity.getId());
            // 是文件，检查是否需要删除实际文件
            if (!hasResourceFileByFile(resourceFileEntity.getFileEntity())) {
                fileService.deleteFileById(resourceFileEntity.getFileEntity().getId());
            }
        }
        return resourceFileEntity;
    }

    // 根据ID修改资源文件
    public ResourceFileEntity updateResourceFileById(String resourceFileId, ResourceFileEntity resourceFileArgs) {
        ResourceFileEntity resourceFileEntity = getResourceFileById(resourceFileId);
        if (StringUtils.isEmpty(resourceFileArgs.getName())) {
            throw new ResourceFileException(ApplicationMessageEnum.RESOURCE_FILE_NAME_NOT_FOUND);
        }
        if (!resourceFileEntity.getName().equals(resourceFileArgs.getName())) {
            if (hasResourceFileByNameAndExtensionAndParentNodeAndResourceIdAndFolder(resourceFileArgs.getName(), resourceFileEntity.getExtension(), resourceFileEntity.getParentNode(), resourceFileEntity.getResourceId(), resourceFileEntity.isFolder())) {
                throw new ResourceFileException(ApplicationMessageEnum.RESOURCE_FILE_NAME_EXISTS);
            } else {
                resourceFileEntity.setName(resourceFileArgs.getName());
            }
        }
        return resourceFileRepository.save(resourceFileEntity);
    }

    // 根据id查询资源文件
    public ResourceFileEntity getResourceFileById(String resourceFileId) {
        if (StringUtils.isEmpty(resourceFileId)) {
            throw new ResourceFileException(ApplicationMessageEnum.RESOURCE_FILE_ID_NOT_FOUND);
        }
        Optional<ResourceFileEntity> resourceFileEntityOptional = resourceFileRepository.findById(resourceFileId);
        if (!resourceFileEntityOptional.isPresent()) {
            throw new ResourceFileException(ApplicationMessageEnum.RESOURCE_FILE_ID_NOT_EXISTS);
        }
        return resourceFileEntityOptional.get();
    }

    // 根据名称、拓展名、父节点、资源id查询资源文件
    public ResourceFileEntity getResourceFileByNameAndExtensionAndParentNodeAndResourceIdAndFolder(String name, String extension, ResourceFileEntity parentNode, String resourceId, boolean isFolder) {
        if (StringUtils.isEmpty(name)) {
            throw new ResourceFileException(ApplicationMessageEnum.RESOURCE_FILE_NAME_NOT_FOUND);
        }
        if (StringUtils.isEmpty(resourceId)) {
            throw new ResourceFileException(ApplicationMessageEnum.RESOURCE_FILE_RESOURCE_ID_NOT_FOUND);
        }
        Optional<ResourceFileEntity> resourceFileEntityOptional = resourceFileRepository.findByNameAndExtensionAndParentNodeAndResourceIdAndFolderEquals(name, extension, parentNode, resourceId, isFolder);
        if (!resourceFileEntityOptional.isPresent()) {
            throw new ResourceFileException(ApplicationMessageEnum.RESOURCE_FILE_NOT_EXISTS);
        }
        return resourceFileEntityOptional.get();
    }

    // 根据资源Id和父节点查询
    public Set<ResourceFileEntity> getResourceFileByParentNodeAndResourceId(ResourceFileEntity parentNode, String resourceId) {
        if (StringUtils.isEmpty(resourceId)) {
            throw new ResourceFileException(ApplicationMessageEnum.RESOURCE_FILE_RESOURCE_ID_NOT_FOUND);
        }
        return resourceFileRepository.findByParentNodeAndResourceId(parentNode, resourceId);
    }

    // 根据资源导出
    public File downloadResourceFileByResourceFileId(String resourceFileId) throws IOException {
        ResourceFileEntity resourceFileEntity = getResourceFileById(resourceFileId);
        File exportDir = new File(FileUtils.getTempDirectoryPath() + File.separatorChar + UUID.randomUUID().toString() + File.separatorChar + resourceFileEntity.getName());
        if (resourceFileEntity.isFolder()) {
            Set<ResourceFileEntity> resourceFileEntitySet = getResourceFileByParentNodeAndResourceId(resourceFileEntity, resourceFileEntity.getResourceId());
            for (ResourceFileEntity tempNode : resourceFileEntitySet) {
                exportResourceFile(tempNode, exportDir);
            }
        } else {
            exportResourceFile(resourceFileEntity, exportDir);
        }
        File compressFile = new File(FileUtils.getTempDirectoryPath() + File.separatorChar + resourceFileEntity.getName() + ".zip");
        return CompressUtils.compress(exportDir, compressFile);
    }

    // 根据资源导出
    public File downloadResourceFileByResourceId(String resourceId) throws IOException {
        Set<ResourceFileEntity> resourceFileEntitySet = getResourceFileByParentNodeAndResourceId(null, resourceId);
        File exportDir = new File(FileUtils.getTempDirectoryPath() + File.separatorChar + UUID.randomUUID().toString());
        exportDir.mkdirs();
        for (ResourceFileEntity resourceFileEntity : resourceFileEntitySet) {
            exportResourceFile(resourceFileEntity, exportDir);
        }
        File compressFile = new File(FileUtils.getTempDirectoryPath() + File.separatorChar + resourceId + ".zip");
        return CompressUtils.compress(exportDir, compressFile);
    }

    // 根据资源文件Id判断是否存在
    public boolean hasResourceFileById(String resourceFileId) {
        if (StringUtils.isEmpty(resourceFileId)) {
            return false;
        }
        return resourceFileRepository.existsById(resourceFileId);
    }

    // 根据名称、拓展名、父节点、资源id判断是否存在
    public boolean hasResourceFileByNameAndExtensionAndParentNodeAndResourceIdAndFolder(String name, String extension, ResourceFileEntity parentNode, String resourceId, boolean isFolder) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(resourceId)) {
            return false;
        }
        return resourceFileRepository.existsByNameAndExtensionAndParentNodeAndResourceIdAndFolderEquals(name, extension, parentNode, resourceId, isFolder);
    }

    // 根据文件查询资源文件是否存在
    public boolean hasResourceFileByFile(FileEntity fileEntity) {
        return resourceFileRepository.existsByFileEntity(fileEntity);
    }

    // 下载文件
    public File exportResourceFile(ResourceFileEntity resourceFileEntity, File exportDir) throws IOException {
        // 检查是否为文件夹
        if (resourceFileEntity.isFolder()) {
            for (ResourceFileEntity temp : getResourceFileByParentNodeAndResourceId(resourceFileEntity, resourceFileEntity.getResourceId())) {
                exportResourceFile(temp, exportDir);
            }
        } else {
            File file = new File(exportDir.getAbsolutePath() + File.separatorChar + getResourceFileRelativePath(resourceFileEntity, ""));
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileUtils.copyFile(new File(resourceFileEntity.getFileEntity().getLocalPath()), file);
        }
        return exportDir;
    }

    // 递归拼接path信息
    private String getResourceFileRelativePath(ResourceFileEntity resourceFileEntity, String basePath) {
        if (basePath.isEmpty()) {
            if (resourceFileEntity.isFolder()) {
                basePath = File.separatorChar + resourceFileEntity.getName() + File.separatorChar;
            } else {
                basePath = File.separatorChar + resourceFileEntity.getName() + "." + resourceFileEntity.getExtension();
            }
        }
        while (resourceFileEntity.getParentNode() != null) {
            resourceFileEntity = resourceFileEntity.getParentNode();
            basePath = File.separatorChar + resourceFileEntity.getName() + basePath;
            getResourceFileRelativePath(resourceFileEntity, basePath);
        }
        return FilenameUtils.separatorsToSystem(basePath);
    }

}
