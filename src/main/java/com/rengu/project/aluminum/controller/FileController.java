package com.rengu.project.aluminum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rengu.project.aluminum.entity.ChunkEntity;
import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * com.rengu.project.aluminum.controller
 *
 * @author hanchangming
 * @date 2019-06-10
 */

@RestController
@RequestMapping(value = "/files")
@Slf4j
public class FileController {
    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // 检查文件块是否存在
    @GetMapping(value = "/chunks")
    public void hasChunk(HttpServletResponse httpServletResponse, ChunkEntity chunk) throws IOException {
        if (!fileService.hasChunkEntity(chunk)) {
            httpServletResponse.setStatus(HttpServletResponse.SC_GONE);
        }
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        PrintWriter writer = httpServletResponse.getWriter();
        boolean boo = fileService.hasUpload(chunk);
        List<Integer> chunkNumberList = fileService.getChunkNumbers(chunk);
        Map<Object, Object> map = new HashMap<>();
        // 数组 包含当前的传递的块数 一个状态skipUpload
        ObjectMapper objectMapper = new ObjectMapper();
        String s;
        if (boo) {
            map.put("skipUpload", true);
            s = objectMapper.writeValueAsString(map);
        } else {
            map.put("chunkNumerList", chunkNumberList);
            s = objectMapper.writeValueAsString(map);
        }
        writer.write(s);
    }

    // 根据MD5检查文件是否存在
    @GetMapping(value = "/hasmd5")
    public ResultEntity<java.io.Serializable> hasFileByMD5(@RequestParam(value = "MD5") String MD5) {
        return new ResultEntity<>(fileService.hasFileByMD5(MD5) ? fileService.getFileByMD5(MD5) : fileService.hasFileByMD5(MD5));
    }

    // 保存文件块
    @PostMapping(value = "/chunks")
    public void saveChunk(ChunkEntity chunk, @RequestParam(value = "file") MultipartFile multipartFile) throws IOException {
        fileService.saveChunkEntity(chunk, multipartFile);
    }

    // 合并文件块
    @PostMapping(value = "/chunks/merge")
    public ResultEntity<com.rengu.project.aluminum.entity.FileEntity> mergeChunks(ChunkEntity chunk) throws IOException, ExecutionException, InterruptedException {
        return new ResultEntity<>(fileService.mergeChunkEntitys(chunk));
    }
}
