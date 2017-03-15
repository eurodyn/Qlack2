package com.eurodyn.qlack2.be.workflow.impl.dto;

public class AuditWorkflowInstanceDTO {

    private String processId;
    private Long processInstanceId;
    private String workflowId;
    private String versionId;

	public AuditWorkflowInstanceDTO() {
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
	
}
