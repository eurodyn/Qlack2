package com.eurodyn.qlack2.fuse.workflow.runtime.api.dto;

import java.io.Serializable;
import java.util.Date;

public class ProcessInstanceDesc implements Serializable{

    private static final long serialVersionUID = 7310019271033570922L;

    private Long id;
    private Long processInstanceId;
    private String processId;
    private String processName;
    private String processVersion;
    private int state;
    private String stateDesc;
    private String deploymentId;
    private String initiator;
    private Date startDate;
    private Date endDate;
    private Long duration;

    public ProcessInstanceDesc() {
    
    }

    public ProcessInstanceDesc(Long id, Long processInstanceId, String processId, String processName, String processVersion,
                                int state, String deploymentId, Date startDate, Date endDate, Long duration, String initiator) {
        this.id = id;
        this.processInstanceId = processInstanceId;
        this.processId = processId;
        this.processName = processName;
        this.processVersion = processVersion==null?"":processVersion;
        this.state = state;
        this.deploymentId = deploymentId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.initiator = initiator;
    }
    
    public String getProcessId() {
        return processId;
    }

    public Long getId() {
        return id;
    }
    
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public String getProcessName() {
        return processName;
    }

    public int getState() {
        return state;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
    
    public Long getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "ProcessInstanceDesc{" + "id=" + id + "processInstanceId=" + processInstanceId + ", processId=" + processId + ", processName=" + processName + ", processVersion=" + processVersion + ", state=" + state + ", deploymentId=" + deploymentId + ", initiator=" + initiator + ", startDate=" + startDate + '}';
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public void setState(int state) {
        this.state = state;
    }
    
    public String getStateDesc() {
        return stateDesc;
    }

    public void setStateDesc(String state) {
        this.stateDesc = state;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

}