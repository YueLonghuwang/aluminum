package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.ApplicationConfig;
import com.rengu.project.aluminum.entity.FileEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.exception.FileException;
import com.rengu.project.aluminum.repository.FileRepository;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
public class FileService {

    private final FileRepository fileRepository;
    private final ApplicationConfig applicationConfig;

    public FileService(FileRepository fileRepository, ApplicationConfig applicationConfig) {
        this.fileRepository = fileRepository;
        this.applicationConfig = applicationConfig;
    }

    // 保存文件信息
    public FileEntity saveFile(File file) throws IOException {
        FileEntity fileEntity = new FileEntity();
        @Cleanup FileInputStream fileInputStream = new FileInputStream(file);
        String md5 = DigestUtils.md5Hex(fileInputStream);
        if (hasFileByMd5(md5)) {
            throw new FileException(ApplicationMessageEnum.FILE_MD5_NOT_EXISTS);
        }
        fileEntity.setMD5(md5);
        fileEntity.setType(FilenameUtils.getExtension(file.getName()));
        fileEntity.setSize(FileUtils.sizeOf(file));
        fileEntity.setLocalPath(file.getAbsolutePath());
        return fileRepository.save(fileEntity);
    }

    // 根据Id删除文件
    public FileEntity deleteFileById(String fileId) throws IOException {
        FileEntity fileEntity = getFileById(fileId);
        FileUtils.forceDeleteOnExit(new File(fileEntity.getLocalPath()));
        return fileEntity;
    }

    // 根据md5查询文件是否存在
    @Cacheable(value = "file_cache", key = "#md5")
    public FileEntity getFileByMd5(String md5) {
        if (StringUtils.isEmpty(md5)) {
            throw new FileException(ApplicationMessageEnum.FILE_MD5_NOT_FOUND);
        }
        Optional<FileEntity> fileEntityOptional = fileRepository.findByMD5(md5);
        if (!fileEntityOptional.isPresent()) {
            throw new FileException(ApplicationMessageEnum.FILE_MD5_NOT_EXISTS);
        }
        return fileEntityOptional.get();
    }

    // 根据Id查询文件
    public FileEntity getFileById(String fileId) {
        if (StringUtils.isEmpty(fileId)) {
            throw new FileException(ApplicationMessageEnum.FILE_ID_NOT_FOUND);
        }
        Optional<FileEntity> fileEntityOptional = fileRepository.findById(fileId);
        if (!fileEntityOptional.isPresent()) {
            throw new FileException(ApplicationMessageEnum.FILE_ID_NOT_EXISTS);
        }
        return fileEntityOptional.get();
    }

    // 根据md5查询文件是否存在
    public boolean hasFileByMd5(String md5) {
        if (StringUtils.isEmpty(md5)) {
            return false;
        }
        return fileRepository.existsByMD5(md5);
    }
}
