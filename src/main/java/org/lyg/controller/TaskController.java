package org.lyg.controller;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.lyg.bo.TaskBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author :lyg
 * @time :2018/7/29 0029
 */
@RestController
public class TaskController {
    @Autowired
    private ProcessEngine processEngine;

    /**
     * 查询任务列表 --根据用户名查询
     *
     * @return taskId 集合
     */
    @RequestMapping("/task/query")
    public List<TaskBo> query(@RequestParam("assignee") String assignee) {
        TaskQuery query = processEngine.getTaskService().createTaskQuery();

        // 根据用户名查询
        query.taskAssignee(assignee);
        query.orderByTaskCreateTime().desc();
        List<Task> list = query.list();
        List<TaskBo> taskBoList = new ArrayList<TaskBo>();
        for (Task task : list) {
            taskBoList.add(taskToTaskBo(task));
        }
        return taskBoList;
    }

    /**
     * 查询任务 --根据 taskId 查询（单个任务）
     *
     * @param taskId
     * @return
     */
    @RequestMapping("/task/query_id")
    public TaskBo query_id(@RequestParam("taskId") String taskId) {
        TaskQuery query = processEngine.getTaskService().createTaskQuery();
        Task task = query.taskId(taskId).singleResult();
        return taskToTaskBo(task);
    }

    /**
     * 查询公共任务 -- 根据候选人过滤
     *
     * @param candidateUser
     * @return
     */
    @RequestMapping("/task/common/query")
    public List<TaskBo> query_common(@RequestParam("candidateUser") String candidateUser) {
        TaskQuery query = processEngine.getTaskService().createTaskQuery();
        query.taskCandidateUser(candidateUser);
        List<Task> list = query.list();
        List<TaskBo> taskBoList = new ArrayList<TaskBo>();
        for (Task task : list) {
            taskBoList.add(taskToTaskBo(task));
        }
        return taskBoList;
    }

    /**
     * 拾取任务 把公共任务变成个人任务
     *
     * @param taskId
     * @param userId
     * @return
     */
    public String changeCommonToPrivate(String taskId, String userId) {
        processEngine.getTaskService().claim(taskId, userId);
        return "公共任务变成个人任务";
    }

    /**
     * 退回任务 将个人任务变为公共任务
     *
     * @param taskId
     * @return
     */
    public String changePrivateToCommon(String taskId) {
        processEngine.getTaskService().setAssignee(taskId, null);
        return "个人任务变成公共任务";
    }

    /**
     * 根据taskId执行任务，推送到下一步
     *
     * @return
     */
    @RequestMapping("/task/execute")
    public String execute(String taskId) {
        processEngine.getTaskService().complete(taskId);
        return "任务执行完成";
    }

    protected void executeAndSetVariables(String taskId) {
        // 设置流程变量方式二：在办理任务时设置

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("经理审批", "同意");

        processEngine.getTaskService().complete(taskId, map);
    }

    /**
     * 设置流程变量方式四：使用TaskService的方法设置
     *
     */
    public void setVariable(String taskId) {
        processEngine.getTaskService().setVariable(taskId, "行政审批", "批准");
    }

    /**
     * 获取流程变量方式二：使用TaskService方法获取
     *
     * @param taskId
     * @return
     */
    public String getVariables(String taskId) {
        processEngine.getTaskService().getVariables(taskId);
        return null;
    }
    @RequestMapping("/task/getCoordinates")
    public Map<String,Integer> getCoordinatesByTaskId(String taskId) {
        TaskQuery query = processEngine.getTaskService().createTaskQuery();
        query.taskId(taskId);
        Task task = query.singleResult();
        // 根据任务获取流程定义id 和流程实例id
        String processDefinitionId = task.getProcessDefinitionId();
        String processInstanceId = task.getProcessInstanceId();

        // 根据流程定义id 获取流程定义
        ProcessDefinition processDefinition = processEngine.getRepositoryService().getProcessDefinition(processDefinitionId);

        // 根据流程实例id 获取流程实例
        ProcessInstanceQuery processInstanceQuery = processEngine.getRuntimeService().createProcessInstanceQuery();
        processInstanceQuery.processDefinitionId(processDefinitionId);
        ProcessInstance processInstance = processInstanceQuery.singleResult();

        // 获取活动节点
        String activityId = processInstance.getActivityId();

        // 流程定义实例 根据活动节点id返回坐标
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) processDefinition;
        ActivityImpl activity = processDefinitionEntity.findActivity(activityId);
        int x = activity.getX();
        int y = activity.getY();
        int height = activity.getHeight();
        int width = activity.getWidth();

        Map<String,Integer> map = new HashMap<String,Integer>();
        map.put("x",x);
        map.put("y",y);
        map.put("height",height);
        map.put("width",width);
        return map;
    }


    // 一般要管理员权限的来做
    // 处理接收任务

    /**
     * 直接将流程向下执行一步
     *
     * @param executionId
     * @return
     */
    @RequestMapping("/task/next_f")
    public String next_f(String executionId) {
        processEngine.getRuntimeService().signal(executionId);
        return null;
    }

    /**
     * 将task 转换成 taskBo
     * @param task
     * @return
     */
    private TaskBo taskToTaskBo(Task task){
        TaskBo bo = new TaskBo();
        bo.setId(task.getId());
        bo.setName(task.getName());
        bo.setDescription(task.getDescription());
        bo.setPriority(task.getPriority());
        bo.setOwner(task.getOwner());
        bo.setAssignee(task.getAssignee());
        bo.setProcessInstanceId(task.getProcessInstanceId());
        bo.setExecutionId(task.getExecutionId());
        bo.setProcessDefinitionId(task.getProcessDefinitionId());
        bo.setCreateTime(task.getCreateTime());
        bo.setTaskDefinitionKey(task.getTaskDefinitionKey());
        bo.setDueDate(task.getDueDate());
        bo.setCategory(task.getCategory());
        bo.setParentTaskId(task.getParentTaskId());
        bo.setTenantId(task.getTenantId());
        bo.setFormKey(task.getFormKey());
        bo.setTaskLocalVariables(task.getTaskLocalVariables());
        bo.setProcessVariables(task.getProcessVariables());
        bo.setDelegationState(task.getDelegationState());
        return bo;
    }
}
