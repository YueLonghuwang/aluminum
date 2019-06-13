package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.service.FlowableService;
import org.flowable.task.api.Task;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FlowableController {
    private final FlowableService flowableService;

    public FlowableController(FlowableService flowableService) {
        this.flowableService = flowableService;
    }

    @PostMapping(value = "/process")
    public void startProcessInstance(String a) {
        flowableService.startProcess(a);
    }

    @GetMapping(value = "/tasks")
    public List<TaskRepresentation> getTasks(String assignee) {
        List<Task> tasks = flowableService.getTasks(assignee);
        List<TaskRepresentation> dtos = new ArrayList<>();
        for (Task task : tasks) {
            dtos.add(new TaskRepresentation(task.getId(), task.getName()));
        }
        return dtos;
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
