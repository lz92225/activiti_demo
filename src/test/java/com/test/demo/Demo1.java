package com.test.demo;

import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo1 {

    private static final ProcessEngine processEngine;

    static{
        processEngine = ProcessEngines.getDefaultProcessEngine();
    }
    private static final Logger log = LoggerFactory.getLogger(Demo1.class);
    @Test
    public void startProcess(){
        RepositoryService repositoryService = processEngine.getRepositoryService();

        DeploymentBuilder builder = repositoryService.createDeployment();
        builder.addClasspathResource("processes/my_process.bpmn");
        builder.deploy();
        // select * from `ACT_re_procdef`;这时这个表中会多条数据

        List<ProcessDefinition> p = repositoryService.createProcessDefinitionQuery().list();
        for(int i=0;i<p.size();i++){
            System.out.println(p.get(i).getKey());
        }
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.startProcessInstanceByKey("myProcess");//启动流程，ID必须与你配置的一致

        System.out.println("ok......");
    }


    @Test
//    @org.activiti.engine.test.Deployment(resources = "processes/my_process.bpmn20.xml")
    public void test2(){
//        RepositoryService repositoryService = processEngine.getRepositoryService();
//        Deployment deploy = repositoryService.createDeployment().addClasspathResource("processes/my_process.bpmn").deploy();
//        log.info("流程发布成功，id："+deploy.getId());
//
//
//        RuntimeService runtimeService = processEngine.getRuntimeService();
//        ProcessInstance instance = runtimeService.startProcessInstanceByKey("my_process", "1");
//        log.info("流程启动成功，id为："+instance.getId());
    }

    @Test
    public void test3(){
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("processes/my_process.bpmn").deploy();
        log.info("流程发布成功，id："+deploy.getId());

        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("my_process", "1");
        log.info("流程启动成功，id为："+instance.getId());

        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(instance.getId()).singleResult();
        if(task != null){
            Map<String, Object> map = new HashMap<>(2);
            map.put("check",1);
            map.put("personid", "101");
            taskService.setAssignee(task.getId(), "101");
            taskService.complete(task.getId(), map);

            task = taskService.createTaskQuery().processInstanceId(instance.getId()).singleResult();
            taskService.setAssignee(task.getId(), "101");
        }else {
            log.info("没有任务。");
        }
        log.info("流程提交成功，id为："+instance.getId());

    }

    @Test
    public void test4(){
    }
}
