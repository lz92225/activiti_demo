package com.test.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;

import java.util.List;

public class Demo1 {

    @Test
    public void startProcess(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        DeploymentBuilder builder = repositoryService.createDeployment();
        builder.addClasspathResource("processes/my_process.bpmn");
        builder.deploy();
        // select * from `ACT_GE_PROPERTY`;这时这个表中会多条数据

        List<ProcessDefinition> p = repositoryService.createProcessDefinitionQuery().list();
        for(int i=0;i<p.size();i++){
            System.out.println(p.get(i).getKey());
        }
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.startProcessInstanceByKey("myProcess");//启动流程，ID必须与你配置的一致

        System.out.println("ok......");
    }
}
