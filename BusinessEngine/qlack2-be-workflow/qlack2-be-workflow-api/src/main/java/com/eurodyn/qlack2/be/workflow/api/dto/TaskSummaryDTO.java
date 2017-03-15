package com.eurodyn.qlack2.be.workflow.api.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskSummaryDTO {

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

    private Long parentId;
    private List<String> potentialOwnerGroups = new ArrayList<>();

    public TaskSummaryDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name= name;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long id) {
        this.processInstanceId = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description= desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status= status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority= priority;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public void setActualOwner(String owner) {
        this.actualOwner= owner;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy= createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn= createdOn;
    }

    public Date getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(Date activationTime) {
        this.activationTime= activationTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime= expirationTime;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId= processId;
    }

    public long getProcessSessionId() {
        return processSessionId;
    }

    public void setProcessSessionId(long processSessionId) {
        this.processSessionId= processSessionId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId= parentId;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId= deploymentId;
    }
    
    public List<String> getPotentialOwnerGroups() {
        return potentialOwnerGroups;
    }
    
    public void setPotentialOwnerGroups(List<String> owners) {
        this.potentialOwnerGroups= owners;
    }

}