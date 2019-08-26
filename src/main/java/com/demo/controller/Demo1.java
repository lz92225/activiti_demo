package com.demo.controller;

import com.demo.utils.ActivitiUtils;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.util.Activiti5Util;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
public class Demo1 {

    @Autowired
    private RuntimeService runtimeService;//流程运行时相关接口

    @Autowired
    private HistoryService historyService;//历史记录相关接口

    @Autowired
    private RepositoryService repositoryService;//流程定义与部署的相关接口  xml<--->数据库

    @Autowired
    private ProcessDiagramGenerator processDiagramGenerator;//流程图生成器

    Logger logger = LoggerFactory.getLogger(Demo1.class);

    @RequestMapping(value = "/aa")
    public String test(){
        return "aaa";
    }
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public String start() {
        String key = "lztest";
        logger.info("开始流程。。。");

        Map<String, Object> map = new HashMap<>();
        map.put("name", "lz");
        map.put("data", "new data");
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(key, map);

        logger.info("启动流程的实例：" + instance);
        logger.info("流程id：" + instance.getId());
        logger.info("流程name：" + instance.getName());

        return instance.getId();
    }


    @RequestMapping(value = "/showIMG/{instanceid}", method = RequestMethod.GET)
    public void showIMG(@PathVariable("instanceid") String instanceID, HttpServletResponse response) {
        HistoricActivityInstance processInstance = historyService.createHistoricActivityInstanceQuery().processInstanceId(instanceID).singleResult();
        if (processInstance == null) {
            logger.info("无对应的流程实例:" + instanceID);
            return;
        }

        //根据流程对象获取流程模版
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());

        //构造历史流程查询
        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery().processInstanceId(instanceID);
        //获取流程历史顺序
        List<HistoricActivityInstance> list = historicActivityInstanceQuery.orderByHistoricActivityInstanceStartTime().asc().list();
        if (list == null && list.size() <= 0) {
            logger.info("流程id（）对应的流程没有历史节点。" + instanceID);
            return;
        }

        //获取当前流程中的每个节点id
        List<String> idList = list.stream().map(item -> item.getActivityId()).collect(Collectors.toList());

        /*
         *  获取流程走过的线
         */
        // 获取流程定义
        ProcessDefinitionEntity deployedProcessDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        List<String> highLightedFlows = ActivitiUtils.getHighLightedFlows(bpmnModel, deployedProcessDefinition, list);
        outputImg(response, bpmnModel, highLightedFlows, idList);
    }

    /**
     * <p>输出图像</p>
     *
     * @param response               响应实体
     * @param bpmnModel              图像对象
     * @param flowIds                已执行的线集合
     * @param executedActivityIdList void 已执行的节点ID集合
     */
    private void outputImg(HttpServletResponse response, BpmnModel bpmnModel, List<String> flowIds, List<String> executedActivityIdList) {
        InputStream imageStream = null;
        try {
            imageStream = processDiagramGenerator.generateDiagram(bpmnModel, executedActivityIdList, flowIds, "宋体", "微软雅黑", "黑体", true, "png");
            // 输出资源内容到相应对象
            byte[] b = new byte[1024];
            int len;
            while ((len = imageStream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
            response.getOutputStream().flush();
        } catch (Exception e) {
            logger.error("流程图输出异常！", e);
        } finally { // 流关闭
            try {
                imageStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}