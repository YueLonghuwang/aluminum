package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.*;
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
import java.util.List;

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

    // 根据用户姓名查询未入库的信息
    @GetMapping(value = "/username/ByInitialStatus")
    public ResultEntity<Page<AlgorithmAndServerEntity>> getResourcesByInitialStatus(@AuthenticationPrincipal String username, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        UserEntity userEntity = userService.getUserByUsername(username);
        int[] status = {0, 1, 2, 3};
        return new ResultEntity<>(modelResourceService.getResourcesByUser(pageable, userEntity, status));
    }

    // 根据用户姓名查询入库的信息
    @GetMapping(value = "/username/ByPass")
    public ResultEntity<Page<ApplicationRecord>> getResourcesByPass(@AuthenticationPrincipal String username, Pageable pageable) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity(modelResourceService.getPassResource(userEntity, pageable));
    }

    // 通过用户获取出库的资源
    @GetMapping("/username/ByOut")
    public ResultEntity<Page<ApplicationRecord>> getResourcesByOut(@AuthenticationPrincipal String username, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity(modelResourceService.getOutResources(userEntity, pageable));
    }

    // 根据资源ID查询当前资源文件内所有内容
    @GetMapping(value = "/{resourceId}/getAllFiles")
    public ResultEntity<List<Object>> getAllFiles(@PathVariable(value = "resourceId") String resourceId) {
        return new ResultEntity<>(modelResourceService.getAllFilesById(resourceId));
    }
}
