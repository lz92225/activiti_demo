package com.demo;

import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.*;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class Demo1 {

    private static final Logger logger = LoggerFactory.getLogger(Demo1.class);

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Test
    //发布流程
    public void deploy(){
        ProcessEngines.getDefaultProcessEngine();
        Deployment deploy = repositoryService.createDeployment().name("ceshi1").addClasspathResource("processes/twouser.bpmn").deploy();
        logger.info("流程id："+deploy.getId());
        logger.info("流程定义id:"+deploy.getTenantId());
        logger.info("流程name:"+deploy.getName());
    }

    @Test
    public void startProcessInstance(){
        Map<String, Object> variables = new HashMap<>();
        variables.put("userid", "salaboy");
        variables.put("userid2", "ryandawsonuk");
        securityUtil.logInAs("salaboy");
//        org.activiti.engine.impl.identity.Authentication.setAuthenticatedUserId("salaboy");//设置
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("twouser",variables);
        logger.info("流程id："+instance.getId());
        logger.info("流程名称："+instance.getName());
        logger.info("流程定义id:"+instance.getProcessDefinitionId());
    }

    @Test
    public void test1() {
//        securityUtil.logInAs("salaboy");
//        Page<ProcessDefinition> processDefinitionPage = processRuntime.processDefinitions(Pageable.of(0, 1));
//        logger.info("流程定义数量：" + processDefinitionPage.getTotalItems());
//
//        List<ProcessDefinition> content = processDefinitionPage.getContent();
//        for (ProcessDefinition processDefinition : content) {
//            logger.info(processDefinition.getId() + "--" + processDefinition.getName());
//        }
    }

    @Test
    public void test2(){
        securityUtil.logInAs("salaboy");
        //发布任务
//        taskRuntime.create(TaskPayloadBuilder.create()
//                .withName("task test1")
//                .withDescription("description")
//                .withCandidateGroup("group")
//                .withPriority(10)
//                .withAssignee("ryandawsonuk")
//                .build());



        //查询 ryandawsonuk 的任务
        List<Task> list = taskService.createTaskQuery()
//                .taskAssignee("ryandawsonuk")
//                .taskOwner("")
                .orderByTaskCreateTime()
                .asc()
                .list();

        if(list.size()>0) {
            for (Task task : list) {
                logger.info("任务ID" + task.getId());
                logger.info("任务名称" + task.getName());
                logger.info("任务创建时间" + task.getCreateTime());
                logger.info("任务的办理人" + task.getAssignee());
                logger.info("流程实例id:" + task.getProcessInstanceId());
                logger.info("执行对象id:" + task.getExecutionId());
                logger.info("流程定义id:" + task.getProcessDefinitionId());
            }
        }else {
            logger.info("没有任务");
        }
    }

    //完成任务
    @Test
    public void test3() {
        String taskid = "be9aeb2d-c96e-11e9-bb51-1002b52d6afe";
        taskService.complete(taskid);
    }

    //任务转移
    @Test
    public void test4(){
        String taskid = "c5f8f0ea-c97a-11e9-b4c7-1002b52d6afe";
        String taskAssginee = "ryandawsonuk";
        String taskOwner = "salaboy";
//        taskService.setAssignee(taskid, taskAssginee);

        //委派：未决定PENDING,   已决定RESOLVED;
        taskService.delegateTask(taskid, "ssdsadsa");

//        taskService.setOwner(taskid,taskOwner);
    }

}
