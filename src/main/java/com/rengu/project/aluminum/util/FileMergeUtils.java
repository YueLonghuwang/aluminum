package com.rengu.project.aluminum.util;

import com.rengu.project.aluminum.ApplicationConfig;
import com.rengu.project.aluminum.entity.ChunkEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * author : yaojiahao
 * Date: 2019/6/14 18:34
 **/

@Slf4j
@Component
public class FileMergeUtils {

    private final ApplicationConfig applicationConfig;

    public FileMergeUtils(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Async
    public CompletableFuture<File> megeChunks(int start, int end, ChunkEntity chunk) throws IOException {
        long startTime = System.currentTimeMillis();
        log.info("开启多线程ing");
        File blockFile = new File(applicationConfig.getCHUNKS_SAVE_PATH() + File.separator + chunk.getIdentifier() + File.separator + start + ".block");
        blockFile.delete();
        blockFile.getParentFile().mkdirs();
        blockFile.createNewFile();
        for (int i = start; i <= end; i++) {
            File chunkFile = new File(applicationConfig.getCHUNKS_SAVE_PATH() + File.separator + chunk.getIdentifier() + File.separator + i + ".tmp");
            if (chunkFile.exists()) {
                FileUtils.writeByteArrayToFile(blockFile, FileUtils.readFileToByteArray(chunkFile), true);
            } else {
                throw new FileException(ApplicationMessageEnum.CHUNK_NOT_EXISTS);
            }
        }
        log.info(chunk.getFilename() + "多线程合并，start：" + start + ",end:" + end + "结束,耗时：" + (System.currentTimeMillis() - startTime) + ",开始时间：" + startTime);
        return CompletableFuture.completedFuture(blockFile);
    }
}
