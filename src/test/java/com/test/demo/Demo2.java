package com.test.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.List;

public class Demo2 {

    private static final ProcessEngine processEngine;

    static{
        processEngine = ProcessEngines.getDefaultProcessEngine();
    }
    @Test
    public void test1(){
        Deployment deploy = processEngine.getRepositoryService()//流程定义与部署相关的service
                .createDeployment()
                .name("system")
                .addClasspathResource("processes/twouser.bpmn")
                .deploy();//流程部署
        System.out.println(deploy.getId());//2501
        System.out.println(deploy.getName());//system
    }

    @Test
    public void test2(){
        ProcessInstance instance = processEngine.getRuntimeService()
                .startProcessInstanceByKey("twouser");
        System.out.println(instance.getId());//7501
        System.out.println(instance.getName());//null
        System.out.println(instance.getProcessDefinitionId());//twouser:2:2503
    }

    @Test
    public void test3(){
        String assignee="说了多少啦";
        TaskService taskService = processEngine.getTaskService();

        List<Task> list = processEngine.getTaskService()//与正在执行的任务管理相关的Service
                .createTaskQuery()//创建任务查询对象
                .taskAssignee(assignee)//指定个人任务
                .orderByTaskCreateTime().asc()
                .list();
        if (list!=null&&list.size()>0) {
            for (Task task : list) {
                System.out.println("任务ID"+task.getId());
                System.out.println("任务名称"+task.getName());
                System.out.println("任务创建时间"+task.getCreateTime());
                System.out.println("任务的办理人"+task.getAssignee());
                System.out.println("流程实例id:"+task.getProcessInstanceId());
                System.out.println("执行对象id:"+task.getExecutionId());
                System.out.println("流程定义id:"+task.getProcessDefinitionId());

            }
        }
    }

    //自定义sql查询
    @Test
    public void test4(){
        String taskAssignee = "aaaa";
        List<Task> list = processEngine.getTaskService()
                .createNativeTaskQuery().sql("select distinct RES.* from ACT_RU_TASK RES WHERE RES.ASSIGNEE_ = #{taskassignee} order by RES.CREATE_TIME_ asc LIMIT 2147483647 OFFSET 0")
                .parameter("taskassignee",taskAssignee)
                .list();
        for (Task task : list) {
            System.out.println("任务ID:"+task.getId());
            System.out.println("任务名称:"+task.getName());
            System.out.println("任务创建时间:"+task.getCreateTime());
            System.out.println("任务的办理人:"+task.getAssignee());
            System.out.println("流程实例id:"+task.getProcessInstanceId());
            System.out.println("执行对象id:"+task.getExecutionId());
            System.out.println("流程定义id:"+task.getProcessDefinitionId());
        }
    }
}
