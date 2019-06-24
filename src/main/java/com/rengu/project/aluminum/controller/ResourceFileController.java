package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.FileMetaEntity;
import com.rengu.project.aluminum.entity.ResourceFileEntity;
import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.service.ResourceFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/**
 * com.rengu.project.aluminum.controller
 *
 * @author hanchangming
 * @date 2019-06-11
 */

@Slf4j
@RestController
@RequestMapping(value = "/resource-files")
public class ResourceFileController {

    private final ResourceFileService resourceFileService;

    public ResourceFileController(ResourceFileService resourceFileService) {
        this.resourceFileService = resourceFileService;
    }

    // 根据资源Id上传文件
    @PostMapping(value = "/{resourceId}/upload/files")
    public ResultEntity<Set<ResourceFileEntity>> uploadFiles(@PathVariable(value = "resourceId") String resourceId, @RequestBody String parentNodeId, @RequestBody List<FileMetaEntity> fileMetaEntityList) {
        return new ResultEntity<>(resourceFileService.uploadFiles(resourceId, parentNodeId, fileMetaEntityList));
    }

    // 根据资源Id创建文件夹
    @PostMapping(value = "/{resourceId}/create/folder")
    public ResultEntity<ResourceFileEntity> createFolder(@PathVariable(value = "resourceId") String resourceId, @RequestParam(value = "parentNodeId", required = false) String parentNodeId, ResourceFileEntity resourceFileEntity) {
        return new ResultEntity<>(resourceFileService.createFolder(resourceId, parentNodeId, resourceFileEntity));
    }

    // 根据Id删除资源文件
    @DeleteMapping(value = "/{resourceFileId}")
    public ResultEntity<ResourceFileEntity> deleteResourceFileById(@PathVariable(value = "resourceFileId") String resourceFileId) throws IOException {
        return new ResultEntity<>(resourceFileService.deleteResourceFileById(resourceFileId));
    }

    // 根据Id修改资源文件
    @PatchMapping(value = "/{resourceFileId}")
    public ResultEntity<ResourceFileEntity> updateResourceFileById(@PathVariable(value = "resourceFileId") String resourceFileId, ResourceFileEntity resourceFileEntity) {
        return new ResultEntity<>(resourceFileService.updateResourceFileById(resourceFileId, resourceFileEntity));
    }

    // 根据资源id和父节点查询自节点
    @GetMapping(value = "/{resourceFileId}/download")
    public void downloadResourceFileByResourceIdAndParentNode(HttpServletResponse httpServletResponse, @PathVariable(value = "resourceFileId") String resourceFileId) throws IOException {
        File compressFile = resourceFileService.downloadResourceFileByResourceFileId(resourceFileId);
        String mimeType = URLConnection.guessContentTypeFromName(compressFile.getName()) == null ? "application/octet-stream" : URLConnection.guessContentTypeFromName(compressFile.getName());
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(compressFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        httpServletResponse.setContentLengthLong(compressFile.length());
        // 文件流输出
        IOUtils.copy(new FileInputStream(compressFile), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
    }

    // 根据资源id和父节点查询自节点
    @GetMapping(value = "/{resourceId}/files")
    public ResultEntity<Set<ResourceFileEntity>> getResourceFileByParentNodeIdAndResourceId(@PathVariable(value = "resourceId") String resourceId, @RequestParam(value = "parentNodeId") String parentNodeId) {
        ResourceFileEntity resourceFileEntity = resourceFileService.hasResourceFileById(parentNodeId) ? resourceFileService.getResourceFileById(parentNodeId) : null;
        return new ResultEntity<>(resourceFileService.getResourceFileByParentNodeAndResourceId(resourceFileEntity, resourceId));
    }
}
