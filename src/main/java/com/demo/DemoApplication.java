package com.demo;

import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {


    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);
    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        securityUtil.logInAs("salaboy");

        logger.info("> Creating a Group Task for 'activitiTeam'");
        taskRuntime.create(TaskPayloadBuilder.create()
                .withName("task test1")
                .withDescription("description")
                .withCandidateGroup("group")
                .withPriority(10)
                .build());

        securityUtil.logInAs("other");

        logger.info("> Getting all the tasks");
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 10));

        logger.info(">  other cannot see the task: " + tasks.getTotalItems());

        securityUtil.logInAs("erdemedeiros");

        // Let's get 'erdemedeiros' tasks
        logger.info("> Getting all the tasks");
        tasks = taskRuntime.tasks(Pageable.of(0, 10));

        // 'erdemedeiros' can see and claim the task
        logger.info(">  erdemedeiros can see the task: " + tasks.getTotalItems());


        String availableTaskId = tasks.getContent().get(0).getId();

        // Let's claim the task, after the claim, nobody else can see the task and 'erdemedeiros' becomes the assignee
        logger.info("> Claiming the task");
        taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(availableTaskId).build());


        // Let's complete the task
        logger.info("> Completing the task");
        taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(availableTaskId).build());
    }
}
