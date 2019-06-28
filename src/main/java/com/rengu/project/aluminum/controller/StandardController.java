package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.PreviewFile;
import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.entity.StandardEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.repository.StandardRepository;
import com.rengu.project.aluminum.service.StandardService;
import com.rengu.project.aluminum.service.UserService;
import com.rengu.project.aluminum.specification.Filter;
import com.rengu.project.aluminum.util.FileUtil;
import com.rengu.project.aluminum.util.PreviewFileInit;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rengu.project.aluminum.specification.SpecificationBuilder.selectFrom;

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
    private final String rootPath = "C:/tmp/conventer";
    private final StandardService standardService;
    private final UserService userService;
    private final StandardRepository standardRepository;
    private final PreviewFileInit previewFileInit;

    public StandardController(StandardService standardService, UserService userService, StandardRepository standardRepository, PreviewFileInit previewFileInit) {
        this.standardService = standardService;
        this.userService = userService;
        this.standardRepository = standardRepository;
        this.previewFileInit = previewFileInit;
    }

    // 保存标准规范
    @PostMapping
    public ResultEntity<StandardEntity> saveResource(@AuthenticationPrincipal String username, StandardEntity standardEntity) {
        UserEntity userEntity = userService.getUserByUsername(username);
        return new ResultEntity<>(standardService.saveResource(standardEntity, userEntity));
    }

    // 根据关键字查询
    @PostMapping("/KeyWord")
    public ResultEntity findByKeyWord(@RequestBody Filter filter) {
        return new ResultEntity(selectFrom(standardRepository).where(filter).findAll());
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
    @GetMapping(value = "/{userId}/{standardId}/download")
    public void downloadResourceById(HttpServletResponse httpServletResponse, @PathVariable(value = "userId") String userId, @PathVariable(value = "standardId") String standardId) throws IOException {
        UserEntity userEntity = userService.getUserById(userId);
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

    // 根据ID预览文件
    @GetMapping(value = "/{fileId}/readPDF")
    public Map readPdf(@PathVariable(value = "fileId") String fileId) {
        return previewUrl(standardService.readPDF(fileId));
    }

    // 根据资源ID查询当前资源文件内所有内容
    @GetMapping(value = "/{resourceId}/getAllFiles")
    public ResultEntity<List<Object>> getAllFiles(@PathVariable(value = "resourceId") String resourceId) {
        return new ResultEntity<>(standardService.getAllFilesById(resourceId));
    }


    @GetMapping
    public ResultEntity<Page<StandardEntity>> getResources(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResultEntity<>(standardService.getResources(pageable));
    }

    /**
     /* * 得到fileid 然后创建预览临时文件夹，然后调用预览的实体类，然后
     */

    /**
     * 获取重定向路径
     */
    private Map<String, String> previewUrl(PreviewFile PreviewFile) {
        File file = new File(rootPath + File.separator + PreviewFile.getFileId()
                + File.separator + "resource" + File.separator + PreviewFile.getConventedFileName());
        String subfix = FileUtil.getFileSufix(PreviewFile.getFilePath());
        //返回一个后缀名以及pathId给你，根据后缀名判断该通往哪个页面
        Map<String, String> map = new HashMap<>();
        map.put("pathId", PreviewFile.getFileId());
        map.put("fileSubfixType", subfix);
        map.put("fileType", "PDF");
        return map;
    }

    @GetMapping(value = "/viewer/document/{pathId}")
    public void onlinePreview(@PathVariable String pathId, String fileFullPath, HttpServletResponse response) throws
            IOException {
        PreviewFile previewFile = previewFileInit.findByHashCode(pathId);
        // 得到转换后的文件地址
        String fileUrl;
        if (fileFullPath != null) {
            fileUrl = rootPath + File.separator + fileFullPath;
        } else {
            if (previewFile.getConventedFileName() == null || previewFile.getConventedFileName().equals("")) {
                fileUrl = rootPath + File.separator + previewFile.getFileId() + File.separator + "resource" + File.separator;
            }
            fileUrl = rootPath + File.separator + previewFile.getFileId() + File.separator + "resource" + File.separator + previewFile.getConventedFileName();
        }
        File file = new File(fileUrl);
        // 设置内容长度
        response.setContentLength((int) file.length());
        // 内容配置中要转码,inline 浏览器支持的格式会在浏览器中打开,否则下载
        String fullFileName = FileUtil.getFileName(previewFile.getFilePath());
        response.setHeader("Content-Disposition", "inline;fileName=\"" + fullFileName + "\"");
        // 设置content-type
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType(previewFile.getOriginalMIMEType());
        System.out.println(previewFile.getOriginalMIMEType());
        @Cleanup FileInputStream is = new FileInputStream(new File(fileUrl));
        @Cleanup OutputStream os = response.getOutputStream();
        IOUtils.copy(is, os);
        response.flushBuffer();
    }
}
