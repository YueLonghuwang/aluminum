package com.rengu.project.aluminum.service;


import com.rengu.project.aluminum.ApplicationConfig;
import com.rengu.project.aluminum.entity.ChunkEntity;
import com.rengu.project.aluminum.entity.FileEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.exception.FileException;
import com.rengu.project.aluminum.repository.ChunkRepository;
import com.rengu.project.aluminum.repository.FileRepository;
import com.rengu.project.aluminum.util.FileMergeUtils;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Author: XYmar
 * Date: 2019/2/28 11:14
 */
@Slf4j
@Service
@Transactional
public class FileService {

    private final FileRepository fileRepository;
    private final ApplicationConfig applicationConfig;
    private final FileMergeUtils fileMergeUtils;
    private final ChunkRepository chunkRepository;
    private int concurrentNum = 3;
    int switchNum = concurrentNum;

    @Autowired
    public FileService(ApplicationConfig applicationConfig, FileRepository fileRepository, FileMergeUtils fileMergeUtils, ChunkRepository chunkRepository) {
        this.applicationConfig = applicationConfig;
        this.fileRepository = fileRepository;
        this.fileMergeUtils = fileMergeUtils;
        this.chunkRepository = chunkRepository;
    }

    // 根据Md5判断文件是否存在
    public boolean hasFileByMD5(String MD5) {
        System.out.println("MD5:     " + MD5);
        if (StringUtils.isEmpty(MD5)) {
            return false;
        }
        return fileRepository.existsByMD5(MD5);
    }

    // 查询当前文件块数量并返回集合数量
    public List<Integer> getChunkNumbers(ChunkEntity chunkEntity) {
        Optional<ChunkEntity> chunkEntityOptional = chunkRepository.findById(chunkEntity.getIdentifier());
        if (chunkEntityOptional.isPresent()) {
            int chunkNumbers = chunkEntityOptional.get().getChunkNumber();
            List<Integer> chunkList = new ArrayList<>();
            for (int chunkNumber = 1; chunkNumber <= chunkNumbers; chunkNumber++) {
                chunkList.add(chunkNumber);
            }
            return chunkList;
        }
        chunkRepository.save(chunkEntity);
        return null;
    }

    // 保存文件块
    public void saveChunkEntity(ChunkEntity chunkEntity, MultipartFile multipartFile) throws IOException {
        java.io.File chunk = new java.io.File(applicationConfig.getCHUNKS_SAVE_PATH() + java.io.File.separator + chunkEntity.getIdentifier() + java.io.File.separator + chunkEntity.getChunkNumber() + ".tmp");
        Optional<ChunkEntity> chunkEntityOptional = chunkRepository.findById(chunkEntity.getIdentifier());
        // 如果当前文件块不为空 存储当前文件块数量
        if (chunkEntityOptional.isPresent()) {
            chunkRepository.save(chunkEntity);
        }
        //  如果当前文件块与总文件块一致
        if (chunkEntity.getChunkNumber() == chunkEntity.getTotalChunks()) {
            chunkEntity.setSkipUpload(true);
            chunkRepository.save(chunkEntity);
        }
        chunk.getParentFile().mkdirs();
        chunk.createNewFile();
        IOUtils.copy(multipartFile.getInputStream(), new FileOutputStream(chunk));
    }

    // 根据Id删除文件
    @CacheEvict(value = "File_Cache", allEntries = true)
    public FileEntity deleteFileById(String fileId) throws IOException {
        FileEntity files = getFileById(fileId);
        FileUtils.forceDeleteOnExit(new java.io.File(files.getLocalPath()));
        fileRepository.delete(files);
        return files;
    }

    // 检查文件块是否上传过
    public boolean hasUpload(ChunkEntity chunkEntity) {
        Optional<ChunkEntity> chunkEntityOptional = chunkRepository.findById(chunkEntity.getIdentifier());
        return chunkEntityOptional.map(ChunkEntity::isSkipUpload).orElse(false);
    }



    // 检查文件块是否存在
    public boolean hasChunkEntity(ChunkEntity chunkEntity) {
        java.io.File chunk = new java.io.File(applicationConfig.getCHUNKS_SAVE_PATH() + java.io.File.separator + chunkEntity.getIdentifier() + java.io.File.separator + chunkEntity.getChunkNumber() + ".tmp");
        return chunk.exists() && chunkEntity.getChunkSize() == FileUtils.sizeOf(chunk);
    }

    // 根据Id判断文件是否存在
    public boolean hasFileById(String fileId) {
        if (StringUtils.isEmpty(fileId)) {
            return false;
        }
        return fileRepository.existsById(fileId);
    }

    // 根据Id查询文件
    @Cacheable(value = "File_Cache", key = "#fileId")
    public FileEntity getFileById(String fileId) {
        if (!hasFileById(fileId)) {
            throw new FileException(ApplicationMessageEnum.FILE_ID_NOT_FOUND);
        }
        return fileRepository.findById(fileId).get();
    }

    // 根据MD5查询文件
    public FileEntity getFileByMD5(String MD5) {
        if (!hasFileByMD5(MD5)) {
            throw new FileException(ApplicationMessageEnum.FILE_MD5_NOT_EXISTS);
        }
        return fileRepository.findByMD5(MD5).get();
    }

    // 查询所有文件
    public Page<FileEntity> getFileEntity(Pageable pageable) {
        return fileRepository.findAll(pageable);
    }


/*    private FileEntity mergeChunkEntitys(java.io.File file, ChunkEntity chunkEntity) throws IOException {
        file.delete();
        file.getParentFile().mkdirs();
        file.createNewFile();
        for (int i = 1; i <= chunkEntity.getTotalChunkEntitys(); i++) {
            java.io.File chunk = new java.io.File(applicationConfig.getCHUNKS_SAVE_PATH() + java.io.File.separator + chunkEntity.getIdentifier() + java.io.File.separator + i + ".tmp");
            if (chunk.exists()) {
                FileUtils.writeByteArrayToFile(file, FileUtils.readFileToByteArray(chunk), true);
            } else {
                throw new FileException(ApplicationMessageEnum.FILE_CHUNK_NOT_FOUND_ERROR);
            }
        }
        @Cleanup FileInputStream fileInputStream = new FileInputStream(file);
        if (!chunkEntity.getIdentifier().equals(DigestUtils.md5Hex(fileInputStream))) {
            throw new RuntimeException("文件合并失败，请检查：" + file.getAbsolutePath() + "是否正确。");
        }
        return saveFile(file);
    }*/

    // 合并文件块
    public FileEntity mergeChunkEntitys(ChunkEntity chunk) throws IOException, ExecutionException, InterruptedException {
        if (hasFileByMD5(chunk.getIdentifier())) {
            return getFileByMD5(chunk.getIdentifier());
        } else {
            java.io.File file = null;
            String extension = FilenameUtils.getExtension(chunk.getFilename());
            if (StringUtils.isEmpty(extension)) {
                file = new java.io.File(applicationConfig.getFILES_SAVE_PATH() + java.io.File.separator + chunk.getIdentifier());
            } else {
                file = new java.io.File(applicationConfig.getFILES_SAVE_PATH() + java.io.File.separator + chunk.getIdentifier() + "." + FilenameUtils.getExtension(chunk.getFilename()));
            }
            return mergeChunkEntitys(file, chunk);
        }
    }

    // 保存文件信息
    @CacheEvict(value = "File_Cache", allEntries = true)
    public FileEntity saveFile(java.io.File file) throws IOException {
        FileEntity filesEntity = new FileEntity();
        @Cleanup FileInputStream fileInputStream = new FileInputStream(file);
        String MD5 = DigestUtils.md5Hex(fileInputStream);
        if (hasFileByMD5(MD5)) {
                return getFileByMD5(MD5);
        }
        log.warn("现在是在保存文件信息!!!!!");
        filesEntity.setMD5(MD5);         // MD5
        filesEntity.setPostfix(FilenameUtils.getExtension(file.getName()));                // 后缀
        filesEntity.setFileSize(FileUtils.sizeOf(file));                                   // 大小
        filesEntity.setLocalPath(file.getAbsolutePath());                                  // 路径
        return fileRepository.save(filesEntity);
    }

    private FileEntity mergeChunkEntitys(File file, ChunkEntity chunkEntity) throws IOException, ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();
        file.delete();
        file.getParentFile().mkdirs();
        file.createNewFile();
        if (chunkEntity.getTotalChunks() >= switchNum) {
            int last = chunkEntity.getTotalChunks() % concurrentNum;
            int baseStep = (chunkEntity.getTotalChunks() - last) / concurrentNum;
            int startPoint = 1;
            Map<Integer, CompletableFuture<File>> fileMap = new HashMap<>();
            for (int i = 1; i <= concurrentNum; i++) {
                int step = baseStep;
                if (i == concurrentNum) {
                    step = step + last;
                }
                fileMap.put(i, fileMergeUtils.megeChunks(startPoint, startPoint + step - 1, chunkEntity));
                startPoint = startPoint + step;
            }
            FileOutputStream fileOutputStream = FileUtils.openOutputStream(file, true);
            for (int i = 1; i <= concurrentNum; i++) {
                File block = fileMap.get(i).get();
                if (block.exists()) {
                    IOUtils.copy(FileUtils.openInputStream(block), fileOutputStream);
//                    FileUtils.writeByteArrayToFile(file, FileUtils.readFileToByteArray(block), true);
                } else {
                    IOUtils.closeQuietly(fileOutputStream);
                    throw new FileException(ApplicationMessageEnum.CHUNK_NOT_EXISTS);
                }
            }
            IOUtils.closeQuietly(fileOutputStream);
        } else {
            for (int i = 1; i <= chunkEntity.getTotalChunks(); i++) {
                java.io.File chunk = new java.io.File(applicationConfig.getCHUNKS_SAVE_PATH() + java.io.File.separator + chunkEntity.getIdentifier() + java.io.File.separator + i + ".tmp");
                if (chunk.exists()) {
                    FileUtils.writeByteArrayToFile(file, FileUtils.readFileToByteArray(chunk), true);
                } else {
                    throw new FileException(ApplicationMessageEnum.CHUNK_NOT_EXISTS);
                }
            }
        }
        log.info(file.getAbsolutePath() + "合成完毕！总大小：" + FileUtils.sizeOf(file) + ",开始时间：" + startTime + ",耗时：" + (System.currentTimeMillis() - startTime));
        return saveFile(file);
    }
}
