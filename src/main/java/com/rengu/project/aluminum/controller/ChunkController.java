package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.ChunkEntity;
import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.service.ChunkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * com.rengu.project.aluminum.controller
 *
 * @author hanchangming
 * @date 2019-06-10
 */

@Slf4j
@RestController
@RequestMapping(value = "/chunks")
public class ChunkController {

    private final ChunkService chunkService;

    public ChunkController(ChunkService chunkService) {
        this.chunkService = chunkService;
    }

    // 保存文件块
    @PostMapping
    public ResultEntity saveChunk(ChunkEntity chunkEntity, @RequestParam(value = "file") MultipartFile multipartFile) throws IOException {
        return new ResultEntity<>(chunkService.saveChunk(chunkEntity, multipartFile));
    }

    // 合并文件块
    @PostMapping(value = "/merge")
    public ResultEntity mergeChunks(ChunkEntity chunkEntity) throws IOException {
        return new ResultEntity<>(chunkService.mergeChunks(chunkEntity));
    }

    // 检查文件块是否存在
    @GetMapping(value = "/has/chunks")
    public void hasChunk(HttpServletResponse httpServletResponse, ChunkEntity chunkEntity) {
        if (!chunkService.hasChunk(chunkEntity)) {
            httpServletResponse.setStatus(HttpServletResponse.SC_GONE);
        }
    }
}
