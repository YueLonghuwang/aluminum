package com.rengu.project.aluminum.util;

import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.exception.CompressException;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;

/**
 * com.rengu.project.aluminum.util
 *
 * @author hanchangming
 * @date 2019-06-12
 */

@Slf4j
public class CompressUtils {

    private static final String ZIP = "zip";

    // 压缩方法
    public static File compress(File srcDir, File compressFile) throws IOException {
        if (!srcDir.isDirectory()) {
            throw new CompressException(ApplicationMessageEnum.COMPRESS_FILE_TYPE_ERROR);
        }
        String extension = FilenameUtils.getExtension(compressFile.getName());
        switch (extension) {
            case ZIP:
                return compressZip(srcDir, compressFile);
            default:
                throw new CompressException(ApplicationMessageEnum.COMPRESS_FILE_TYPE_ERROR);
        }
    }

    // 解压方法
    public static File decompress(File compressFile, File outputDir) throws ZipException {
        String extension = FilenameUtils.getExtension(compressFile.getName());
        switch (extension) {
            case ZIP:
                return decompressZip(compressFile, outputDir);
            default:
                throw new CompressException(ApplicationMessageEnum.COMPRESS_FILE_TYPE_ERROR);
        }
    }

    private static File compressZip(File srcDir, File compressFile) throws IOException {
        String tempFolderPath = srcDir.getPath();
        ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(compressFile);
        Collection<File> fileCollection = FileUtils.listFiles(srcDir, null, true);
        for (File file : fileCollection) {
            FileInputStream fileInputStream = new FileInputStream(file);
            ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, file.getPath().replace(tempFolderPath, FilenameUtils.getBaseName(compressFile.getName())));
            zipArchiveOutputStream.putArchiveEntry(zipArchiveEntry);
            IOUtils.copy(fileInputStream, zipArchiveOutputStream);
            zipArchiveOutputStream.closeArchiveEntry();
            fileInputStream.close();
        }
        zipArchiveOutputStream.finish();
        zipArchiveOutputStream.close();
        return compressFile;
    }

    private static File decompressZip(File compressFile, File outputDir) throws ZipException {
        // 首先创建ZipFile指向磁盘上的.zip文件
        ZipFile zipFile = new ZipFile(compressFile);
        if (!zipFile.isValidZipFile()) {
            // 验证.zip文件是否合法，包括文件是否存在、是否为zip文件、是否被损坏等
            throw new ZipException("压缩文件验证失败，解压缩失败.");
        }
        if (outputDir.isDirectory() && !outputDir.exists()) {
            outputDir.mkdir();
        }
        // 将文件抽出到解压目录(解压)
        zipFile.extractAll(outputDir.getAbsolutePath());
        return outputDir;
    }
}
