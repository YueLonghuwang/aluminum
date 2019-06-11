package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.ApplicationConfig;
import com.rengu.project.aluminum.entity.ChunkEntity;
import com.rengu.project.aluminum.entity.FileEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.exception.ChunkException;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * com.rengu.project.aluminum.service
 *
 * @author hanchangming
 * @date 2019-06-10
 */

@Slf4j
@Service
public class ChunkService {

    private final ApplicationConfig applicationConfig;
    private final FileService fileService;

    public ChunkService(ApplicationConfig applicationConfig, FileService fileService) {
        this.applicationConfig = applicationConfig;
        this.fileService = fileService;
    }

    // 保存文件块
    public ChunkEntity saveChunk(ChunkEntity chunkEntity, MultipartFile multipartFile) throws IOException {
        File chunkFile = getChunkFile(chunkEntity);
        chunkFile.getParentFile().mkdirs();
        chunkFile.createNewFile();
        @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(chunkFile);
        IOUtils.copy(multipartFile.getInputStream(), fileOutputStream);
        return chunkEntity;
    }

    // 合并文件块
    public FileEntity mergeChunks(ChunkEntity chunkEntity) throws IOException {
        String extension = FilenameUtils.getExtension(chunkEntity.getFilename());
        File file = StringUtils.isEmpty(extension) ? new File(applicationConfig.getFILES_SAVE_PATH() + File.separator + chunkEntity.getIdentifier()) : new File(applicationConfig.getFILES_SAVE_PATH() + File.separator + chunkEntity.getIdentifier() + "." + extension);
        file.getParentFile().mkdirs();
        file.createNewFile();
        // 合并文件块
        for (int i = 1; i <= chunkEntity.getTotalChunks(); i++) {
            File chunkFile = getChunkFile(chunkEntity);
            if (chunkFile.exists()) {
                FileUtils.writeByteArrayToFile(file, FileUtils.readFileToByteArray(chunkFile), true);
            } else {
                throw new ChunkException(ApplicationMessageEnum.CHUNK_NOT_EXISTS);
            }
        }
        // 校验MD5
        @Cleanup FileInputStream fileInputStream = new FileInputStream(file);
        if (!chunkEntity.getIdentifier().equals(DigestUtils.md5Hex(fileInputStream))) {
            throw new ChunkException(ApplicationMessageEnum.CHUNK_NOT_EXISTS);
        }
        return fileService.saveFile(file);
    }

    // 检查文件块是否存在
    public boolean hasChunk(ChunkEntity chunkEntity) {
        File chunkFile = getChunkFile(chunkEntity);
        return chunkFile.exists() && chunkEntity.getChunkSize() == FileUtils.sizeOf(chunkFile);
    }

    private File getChunkFile(ChunkEntity chunkEntity) {
        return new File(applicationConfig.getCHUNKS_SAVE_PATH() + File.separator + chunkEntity.getIdentifier() + File.separator + chunkEntity.getChunkNumber() + ".tmp");
    }
}
