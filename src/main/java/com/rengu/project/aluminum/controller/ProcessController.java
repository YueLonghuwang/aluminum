package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.entity.TaskEntity;
import com.rengu.project.aluminum.service.ProcessService;
import com.rengu.project.aluminum.service.ResourceFileService;
import org.flowable.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
    private final ResourceFileService resourceFileService;
    @Autowired
    public ProcessController(ProcessService processService, TaskService taskService, ResourceFileService resourceFileService) {
        this.processService = processService;
        this.taskService = taskService;
        this.resourceFileService = resourceFileService;
    }

    // 启动流程
    @PostMapping
    public ResultEntity startProcessInstance(String userId, String departmentId, int resourceType, String resourceId, int applicationStatus, String explain) {
        return new ResultEntity<>(processService.startProcess(userId, departmentId, resourceType, resourceId, applicationStatus, explain));
    }
    // 查看部门任务列表
    @GetMapping(value="/departmentTasks/{departmentId}")
    public ResultEntity getDepartmentTasks(@PathVariable(value = "departmentId") String departmentId) {
        List<TaskEntity> tasks = processService.getDepartmentTasks(departmentId);
        /*List<TaskRepresentation> dtos = new ArrayList<>();
        for (Task task : tasks) {
            dtos.add(new TaskRepresentation(task.getId(), task.getName()));
        }*/
        return new ResultEntity<>(tasks);
    }

    // 申领任务
    @PostMapping(value = "/claimTask/{taskId}")
    public ResultEntity claimTask(@PathVariable String taskId,  String userId) {
        return new ResultEntity<>(processService.claimTask(taskId, userId));
    }

    // 查看任务列表
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value="/tasks/{userId}")
    public ResultEntity getTasks(@PathVariable(value = "userId") String userId) {
        List<TaskEntity> tasks = processService.getTasks(userId);
        /*List<TaskRepresentation> dtos = new ArrayList<>();
        for (Task task : tasks) {
            dtos.add(new TaskRepresentation(task.getId(), task.getName()));
        }*/
        return new ResultEntity<>(tasks);
    }

    // 完成任务
    @PostMapping(value = "/completeTask/{taskId}")
    public void completeTask(@PathVariable String taskId) {
        processService.completeTask(taskId);
    }

    /**
     * 批准
     * @param taskId 任务ID
     */
    @PostMapping(value = "/apply/{taskId}")
    public ResultEntity apply(@PathVariable(value = "taskId") String taskId, String ifApprove) {
        return new ResultEntity<>(processService.apply(taskId, ifApprove));
    }

    // 返回流程图
    @GetMapping(value = "/processDiagram/{processId}")
    public void getProcessDiagram(HttpServletResponse httpServletResponse, @PathVariable(value = "processId") String processId) throws Exception {
        processService.getProcessDiagram(httpServletResponse, processId);
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

    // 根据资源ID查询当前资源文件内所有内容
    @GetMapping(value = "/{resourceId}/getAllFiles")
    public ResultEntity<List<Object>> getAllFiles(@PathVariable(value = "resourceId") String resourceId) {
        return new ResultEntity<>(resourceFileService.getAllFilesById(resourceId));
    }
}
