package org.lyg.controller;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author :lyg
 * @time :2018/7/29 0029
 */
@RestController
public class HistoryController {
    @Autowired
    private ProcessEngine processEngine;

    /**
     * 查询历史流程实例列表
     * @return
     */
    @RequestMapping("/history/query")
    public String history_query(){
        HistoricProcessInstanceQuery query = processEngine.getHistoryService().createHistoricProcessInstanceQuery();
        List<HistoricProcessInstance> list = query.list();
        return null;
    }

    /**
     * 查询历史活动数据列表 流程实例执行的详细步骤
     * @return
     */
    @RequestMapping("/history/query/activiti")
    public String history_query_activiti(){
        HistoricActivityInstanceQuery query = processEngine.getHistoryService().createHistoricActivityInstanceQuery();
        // 按照流程实例排序
        query.orderByProcessInstanceId().desc();
        query.orderByHistoricActivityInstanceEndTime().asc();
        List<HistoricActivityInstance> list = query.list();
        return null;
    }
    /**
     * 查询历史任务数据列表
     * @return
     */
    @RequestMapping("/history/query/task")
    public String history_query_task(){
        HistoricTaskInstanceQuery query = processEngine.getHistoryService().createHistoricTaskInstanceQuery();
        // 按照流程实例排序
        query.orderByProcessInstanceId().asc();

        query.orderByHistoricTaskInstanceEndTime().desc();
        List<HistoricTaskInstance> list = query.list();
        return null;
    }
}
