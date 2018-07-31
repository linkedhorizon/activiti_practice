package org.lyg.controller;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.*;
import org.apache.commons.io.IOUtils;
import org.lyg.bo.DeploymentBo;
import org.lyg.bo.ProcessDefinitionBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * @author :lyg
 * @time :2018/7/28 0028
 */

@RestController
public class DeploymentController {
    @Autowired
    ProcessEngine processEngine;
    /**
     * 部署流程，操作数据表：act_re_deployment、act_re_procdef
     */
    @RequestMapping(value = "/deploy",method = RequestMethod.POST)
    public String deploy(@RequestParam("zip") MultipartFile zip) throws Exception {
        if(zip == null){
            return "部署失败";
        }
        InputStream in = zip.getInputStream();
        DeploymentBuilder deploymentBuilder = processEngine.getRepositoryService().createDeployment();
        deploymentBuilder.addZipInputStream(new ZipInputStream(in));
        Deployment deployment = deploymentBuilder.deploy();
        return  "部署成功";
    }
    /**
     * 查询部署列表
     * @return
     */
    @RequestMapping("/deployment/query")
    public List<DeploymentBo> deployment_query(){
        DeploymentQuery query = processEngine.getRepositoryService().createDeploymentQuery();
        List<Deployment> list = query.list();
        List<DeploymentBo> deploymentBoList = new ArrayList<DeploymentBo>();
        for(Deployment deployment : list){
            DeploymentBo bo = new DeploymentBo();
            bo.setId(deployment.getId());
            bo.setName(deployment.getName());
            bo.setDeploymentTime(deployment.getDeploymentTime());
            bo.setCategory(deployment.getCategory());
            bo.setTenantId(deployment.getTenantId());
            deploymentBoList.add(bo);
        }
        return deploymentBoList;
    }

    /**
     * 根据deploymentId 删除部署信息
     * @return
     */
    @RequestMapping("/deployment/delete")
    public String deployment_delete(@RequestParam("deploymentId") String deploymentId){
        // 不能删除流程实例已启动的
        processEngine.getRepositoryService().deleteDeployment(deploymentId);
        return "删除成功";
    }
    /**
     * 根据deploymentId 删除部署信息（强制）
     * @return
     */
    @RequestMapping("/deployment/delete_f")
    public String deployment_delete_f(@RequestParam("deploymentId")String deploymentId){
        // 能够删除级联的表信息，即使已经启动流程实例
        processEngine.getRepositoryService().deleteDeployment(deploymentId,true);
        return "删除成功";
    }

    /**
     * 根据deploymentId 获取部署文件信息
     * @param deploymentId
     * @return
     */
    @RequestMapping("/deployment/getDeploymentFilesInfo")
    public String getDeploymentFilesInfo(@RequestParam("deploymentId") String deploymentId){
        //获得两个文件的文件
        List<String> names = processEngine.getRepositoryService().getDeploymentResourceNames(deploymentId);
        // 直接获取图片的输入流
        // InputStream pngInputStream = processEngine.getRepositoryService().getProcessDiagram(processDefinitionId);

        for (String name: names) {
            // 通过循环分别获取文件或图片的输入流
            InputStream in = processEngine.getRepositoryService().getResourceAsStream(deploymentId,name);
        }
        return null;
    }
    /**
     * 查询流程定义列表 act_re_procdef
     */
    @RequestMapping("/processDefinition/query")
    public List<ProcessDefinitionBo> processDefinition_query(){
        ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();
        /*
        // 添加过滤条件
        // 1. 流程定义key为qjkc
        query.processDefinitionKey("qjlc");

        // 2. 添加版本排序条件
        query.orderByProcessDefinitionVersion().desc();

        // 3. 添加分页查询条件
        query.listPage(0,10);
        */
        List<ProcessDefinition> list = query.list();
        List<ProcessDefinitionBo> processDefinitionBoList = new ArrayList<ProcessDefinitionBo>();
        for (ProcessDefinition processDefinition :list){
            ProcessDefinitionBo bo = processDefinitionToBo(processDefinition);
            processDefinitionBoList.add(bo);
        }
        return processDefinitionBoList;
    }
    /**
     * 查询最新版本的流程定义列表
     * @return
     */
    @RequestMapping("processDefinition/latest")
    public List<ProcessDefinitionBo> processDefinition_latest(){
        ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();
        query.orderByProcessDefinitionVersion().asc();
        List<ProcessDefinition> list = query.list();
        Map<String,ProcessDefinition> map = new HashMap<String,ProcessDefinition>();
        for(ProcessDefinition processDefinition : list){
            map.put(processDefinition.getKey(),processDefinition);
        }
        List<ProcessDefinitionBo> processDefinitionBoList = new ArrayList<ProcessDefinitionBo>();
        for (ProcessDefinition processDefinition : map.values()){
            ProcessDefinitionBo bo = processDefinitionToBo(processDefinition);
            processDefinitionBoList.add(bo);
        }
        return processDefinitionBoList;
    }

    private ProcessDefinitionBo processDefinitionToBo(ProcessDefinition processDefinition) {
        ProcessDefinitionBo bo = new ProcessDefinitionBo();
        bo.setId(processDefinition.getId());
        bo.setCategory(processDefinition.getCategory());
        bo.setName(processDefinition.getName());
        bo.setKey(processDefinition.getKey());
        bo.setDescription(processDefinition.getDescription());
        bo.setVersion(processDefinition.getVersion());
        bo.setResourceName(processDefinition.getResourceName());
        bo.setDeploymentId(processDefinition.getDeploymentId());
        bo.setDiagramResourceName(processDefinition.getDiagramResourceName());
        bo.setStartFormKey(processDefinition.hasStartFormKey());
        bo.setGraphicalNotation(processDefinition.hasGraphicalNotation());
        bo.setSuspended(processDefinition.isSuspended());
        bo.setTenantId(processDefinition.getTenantId());
        return bo;
    }

    /**
     * 根据deploymentId 删除流程定义，同根据deploymentId删除部署信息完全一样
     * @param deploymentId
     * @return
     */
    @RequestMapping("processDefinition/delete")
    public String processDefinition_delete(String deploymentId){
        this.deployment_delete_f(deploymentId);
        return "删除成功";
    }
    /**
     * 获取图片
     * @param response
     * @param processDefinitionId
     * @throws IOException
     */
    @RequestMapping("/deployment/getImage")
    public void getProcessImage(HttpServletResponse response, String processDefinitionId) throws IOException {
        InputStream inputStream = processEngine.getRepositoryService().getProcessDiagram(processDefinitionId);
        response.setContentType("image/png");
        ServletOutputStream outputStream = response.getOutputStream();
        IOUtils.copy(inputStream,outputStream);
    }
}
