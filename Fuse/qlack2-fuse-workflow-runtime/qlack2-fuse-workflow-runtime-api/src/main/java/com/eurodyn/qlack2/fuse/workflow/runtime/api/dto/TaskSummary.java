package com.eurodyn.qlack2.fuse.workflow.runtime.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskSummary implements Serializable{

    private static final long serialVersionUID = 7310019271033570922L;

    private Long id;
    private String name;
    private String description;
    // Was Status
    private String status;
    private int priority;

    private String actualOwner;
    private String createdBy;
    private Date createdOn;
    private Date activationTime;
    private Date expirationTime;
    private Long processInstanceId;
    private String processId;
    private long processSessionId;
    private String deploymentId;
    private List<String> potentialOwnerGroups = new ArrayList<>();

    private Long parentId;

    public TaskSummary(long id, String name, String description, String status,
            int priority, String actualOwner, String createdBy, Date createdOn, Date activationTime,
            Date expirationTime, String processId, long processSessionId, long processInstanceId, String deploymentId, long parentId, List<String> potentialOwners) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.actualOwner = actualOwner;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.activationTime = activationTime;
        this.expirationTime = expirationTime;
        this.processId = processId;
        this.processSessionId = processSessionId;
        this.processInstanceId = processInstanceId;
        this.deploymentId = deploymentId;
        this.parentId = parentId;
        this.potentialOwnerGroups = potentialOwners;
    }

    public TaskSummary() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public int getPriority() {
        return priority;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getActivationTime() {
        return activationTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public String getProcessId() {
        return processId;
    }

    public long getProcessSessionId() {
        return processSessionId;
    }

    public long getParentId() {
        return parentId;
    }

    public String getDeploymentId() {
        return deploymentId;
    }
    
    public List<String> getPotentialOwnerGroups() {
        return potentialOwnerGroups;
    }

    @Override
    public String toString() {
        return "TaskSummary [id=" + id + ", name=" + name + ", description=" + description + ", deploymentId=" + deploymentId
                + ", status=" + status + ", priority=" + priority + ", parentId=" + parentId
                + ", actualOwner=" + actualOwner + ", createdBy=" + createdBy + ", createdOn=" + createdOn
                + ", activationTime=" + activationTime + ", expirationTime=" + expirationTime + ", processInstanceId="
                + processInstanceId + ", processId=" + processId + ", processSessionId=" + processSessionId + "]";
    }

}