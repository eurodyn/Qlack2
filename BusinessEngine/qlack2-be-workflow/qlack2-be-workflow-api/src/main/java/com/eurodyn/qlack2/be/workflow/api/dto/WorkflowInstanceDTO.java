package com.eurodyn.qlack2.be.workflow.api.dto;

public class WorkflowInstanceDTO {

	private Long id;
    private Long duration;
    private long startDate;
    private long endDate;
    private String processId;
    private Long processInstanceId;
    private String processName;
    private String workflowId;
    private String versionId;
    private String workflowName;
    private String versionName;
    private int status;
    private String statusDesc;

	public WorkflowInstanceDTO() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}
	
	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}
	
	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}
	
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}
	
	public Long getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String id) {
		this.workflowId = id;
	}
	
	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String id) {
		this.versionId = id;
	}
	
	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String name) {
		this.workflowName = name;
	}
	
	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String name) {
		this.versionName = name;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String status) {
		this.statusDesc = status;
	}
}
