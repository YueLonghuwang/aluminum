package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.AlgorithmAndServerEntity;
import com.rengu.project.aluminum.entity.ApplicationRecord;
import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.repository.AlgorithmAndServerRepository;
import com.rengu.project.aluminum.service.AlgorithmAndServerService;
import com.rengu.project.aluminum.service.UserService;
import com.rengu.project.aluminum.specification.Filter;
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
import java.util.List;

import static com.rengu.project.aluminum.specification.SpecificationBuilder.selectFrom;

/**
 * author : yaojiahao
 * Date: 2019/6/13 14:31
 **/

@RestController
@RequestMapping("/algorithmAndServer")
public class AlgorithmAndServerController {
    private final AlgorithmAndServerService algorithmAndServerService;
    private final UserService userService;
    private final AlgorithmAndServerRepository algorithmAndServerRepository;

    public AlgorithmAndServerController(AlgorithmAndServerService algorithmAndServerService, UserService userService, AlgorithmAndServerRepository algorithmAndServerRepository) {
        this.algorithmAndServerService = algorithmAndServerService;
        this.userService = userService;
        this.algorithmAndServerRepository = algorithmAndServerRepository;
    }

    // 保存标准规范
    @PostMapping
    public ResultEntity<AlgorithmAndServerEntity> saveResource(@AuthenticationPrincipal String username, AlgorithmAndServerEntity algorithmAndServerEntity) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(algorithmAndServerService.saveResource(algorithmAndServerEntity, userEntity));
    }

    // 根据关键字查询
    @PostMapping("/KeyWord")
    public ResultEntity<java.util.List<AlgorithmAndServerEntity>> findByKeyWord(@RequestBody Filter filter) {
        return new ResultEntity<>(selectFrom(algorithmAndServerRepository).where(filter).findAll());
    }
    // 根据ID删除标准规范
    @DeleteMapping(value = "/{algorithmAndServerId}")
    public ResultEntity<AlgorithmAndServerEntity> deleteResourceById(@AuthenticationPrincipal String username, @PathVariable(value = "algorithmAndServerId") String algorithmAndServerId) throws IOException {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(algorithmAndServerService.deleteResourceById(algorithmAndServerId, userEntity));
    }

    // 根据ID修改标准规范
    @PatchMapping(value = "/{algorithmAndServerId}")
    public ResultEntity<AlgorithmAndServerEntity> updateResourceById(@AuthenticationPrincipal String username, @PathVariable(value = "algorithmAndServerId") String algorithmAndServerId, AlgorithmAndServerEntity algorithmAndServerEntity) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(algorithmAndServerService.updateResourceById(algorithmAndServerId, algorithmAndServerEntity, userEntity));
    }

    // 根据ID修查询准规范
    @GetMapping(value = "/{algorithmAndServerId}")
    public ResultEntity<AlgorithmAndServerEntity> getResourceById(@AuthenticationPrincipal String username, @PathVariable(value = "algorithmAndServerId") String algorithmAndServerId) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(algorithmAndServerService.getResourceById(algorithmAndServerId, userEntity));
    }

    // 根据ID修查询准规范
    @GetMapping(value = "/{userId}/{algorithmAndServerId}/download")
    public void downloadResourceById(HttpServletResponse httpServletResponse, @PathVariable(value = "userId") String userId, @PathVariable(value = "algorithmAndServerId") String algorithmAndServerId) throws IOException {
        UserEntity userEntity = userService.getUserById(userId);
        File compressFile = algorithmAndServerService.downloadResourceById(algorithmAndServerId, userEntity);
        String mimeType = URLConnection.guessContentTypeFromName(compressFile.getName()) == null ? "application/octet-stream" : URLConnection.guessContentTypeFromName(compressFile.getName());
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(compressFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        httpServletResponse.setContentLengthLong(compressFile.length());
        // 文件流输出
        IOUtils.copy(new FileInputStream(compressFile), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
    }

    // 根据用户姓名查询入库的信息
    @GetMapping(value = "/username/ByPass")
    public ResultEntity<Page<ApplicationRecord>> getResourcesByPass(@AuthenticationPrincipal String username, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity(algorithmAndServerService.getPassResource(userEntity, pageable));
    }

    // 根据用户姓名查询未入库的信息
    @GetMapping(value = "/username/ByInitialStatus")
    public ResultEntity<Page<AlgorithmAndServerEntity>> getResourcesByInitialStatus(@AuthenticationPrincipal String username, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        UserEntity userEntity = userService.getUserByUsername(username);
        int[] status = {0, 1, 2, 3};
        return new ResultEntity<>(algorithmAndServerService.getResourcesByUser(pageable, userEntity, status));
    }

    // 通过用户获取出库的资源
    @GetMapping("/username/ByOut")
    public ResultEntity<Page<ApplicationRecord>> getResourcesByOut(@AuthenticationPrincipal String username, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity(algorithmAndServerService.getOutResources(userEntity, pageable));
    }

    // 根据资源ID查询当前资源文件内所有内容
    @GetMapping(value = "/{resourceId}/getAllFiles")
    public ResultEntity<List<Object>> getAllFiles(@PathVariable(value = "resourceId") String resourceId) {
        return new ResultEntity<>(algorithmAndServerService.getAllFilesById(resourceId));
    }
}