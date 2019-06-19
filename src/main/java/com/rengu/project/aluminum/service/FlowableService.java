package com.rengu.project.aluminum.service;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
    private final UserService userService;

    public FlowableService(RuntimeService runtimeService, TaskService taskService, RepositoryService repositoryService, ProcessEngine processEngine, UserService userService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.repositoryService = repositoryService;
        this.processEngine = processEngine;
        this.userService = userService;
    }

    // 启动流程
    public void findAllFlow(String userId, String flowId) {
        String username = userService.getUserById(userId).getUsername();
        Map map = new HashMap();
        map.put("userName", username);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(flowId, map);
        System.out.println("成功启动流程: " + processInstance.getName());
    }

    // 查询流程列表，待办列表
}
