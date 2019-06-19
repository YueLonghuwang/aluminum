package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.entity.StandardEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.service.StandardService;
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
 * com.rengu.project.aluminum.controller
 *
 * @author hanchangming
 * @date 2019-06-11
 */

@Slf4j
@RestController
@RequestMapping(value = "/standards")
public class StandardController {

    private final StandardService standardService;
    private final UserService userService;

    public StandardController(StandardService standardService, UserService userService) {
        this.standardService = standardService;
        this.userService = userService;
    }

    // 保存标准规范
    @PostMapping
    public ResultEntity<StandardEntity> saveResource(@AuthenticationPrincipal String username, StandardEntity standardEntity) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(standardService.saveResource(standardEntity, userEntity));
    }

    // 根据ID删除标准规范
    @DeleteMapping(value = "/{standardId}")
    public ResultEntity<StandardEntity> deleteResourceById(@AuthenticationPrincipal String username, @PathVariable(value = "standardId") String standardId) throws IOException {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(standardService.deleteResourceById(standardId, userEntity));
    }

    // 根据ID修改标准规范
    @PatchMapping(value = "/{standardId}")
    public ResultEntity<StandardEntity> updateResourceById(@AuthenticationPrincipal String username, @PathVariable(value = "standardId") String standardId, StandardEntity standardEntity) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(standardService.updateResourceById(standardId, standardEntity, userEntity));
    }

    // 根据ID查询准规范
    @GetMapping(value = "/{standardId}")
    public ResultEntity<StandardEntity> getResourceById(@AuthenticationPrincipal String username, @PathVariable(value = "standardId") String standardId) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(standardService.getResourceById(standardId, userEntity));
    }

    // 根据ID下载标准规范
    @GetMapping(value = "/{standardId}/download")
    public void downloadResourceById(HttpServletResponse httpServletResponse, @AuthenticationPrincipal String username, @PathVariable(value = "standardId") String standardId) throws IOException {
        UserEntity userEntity = userService.getUserByUsername(username);
        File compressFile = standardService.downloadResourceById(standardId, userEntity);
        String mimeType = URLConnection.guessContentTypeFromName(compressFile.getName()) == null ? "application/octet-stream" : URLConnection.guessContentTypeFromName(compressFile.getName());
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(compressFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        httpServletResponse.setContentLengthLong(compressFile.length());
        // 文件流输出
        IOUtils.copy(new FileInputStream(compressFile), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
    }

    // 根据用户名查询资源
    @GetMapping(value = "/by/user")
    public ResultEntity<Page<StandardEntity>> getResourcesByUser(@AuthenticationPrincipal String username, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(standardService.getResourcesByUser(pageable, userEntity));
    }


    @GetMapping
    public ResultEntity<Page<StandardEntity>> getResources(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResultEntity<>(standardService.getResources(pageable));
    }
}
