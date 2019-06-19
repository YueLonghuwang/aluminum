package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.service.FlowableService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * author : yaojiahao
 * Date: 2019/6/18 9:49
 */


@RestController
@RequestMapping("/flowable")
public class FlowableController {
    private final FlowableService flowableService;

    public FlowableController(FlowableService flowableService) {
        this.flowableService = flowableService;
    }

    // 查询所有流程
    @PreAuthorize(value = "hasRole('ADMIN')")
    @GetMapping
    public ResultEntity findAll(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResultEntity<>(null);
    }

    // 启动流程
    @PreAuthorize(value = "hasRole('ADMIN')")
    @PostMapping(value = "/start/{flowId}")
    public ResultEntity startFlow(String userID) {
        return new ResultEntity<>(null);
    }
}
