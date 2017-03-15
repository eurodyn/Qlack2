package com.eurodyn.qlack2.be.workflow.api.dto;

public class WorkflowRuntimeErrorLogDTO {

	private String id;
    private long logDate;
    private String processId;
    private String processInstanceId;
    private String workflowName;
    private String versionName;
    private String traceData;

	public WorkflowRuntimeErrorLogDTO() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getLogDate() {
		return logDate;
	}

	public void setLogDate(long startDate) {
		this.logDate = startDate;
	}
	
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}
	
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
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
	
	public String getTraceData() {
		return traceData;
	}

	public void setTraceData(String traceData) {
		this.traceData = traceData;
	}
}
