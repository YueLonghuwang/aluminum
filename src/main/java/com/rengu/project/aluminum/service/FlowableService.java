package com.rengu.project.aluminum.service;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.springframework.stereotype.Service;

/**
 * author : yaojiahao
 * Date: 2019/6/18 10:48
 **/
@Service
public class FlowableService {
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final RepositoryService repositoryService;
    private final ProcessEngine processEngine;

    public FlowableService(RuntimeService runtimeService, TaskService taskService, RepositoryService repositoryService, ProcessEngine processEngine) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.repositoryService = repositoryService;
        this.processEngine = processEngine;
    }

    // 查询所有启动和未启动项目流程
//    public Page
    public void finaAllFlow() {

    }
}
