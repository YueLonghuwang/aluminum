package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.entity.ToolsAndSoftwareEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.service.ToolsAndSoftwareService;
import com.rengu.project.aluminum.service.UserService;
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
 * Date: 2019/6/13 14:56
 **/

@RestController
@RequestMapping("/toolsAndSoftware")
public class ToolsAndSoftwareController {
    private final ToolsAndSoftwareService toolsAndSoftwareService;
    private final UserService userService;

    public ToolsAndSoftwareController(ToolsAndSoftwareService toolsAndSoftwareService, UserService userService) {
        this.toolsAndSoftwareService = toolsAndSoftwareService;
        this.userService = userService;
    }

    // 保存标准规范
    @PostMapping
    public ResultEntity<ToolsAndSoftwareEntity> saveResource(@AuthenticationPrincipal String username, ToolsAndSoftwareEntity toolsAndSoftwareEntity) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(toolsAndSoftwareService.saveResource(toolsAndSoftwareEntity, userEntity));
    }

    // 根据ID删除标准规范
    @DeleteMapping(value = "/{toolsAndSoftwareId}")
    public ResultEntity<ToolsAndSoftwareEntity> deleteResourceById(@AuthenticationPrincipal String username, @PathVariable(value = "toolsAndSoftwareId") String toolsAndSoftwareId) throws IOException {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(toolsAndSoftwareService.deleteResourceById(toolsAndSoftwareId, userEntity));
    }

    // 根据ID修改标准规范
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
    @GetMapping(value = "/{toolsAndSoftwareId}/download")
    public void downloadResourceById(HttpServletResponse httpServletResponse, @AuthenticationPrincipal String username, @PathVariable(value = "toolsAndSoftwareId") String toolsAndSoftwareId) throws IOException {
        UserEntity userEntity = userService.getUserByUsername(username);
        File compressFile = toolsAndSoftwareService.downloadResourceById(toolsAndSoftwareId, userEntity);
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
    public ResultEntity<Page<ToolsAndSoftwareEntity>> getResourcesByUser(@AuthenticationPrincipal String username, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(toolsAndSoftwareService.getResourcesByUser(pageable, userEntity));
    }

    // 根据ID修查询准规范
    @GetMapping
    public ResultEntity<Page<ToolsAndSoftwareEntity>> getResources(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResultEntity<>(toolsAndSoftwareService.getResources(pageable));
    }
}
