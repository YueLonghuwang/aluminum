package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.ApplicationRecord;
import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.entity.ToolsAndSoftwareEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.repository.ToolsAndSoftwareRepository;
import com.rengu.project.aluminum.service.ResourceFileService;
import com.rengu.project.aluminum.service.ToolsAndSoftwareService;
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
 * Date: 2019/6/13 14:56
 **/

@RestController
@RequestMapping("/toolsAndSoftware")
public class ToolsAndSoftwareController {
    private final ToolsAndSoftwareService toolsAndSoftwareService;
    private final UserService userService;
    private final ToolsAndSoftwareRepository toolsAndSoftwareRepository;
    private final ResourceFileService resourceFileService;

    public ToolsAndSoftwareController(ToolsAndSoftwareService toolsAndSoftwareService, UserService userService, ToolsAndSoftwareRepository toolsAndSoftwareRepository, ResourceFileService resourceFileService) {
        this.toolsAndSoftwareService = toolsAndSoftwareService;
        this.userService = userService;
        this.toolsAndSoftwareRepository = toolsAndSoftwareRepository;
        this.resourceFileService = resourceFileService;
    }

    // 保存标准规范
    @PostMapping
    public ResultEntity<ToolsAndSoftwareEntity> saveResource(@AuthenticationPrincipal String username, ToolsAndSoftwareEntity toolsAndSoftwareEntity) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(toolsAndSoftwareService.saveResource(toolsAndSoftwareEntity, userEntity));
    }

    // 根据关键字查询
    @PostMapping("/KeyWord")
    public ResultEntity findByKeyWord(@RequestBody Filter filter) {
        return new ResultEntity(selectFrom(toolsAndSoftwareRepository).where(filter).findAll());
    }

    // 根据ID删除标准规范
    @DeleteMapping(value = "/{toolsAndSoftwareId}")
    public ResultEntity<ToolsAndSoftwareEntity> deleteResourceById(@AuthenticationPrincipal String username, @PathVariable(value = "toolsAndSoftwareId") String toolsAndSoftwareId) throws IOException {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(toolsAndSoftwareService.deleteResourceById(toolsAndSoftwareId, userEntity));
    }

    // 根据ID修改工具
    @PatchMapping(value = "/{toolsAndSoftwareId}")
    public ResultEntity<ToolsAndSoftwareEntity> updateResourceById(@AuthenticationPrincipal String username, @PathVariable(value = "toolsAndSoftwareId") String toolsAndSoftwareId, ToolsAndSoftwareEntity toolsAndSoftwareEntity) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(toolsAndSoftwareService.updateResourceById(toolsAndSoftwareId, toolsAndSoftwareEntity, userEntity));
    }

    // 根据ID修查询准规范
    @GetMapping(value = "/{toolsAndSoftwareId}")
    public ResultEntity<ToolsAndSoftwareEntity> getResourceById(@AuthenticationPrincipal String username, @PathVariable(value = "toolsAndSoftwareId") String toolsAndSoftwareId) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(toolsAndSoftwareService.getResourceById(toolsAndSoftwareId, userEntity));
    }

    // 根据ID修查询准规范
    @GetMapping(value = "/{userId}/{toolsAndSoftwareId}/download")
    public void downloadResourceById(HttpServletResponse httpServletResponse, @PathVariable(value = "userId") String userId, @PathVariable(value = "toolsAndSoftwareId") String toolsAndSoftwareId) throws IOException {
        UserEntity userEntity = userService.getUserById(userId);
        File compressFile = toolsAndSoftwareService.downloadResourceById(toolsAndSoftwareId, userEntity);
        String mimeType = URLConnection.guessContentTypeFromName(compressFile.getName()) == null ? "application/octet-stream" : URLConnection.guessContentTypeFromName(compressFile.getName());
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(compressFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        httpServletResponse.setContentLengthLong(compressFile.length());
        // 文件流输出
        IOUtils.copy(new FileInputStream(compressFile), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
    }

    // 根据用户姓名查询未入库的信息
    @GetMapping(value = "/username/ByInitialStatus")
    public ResultEntity<Page<ToolsAndSoftwareEntity>> getResourcesByInitialStatus(@AuthenticationPrincipal String username, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        UserEntity userEntity = userService.getUserByUsername(username);
        int[] status = {0, 1, 2, 3};
        return new ResultEntity<>(toolsAndSoftwareService.getResourcesByUser(pageable, userEntity, status));
    }

    // 根据用户姓名查询入库的信息
    @GetMapping(value = "/username/ByPass")
    public ResultEntity<Page<ApplicationRecord>> getResourcesByPass(@AuthenticationPrincipal String username, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity(toolsAndSoftwareService.getPassResource(userEntity, pageable));
    }

    // 通过用户获取出库的资源
    @GetMapping("/username/ByOut")
    public ResultEntity<Page<ApplicationRecord>> getResourcesByOut(@AuthenticationPrincipal String username, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity(toolsAndSoftwareService.getOutResources(userEntity, pageable));
    }

    // 根据资源ID查询当前资源文件内所有内容
    @GetMapping(value = "/{resourceId}/getAllFiles")
    public ResultEntity<List<Object>> getAllFiles(@PathVariable(value = "resourceId") String resourceId) {
        return new ResultEntity<>(toolsAndSoftwareService.getAllFilesById(resourceId));
    }

    // 根据资源Id查询历史文件信息
    @GetMapping(value = "/{resourceId}/getAllHistoryFilesById")
    public ResultEntity<List<ToolsAndSoftwareEntity>> getAllHistoryFilesById(@PathVariable(value = "resourceId") String resourceId) {
        return new ResultEntity(resourceFileService.getAllHistoryFilesById(resourceId));
    }

    // 根据历史文件Id查询历史文件
    @GetMapping(value = "/{historyResourceId}/getAllHistoryFilesByHistoryId")
    public ResultEntity<List<Object>> getAllHistoryFiles(@PathVariable(value = "historyResourceId") String historyResourceId) {
        return new ResultEntity<>(resourceFileService.getAllHistoryFilesById(historyResourceId));
    }
}
