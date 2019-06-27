package com.rengu.project.aluminum;

import com.rengu.project.aluminum.service.ProcessService;
import com.spring4all.swagger.EnableSwagger2Doc;
import org.flowable.engine.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@EnableSwagger2Doc
@EnableCaching
@SpringBootApplication
public class AluminumApplication {

    public static void main(String[] args) {
        SpringApplication.run(AluminumApplication.class, args);
    }

    /*@Bean
    public CommandLineRunner init(final RepositoryService repositoryService,
                                  final RuntimeService runtimeService,
                                  final TaskService taskService) {

        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                System.out.println("Number of process definitions : "
                        + repositoryService.createProcessDefinitionQuery().count());
                System.out.println("Number of tasks : " + taskService.createTaskQuery().count());
                runtimeService.startProcessInstanceByKey("oneTaskProcess");
                System.out.println("Number of tasks after process start: "
                        + taskService.createTaskQuery().count());
            }
        };
    }*/

    @Bean
    public CommandLineRunner init(final ProcessService processService) {

        return new CommandLineRunner() {
            public void run(String... strings) throws Exception {
            }
        };
    }
}
