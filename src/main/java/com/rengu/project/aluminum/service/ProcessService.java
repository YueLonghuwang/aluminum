package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.entity.*;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.enums.ResourceStatusEnum;
import com.rengu.project.aluminum.exception.ProcessException;
import com.rengu.project.aluminum.exception.ResourceException;
import com.rengu.project.aluminum.repository.AlgorithmAndServerRepository;
import com.rengu.project.aluminum.repository.ModelResourceRepository;
import com.rengu.project.aluminum.repository.StandardRepository;
import com.rengu.project.aluminum.repository.ToolsAndSoftwareRepository;
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
import com.rengu.project.aluminum.ApplicationConfig;
import org.springframework.util.StringUtils;

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
    private final DepartmentService departmentService;
    private final ModelResourceRepository modelResourceRepository;
    private final StandardRepository standardRepository;
    private final AlgorithmAndServerRepository algorithmAndServerRepository;
    private final ToolsAndSoftwareRepository toolsAndSoftwareRepository;

    @Autowired
    public ProcessService(RuntimeService runtimeService, TaskService taskService, UserService userService, RepositoryService repositoryService, ProcessEngineConfiguration processEngineConfiguration, DepartmentService departmentService, ModelResourceRepository modelResourceRepository, StandardRepository standardRepository, ToolsAndSoftwareRepository toolsAndSoftwareRepository, AlgorithmAndServerRepository algorithmAndServerRepository) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.userService = userService;
        this.repositoryService = repositoryService;
        this.processEngineConfiguration = processEngineConfiguration;
        this.departmentService = departmentService;
        this.modelResourceRepository = modelResourceRepository;
        this.standardRepository = standardRepository;
        this.toolsAndSoftwareRepository = toolsAndSoftwareRepository;
        this.algorithmAndServerRepository = algorithmAndServerRepository;
    }

    // 启动流程
    @Transactional
    public ProcessEntity startProcess(String userId, String departmentId, int resourceType, String resourceId) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("departmentId", departmentId);
        map.put("resourceId", resourceId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("audit", map);
        String processId = processInstance.getId();              // 流程id
        ProcessEntity processEntity = new ProcessEntity();
        switch (resourceType) {                                  // 保存流程节点到resource
            case ApplicationConfig.MODEL_RESOURCE:
                ModelResourceEntity modelResourceEntity = modelResourceRepository.findById(resourceId).get();
                modelResourceEntity.setProcessId(processId);
                modelResourceEntity.setStatus(ResourceStatusEnum.REVIEWING.getCode());
                modelResourceRepository.save(modelResourceEntity);
                processEntity.setResourceEntity(modelResourceEntity);
                break;
            case ApplicationConfig.STANDARD_RESOURCE:
                StandardEntity standardEntity = standardRepository.findById(resourceId).get();
                standardEntity.setProcessId(processId);
                standardEntity.setStatus(ResourceStatusEnum.REVIEWING.getCode());
                standardRepository.save(standardEntity);
                processEntity.setResourceEntity(standardEntity);
                break;
            case ApplicationConfig.ALGORITHM_RESOURCE:
                AlgorithmAndServerEntity algorithmAndServerEntity = algorithmAndServerRepository.findById(resourceId).get();
                algorithmAndServerEntity.setProcessId(processId);
                algorithmAndServerEntity.setStatus(ResourceStatusEnum.REVIEWING.getCode());
                algorithmAndServerRepository.save(algorithmAndServerEntity);
                processEntity.setResourceEntity(algorithmAndServerEntity);
                break;
            case ApplicationConfig.TOOLS_RESOURCE:
                ToolsAndSoftwareEntity toolsAndSoftwareEntity = toolsAndSoftwareRepository.findById(resourceId).get();
                toolsAndSoftwareEntity.setProcessId(processId);
                toolsAndSoftwareEntity.setStatus(ResourceStatusEnum.REVIEWING.getCode());
                toolsAndSoftwareRepository.save(toolsAndSoftwareEntity);
                processEntity.setResourceEntity(toolsAndSoftwareEntity);
                break;
            default:
                throw new ResourceException(ApplicationMessageEnum.RESOURCE_TYPE_NOT_FOUND);
        }
        processEntity.setResourceType(resourceType);
        processEntity.setId(processId);
        processEntity.setUserEntity(userService.getUserById(userId));
        return processEntity;
    }

    //  获取某部门的任务列表
    @Transactional
    public List<TaskEntity> getDepartmentTasks(String departmentId) {
        DepartmentEntity departmentEntity = departmentService.getDepartmentById(departmentId);
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(departmentEntity.getName()).list();
        return getTaskList(tasks);
    }

    // 申领任务
    @Transactional
    public TaskEntity claimTask(String taskId, String userId){
        UserEntity userEntity = userService.getUserById(userId);
        taskService.claim(taskId, userEntity.getUsername());
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setName(task.getName());
        taskEntity.setProcessId(task.getProcessInstanceId());
        taskEntity.setTaskAssignee(task.getAssignee());
        taskEntity.setResourceEntity(getFirstOrNull(getResourceEntity(task.getProcessInstanceId())));
        return taskEntity;
    }

    // 获取给定任务办理人的任务列表,或申领任务的列表
    @Transactional
    public List<TaskEntity> getTasks(String userId) {
        UserEntity userEntity = userService.getUserById(userId);
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userEntity.getUsername()).orderByTaskCreateTime().desc().list();
        return getTaskList(tasks);
        // return taskService.createTaskQuery().taskAssignee(userId).list();
    }

    // 根据任务返回任务列表
    public List<TaskEntity> getTaskList(List<Task> tasks){
        List<TaskEntity> taskEntityList = new ArrayList<>();
        for (Task task : tasks) {
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setId(task.getId());
            taskEntity.setName(task.getName());
            taskEntity.setProcessId(task.getProcessInstanceId());
            taskEntity.setTaskAssignee(task.getAssignee());
            taskEntity.setResourceEntity(getFirstOrNull(getResourceEntity(task.getProcessInstanceId())));
            taskEntityList.add(taskEntity);
        }
        return taskEntityList;
    }

    //批准
    @Transactional
    public TaskEntity apply(String taskId, String ifApprove) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        //通过审核
        HashMap<String, Object> map = new HashMap<>();
        map.put("ifApprove", ifApprove);
        taskService.complete(taskId, map);
        ResourceEntity resourceEntity = updateResourceEntity(task.getProcessInstanceId(), ifApprove, task.getName());
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setName(task.getName());
        taskEntity.setProcessId(task.getProcessInstanceId());
        taskEntity.setTaskAssignee(task.getAssignee());
        taskEntity.setResourceEntity(resourceEntity);
        return taskEntity;
    }

    // 获取第一个任务
    /*@Transactional
    public void getFirstTask(String departmentId, String userId){
        List<Task> tasks = getDepartmentTasks(departmentId);
        UserEntity userEntity = userService.getUserById(userId);

        for (Task task : tasks) {
            System.out.println("Following task is available for accountancy department: " + task.getName());
            // 申领任务
            taskService.claim(task.getId(), userEntity.getUsername());
        }
    }*/

    // 完成任务
    public void completeTask(String taskId) {
        taskService.complete(taskId);
    }

    // 根据流程id获取流程图
    public void getProcessDiagram(HttpServletResponse httpServletResponse, String processId) throws Exception {
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

    // 根据processId查找resource,并返回resource对应的类型
    public Map<Integer, ResourceEntity> getResourceEntity(String processId) {
        if (StringUtils.isEmpty(processId)) {
            throw new ResourceException(ApplicationMessageEnum.PROCESSID_NOT_FOUND);
        }
        Map<Integer, ResourceEntity> map = new HashMap<>();
        if (modelResourceRepository.existsByProcessId(processId)) {
            map.put(ApplicationConfig.MODEL_RESOURCE, modelResourceRepository.findByProcessId(processId));
        } else if (standardRepository.existsByProcessId(processId)) {
            map.put(ApplicationConfig.STANDARD_RESOURCE, standardRepository.findByProcessId(processId));
        } else if (algorithmAndServerRepository.existsByProcessId(processId)) {
            map.put(ApplicationConfig.ALGORITHM_RESOURCE, algorithmAndServerRepository.findByProcessId(processId));
        } else {
            map.put(ApplicationConfig.TOOLS_RESOURCE, toolsAndSoftwareRepository.findByProcessId(processId));
        }
        return map;
    }

    // 根据processId 及审批结果设置resource的状态
    public ResourceEntity updateResourceEntity(String processId, String ifApprove, String name){
        Map<Integer, ResourceEntity> map = getResourceEntity(processId);
        Integer key = getKeyOrNull(map);
        switch (key) {
            case ApplicationConfig.MODEL_RESOURCE:
                ModelResourceEntity modelResourceEntity = modelResourceRepository.findByProcessId(processId);
                if(ifApprove.equals("驳回")){
                    modelResourceEntity.setStatus(ResourceStatusEnum.REFUSED.getCode());
                }
                if(ifApprove.equals("通过") && name.equals("批准")){
                    modelResourceEntity.setStatus(ResourceStatusEnum.PASSED.getCode());
                }
                return modelResourceRepository.save(modelResourceEntity);
            case ApplicationConfig.STANDARD_RESOURCE:
                StandardEntity standardEntity = standardRepository.findByProcessId(processId);
                if(ifApprove.equals("驳回")){
                    standardEntity.setStatus(ResourceStatusEnum.REFUSED.getCode());
                }
                if(ifApprove.equals("通过") && name.equals("批准")){
                    standardEntity.setStatus(ResourceStatusEnum.PASSED.getCode());
                }
                return standardRepository.save(standardEntity);
            case ApplicationConfig.ALGORITHM_RESOURCE:
                AlgorithmAndServerEntity algorithmAndServerEntity = algorithmAndServerRepository.findByProcessId(processId);
                if(ifApprove.equals("驳回")){
                    algorithmAndServerEntity.setStatus(ResourceStatusEnum.REFUSED.getCode());
                }
                if(ifApprove.equals("通过") && name.equals("批准")){
                    algorithmAndServerEntity.setStatus(ResourceStatusEnum.PASSED.getCode());
                }
                return algorithmAndServerRepository.save(algorithmAndServerEntity);
            case ApplicationConfig.TOOLS_RESOURCE:
                ToolsAndSoftwareEntity toolsAndSoftwareEntity = toolsAndSoftwareRepository.findByProcessId(processId);
                if(ifApprove.equals("驳回")){
                    toolsAndSoftwareEntity.setStatus(ResourceStatusEnum.REFUSED.getCode());
                }
                if(ifApprove.equals("通过") && name.equals("批准")){
                    toolsAndSoftwareEntity.setStatus(ResourceStatusEnum.PASSED.getCode());
                }
                return toolsAndSoftwareRepository.save(toolsAndSoftwareEntity);
            default:
                throw new ResourceException(ApplicationMessageEnum.RESOURCE_TYPE_NOT_FOUND);
        }
    }

    /**
     * 获取map中第一个key值
     */
    private static Integer getKeyOrNull(Map<Integer, ResourceEntity> map) {
        Integer obj = null;
        for (Map.Entry<Integer, ResourceEntity> entry : map.entrySet()) {
            obj = entry.getKey();
            if (obj != null) {
                break;
            }
        }
        return  obj;
    }


    /**
     * 获取map中第一个数据值
     */
    private static ResourceEntity getFirstOrNull(Map<Integer, ResourceEntity> map) {
        ResourceEntity obj = null;
        for (Map.Entry<Integer, ResourceEntity> entry : map.entrySet()) {
            obj = entry.getValue();
            if (obj != null) {
                break;
            }
        }
        return obj;
    }
}
