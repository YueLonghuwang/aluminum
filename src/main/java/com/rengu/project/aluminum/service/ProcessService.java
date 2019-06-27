package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: XYmar
 * Date: 2019/6/21 11:43
 */
@Service
@Transactional
@Slf4j
public class ProcessService {
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final UserService userService;
    private final RepositoryService repositoryService;
    private final ProcessEngineConfiguration processEngineConfiguration;

    @Autowired
    public ProcessService(RuntimeService runtimeService, TaskService taskService, UserService userService, RepositoryService repositoryService, ProcessEngineConfiguration processEngineConfiguration) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.userService = userService;
        this.repositoryService = repositoryService;
        this.processEngineConfiguration = processEngineConfiguration;
    }

    // 启动流程
    @Transactional
    public String startProcess(String userId, Integer money) {
        /*UserEntity userEntity = userService.getUserById(userId);
*/
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("money", money);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("audit", map);
        return "提交成功.流程Id为：" + processInstance.getId();
    }

    // 获取给定任务办理人的任务列表,或申领任务的列表
    @Transactional
    public List<Task> getTasks(String userId) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
        for (Task task : tasks) {
            log.info(task.toString());
        }
        return tasks;
        // return taskService.createTaskQuery().taskAssignee(userId).list();
    }

    // 获取第一个任务
    @Transactional
    public void getFirstTask(String departmentId, String userId){
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(departmentId).list();
        UserEntity userEntity = userService.getUserById(userId);

        for (Task task : tasks) {
            System.out.println("Following task is available for accountancy department: " + task.getName());
            // 申领任务
            taskService.claim(task.getId(), userEntity.getUsername());
        }
    }

    //  获取某部门的任务列表
    @Transactional
    public List<Task> getDepartmentTasks(String departmentId) {
        return taskService.createTaskQuery().taskCandidateGroup(departmentId).list();
    }

    // 完成任务
    public void completeTask(String taskId) {
        taskService.complete(taskId);
    }

    //批准
    @Transactional
    public String apply(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        //通过审核
        HashMap<String, Object> map = new HashMap<>();
        map.put("outcome", "通过");
        taskService.complete(taskId);
        return "已批准!";
    }

    //拒绝
    @Transactional
    public String reject(String taskId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("outcome", "驳回");
        taskService.complete(taskId, map);
        return "已拒绝!";
    }

    public void genProcessDiagram(HttpServletResponse httpServletResponse, String processId) throws Exception {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();

        //流程走完的不显示图
        if (pi == null) {
            return;
        }
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        String InstanceId = task.getProcessInstanceId();
        List<Execution> executions = runtimeService
                .createExecutionQuery()
                .processInstanceId(InstanceId)
                .list();

        //得到正在执行的Activity的Id
        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            activityIds.addAll(ids);
        }

        //获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        // ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        // InputStream generateDiagram(BpmnModel var1, String var2, List<String> var3, List<String> var4, String var5, String var6, String var7, ClassLoader var8, double var9, boolean var11);
        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, processEngineConfiguration.getActivityFontName(), processEngineConfiguration.getLabelFontName(), processEngineConfiguration.getAnnotationFontName(), processEngineConfiguration.getClassLoader(), 1.0, true);
        OutputStream out = null;
        byte[] buf = new byte[1024];
        int legth = 0;
        try {
            out = httpServletResponse.getOutputStream();
            while ((legth = in.read(buf)) != -1) {
                out.write(buf, 0, legth);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }


}
