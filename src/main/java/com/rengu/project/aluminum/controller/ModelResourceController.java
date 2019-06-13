package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.ModelResourceEntity;
import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.service.ModelResourceService;
import com.rengu.project.aluminum.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * author : yaojiahao
 * Date: 2019/6/13 14:22
 **/

@Slf4j
@RestController
@RequestMapping(value = "/modelResource")
public class ModelResourceController {
    private final ModelResourceService modelResourceService;
    private final UserService userService;

    public ModelResourceController(ModelResourceService modelResourceService, UserService userService) {
        this.modelResourceService = modelResourceService;
        this.userService = userService;
    }

    // 保存标准规范
    @PostMapping
    public ResultEntity<ModelResourceEntity> saveResource(@AuthenticationPrincipal String username, ModelResourceEntity modelResourceEntity) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(modelResourceService.saveResource(modelResourceEntity, userEntity));
    }

    // 根据ID删除标准规范
    @DeleteMapping(value = "/{modelResourceId}")
    public ResultEntity<ModelResourceEntity> deleteResourceById(@AuthenticationPrincipal String username, @PathVariable(value = "modelResourceId") String modelResourceId) throws IOException {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(modelResourceService.deleteResourceById(modelResourceId, userEntity));
    }

    // 根据ID修改标准规范
    @PatchMapping(value = "/{modelResourceId}")
    public ResultEntity<ModelResourceEntity> updateResourceById(@AuthenticationPrincipal String username, @PathVariable(value = "modelResourceId") String modelResourceId, ModelResourceEntity modelResourceEntity) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(modelResourceService.updateResourceById(modelResourceId, modelResourceEntity, userEntity));
    }

    // 根据ID修查询准规范
    @GetMapping(value = "/{modelResourceId}")
    public ResultEntity<ModelResourceEntity> getResourceById(@AuthenticationPrincipal String username, @PathVariable(value = "modelResourceId") String modelResourceId) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(modelResourceService.getResourceById(modelResourceId, userEntity));
    }

    // 根据ID修查询准规范
    @GetMapping(value = "/{modelResourceId}/download")
    public void downloadResourceById(HttpServletResponse httpServletResponse, @AuthenticationPrincipal String username, @PathVariable(value = "modelResourceId") String modelResourceId) throws IOException {
        UserEntity userEntity = userService.getUserByUsername(username);
        File compressFile = modelResourceService.downloadResourceById(modelResourceId, userEntity);
        String mimeType = URLConnection.guessContentTypeFromName(compressFile.getName()) == null ? "application/octet-stream" : URLConnection.guessContentTypeFromName(compressFile.getName());
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(compressFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        httpServletResponse.setContentLengthLong(compressFile.length());
        // 文件流输出
        IOUtils.copy(new FileInputStream(compressFile), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
    }

    // 根据ID修查询准规范
    @GetMapping(value = "/by/user")
    public ResultEntity<Page<ModelResourceEntity>> getResourcesByUser(@AuthenticationPrincipal String username, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(modelResourceService.getResourcesByUser(pageable, userEntity));
    }

    // 根据ID修查询准规范
    @GetMapping
    public ResultEntity<Page<ModelResourceEntity>> getResources(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResultEntity<>(modelResourceService.getResources(pageable));
    }
}
