package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.service.ProcessService;
import org.flowable.task.api.Task;
import org.flowable.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: XYmar
 * Date: 2019/6/21 13:41
 */
@RestController
@RequestMapping(value = "/process")
public class ProcessController {
    private final ProcessService processService;
    private final TaskService taskService;

    @Autowired
    public ProcessController(ProcessService processService, TaskService taskService) {
        this.processService = processService;
        this.taskService = taskService;
    }

    // 启动流程
    @PostMapping
    public ResultEntity startProcessInstance(String userId, Integer money) {
        return new ResultEntity<>(processService.startProcess(userId, money));
    }

    // 查看任务列表
    @GetMapping(value="/tasks/{userId}")
    public ResultEntity getTasks(@PathVariable(value = "userId") String userId) {
        List<Task> tasks = processService.getTasks(userId);
        List<TaskRepresentation> dtos = new ArrayList<>();
        for (Task task : tasks) {
            dtos.add(new TaskRepresentation(task.getId(), task.getName()));
        }
        return new ResultEntity<>(dtos);
    }

    @PostMapping(value = "/completeTask/{taskId}")
    public void completeTask(@PathVariable String taskId) {
        processService.completeTask(taskId);
    }

    // 查看部门任务列表
    @GetMapping(value="/departmentTasks/{departmentId}")
    public List<Task> getDepartmentTasks(@PathVariable(value = "departmentId") String departmentId) {
        return processService.getDepartmentTasks(departmentId);
    }

    /**
     * 批准
     * @param taskId 任务ID
     */
    @PostMapping(value = "/apply/{taskId}")
    public ResultEntity apply(@PathVariable(value = "taskId") String taskId) {
        return new ResultEntity<>(processService.apply(taskId));
    }

    /**
     * 拒绝
     */
    @PostMapping(value = "/reject/{taskId}")
    public ResultEntity reject(@PathVariable(value = "taskId") String taskId) {
        return new ResultEntity<>(processService.reject(taskId));
    }

    @GetMapping(value = "/processDiagram/{processId}")
    public void genProcessDiagram(HttpServletResponse httpServletResponse, @PathVariable(value = "processId") String processId) throws Exception {
        processService.genProcessDiagram(httpServletResponse, processId);
    }

    static class TaskRepresentation {

        private String id;
        private String name;

        public TaskRepresentation(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

    }

}
