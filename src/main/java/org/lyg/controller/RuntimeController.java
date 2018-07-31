package org.lyg.controller;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.apache.commons.io.IOUtils;
import org.lyg.bo.ProcessInstanceBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :lyg
 * @time :2018/7/28 0028
 */
@RestController
public class RuntimeController {

    @Autowired
    private ProcessEngine processEngine;

    /**
     * 启动一个流程实例：1、根据processDefinitionId
     * @return
     */
    @RequestMapping(value = "/runtime/start")
    public String start(@RequestParam("processDefinitionId") String processDefinitionId){
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceById(processDefinitionId);

        return "流程实例启动成功";
    }

    /**
     * 启动一个流程实例：2、根据流程定义的key(推荐 自动选择最新版本的流程定义启动流程实例)
     * @param key
     * @return
     */
    @RequestMapping(value = "/runtime/start_key")
    public String start_key(@RequestParam("key") String key){
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey(key);

        // 设置流程变量方式一：在启动流程实例时设置
        /*
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("请假天数",5);
        map.put("原因：","生病");
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey(key,map);
        */

        return "流程实例启动成功";
    }

    /**
     * 查询流程实例列表，查询act_ru_execution表
     * @return
     */
    @RequestMapping("/runtime/query")
    public List<ProcessInstanceBo> runtimeQuery(){
        ProcessInstanceQuery query = processEngine.getRuntimeService().createProcessInstanceQuery();
        List<ProcessInstance> list = query.list();
        List<ProcessInstanceBo> processInstanceBoList = new ArrayList<ProcessInstanceBo>();
        for(ProcessInstance processInstance : list){
            processInstanceBoList.add(processInstanceToBo(processInstance));
        }
        return processInstanceBoList;
    }

    /**
     * 结束流程实例 act_ru_execution、act_ru_task
     * @return
     */
    @RequestMapping("/runtime/end")
    public String end(String processInstanceId){
        processEngine.getRuntimeService().deleteProcessInstance(processInstanceId,"my reason");
        return "结束流程实例";
    }

    /**
     * 设置流程变量方式三：使用RuntimeService的方法设置
     * @param executionId
     * @return
     */
    public String setVariables(String executionId){
        processEngine.getRuntimeService().setVariable(executionId,"请假类型","五一拼假");
        return null;
    }
    /**
     * 获取流程变量方式一：使用RuntimeService方法获取
     * @param executionId
     * @param variableName
     * @return
     */
    public Object getVariable(String executionId,String variableName){
        Object variable = processEngine.getRuntimeService().getVariable(executionId, variableName);
        return variable;
    }

    /**
     * 将processInstancce 转换为 processInstanceBo
     * @param processInstance
     * @return
     */
    private ProcessInstanceBo processInstanceToBo(ProcessInstance processInstance){
        ProcessInstanceBo bo = new ProcessInstanceBo();
        bo.setId(processInstance.getId());
        bo.setEnded(processInstance.isEnded());
        bo.setActivityId(processInstance.getActivityId());
        bo.setProcessInstanceId(processInstance.getProcessInstanceId());
        bo.setParentId(processInstance.getParentId());
        bo.setProcessDefinitionId(processInstance.getProcessDefinitionId());
        bo.setProcessDefinitionName(processInstance.getProcessDefinitionName());
        bo.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
        bo.setProcessDefinitionVersion(processInstance.getProcessDefinitionVersion());
        bo.setDeploymentId(processInstance.getDeploymentId());
        bo.setBusinessKey(processInstance.getBusinessKey());
        bo.setSuspended(processInstance.isSuspended());
        Map<String, Object> processVariables;
        bo.setProcessVariables(processInstance.getProcessVariables());
        bo.setTenantId(processInstance.getTenantId());
        bo.setName(processInstance.getName());
        bo.setDescription(processInstance.getDescription());
        bo.setLocalizedName(processInstance.getLocalizedName());
        bo.setLocalizedDescription(processInstance.getLocalizedDescription());
        return bo;
    }
}
