package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.ModelResourceEntity;
import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.repository.ModelResourceRepository;
import com.rengu.project.aluminum.service.ModelResourceService;
import com.rengu.project.aluminum.service.UserService;
import com.rengu.project.aluminum.specification.Filter;
import lombok.Cleanup;
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
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import static com.rengu.project.aluminum.specification.SpecificationBuilder.selectFrom;

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
    private final ModelResourceRepository modelResourceRepository;

    public ModelResourceController(ModelResourceService modelResourceService, UserService userService, ModelResourceRepository modelResourceRepository) {
        this.modelResourceService = modelResourceService;
        this.userService = userService;
        this.modelResourceRepository = modelResourceRepository;
    }

    // 保存模型资源
    @PostMapping
    public ResultEntity<ModelResourceEntity> saveResource(@AuthenticationPrincipal String username, ModelResourceEntity modelResourceEntity) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(modelResourceService.saveResource(modelResourceEntity, userEntity));
    }

    // 根据关键字查询
    @PostMapping("/KeyWord")
    public ResultEntity findByKeyWord(@RequestBody Filter filter) {
        return new ResultEntity(selectFrom(modelResourceRepository).where(filter).findAll());
    }

    // 根据ID删除模型资源   *
    @DeleteMapping(value = "/{modelResourceId}")
    public ResultEntity<ModelResourceEntity> deleteResourceById(@AuthenticationPrincipal String username, @PathVariable(value = "modelResourceId") String modelResourceId) throws IOException {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(modelResourceService.deleteResourceById(modelResourceId, userEntity));
    }

    // 根据ID修改模型资源   *
    @PatchMapping(value = "/{modelResourceId}")
    public ResultEntity<ModelResourceEntity> updateResourceById(@AuthenticationPrincipal String username, @PathVariable(value = "modelResourceId") String modelResourceId, ModelResourceEntity modelResourceEntity) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(modelResourceService.updateResourceById(modelResourceId, modelResourceEntity, userEntity));
    }

    // 通过资源Id获取资源   *
    @GetMapping(value = "/{modelResourceId}")
    public ResultEntity<ModelResourceEntity> getResourceById(@AuthenticationPrincipal String username, @PathVariable(value = "modelResourceId") String modelResourceId) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(modelResourceService.getResourceById(modelResourceId, userEntity));
    }

    // 通过资源Id下载资源
    @GetMapping(value = "/{userId}/{modelResourceId}/download")
    public void downloadResourceById(HttpServletResponse httpServletResponse, @PathVariable(value = "userId") String userId, @PathVariable(value = "modelResourceId") String modelResourceId) throws IOException {
        UserEntity userEntity = userService.getUserById(userId);
        File compressFile = modelResourceService.downloadResourceById(modelResourceId, userEntity);
        String mimeType = URLConnection.guessContentTypeFromName(compressFile.getName()) == null ? "application/octet-stream" : URLConnection.guessContentTypeFromName(compressFile.getName());
        // 文件流输出
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(compressFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        httpServletResponse.setContentLengthLong(compressFile.length());

        // 文件流输出
        @Cleanup FileInputStream fileInputStream = new FileInputStream(compressFile);
        @Cleanup OutputStream outputStream = httpServletResponse.getOutputStream();
//        printFile(fileInputStream,outputStream);
        IOUtils.copy(fileInputStream, outputStream);
        httpServletResponse.flushBuffer();
    }

    // 通过用户获取资源
    @GetMapping(value = "/by/user")
    public ResultEntity<Page<ModelResourceEntity>> getResourcesByUser(@AuthenticationPrincipal String username, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(modelResourceService.getResourcesByUser(pageable, userEntity));
    }


    @GetMapping
    public ResultEntity<Page<ModelResourceEntity>> getResources(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResultEntity<>(modelResourceService.getResources(pageable));
    }

    // 入库
    @PostMapping("/putInStorage")
    public ResultEntity<ModelResourceEntity> putInStorage(ModelResourceEntity modelResourceEntity) {
        return new ResultEntity<>(modelResourceService.putInStorage(modelResourceEntity));
    }
    // 出库

}
