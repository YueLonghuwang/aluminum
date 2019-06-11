package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * com.rengu.project.aluminum.controller
 *
 * @author hanchangming
 * @date 2019-06-10
 */

@Slf4j
@RestController
@RequestMapping(value = "/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // 根据md5查询文件
    @GetMapping(value = "/by-md5")
    public ResultEntity getFileByMd5(@RequestParam(value = "md5") String md5) {
        return new ResultEntity<>(fileService.getFileByMd5(md5));
    }

    // 根据MD5检查文件是否存在
    @GetMapping(value = "/has-md5")
    public ResultEntity hasFileByMD5(@RequestParam(value = "md5") String md5) {
        return new ResultEntity<>(fileService.hasFileByMd5(md5));
    }
}
