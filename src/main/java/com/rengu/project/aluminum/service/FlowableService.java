package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.repository.UserRepository;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FlowableService {
    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final UserRepository userRepository;

    public FlowableService(RuntimeService runtimeService, TaskService taskService, UserRepository userRepository) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    public void startProcess(String assignee) {
        Optional<UserEntity> u = userRepository.findByUsername(assignee);
        Map<String, Object> variables = new HashMap<String, Object>();
        UserEntity users = u.get();
        variables.put("userR", users);
        runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    }

    public List<Task> getTasks(String assignee) {
        return taskService.createTaskQuery().taskAssignee(assignee).list();
    }

}
