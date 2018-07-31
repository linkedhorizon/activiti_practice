package org.lyg.bo;

import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;

/**
 * @author :lyg
 * @time :2018/7/31 0031
 */
public class ProcessDefinitionBo {
    String id;

    String category;

    String name;

    String key;

    String description;

    int version;
    
    String resourceName;

    String deploymentId;

    String diagramResourceName;

    boolean startFormKey;

    boolean graphicalNotation;

    boolean suspended;

    String tenantId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getDiagramResourceName() {
        return diagramResourceName;
    }

    public void setDiagramResourceName(String diagramResourceName) {
        this.diagramResourceName = diagramResourceName;
    }

    public boolean isStartFormKey() {
        return startFormKey;
    }

    public void setStartFormKey(boolean startFormKey) {
        this.startFormKey = startFormKey;
    }

    public boolean isGraphicalNotation() {
        return graphicalNotation;
    }

    public void setGraphicalNotation(boolean graphicalNotation) {
        this.graphicalNotation = graphicalNotation;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
