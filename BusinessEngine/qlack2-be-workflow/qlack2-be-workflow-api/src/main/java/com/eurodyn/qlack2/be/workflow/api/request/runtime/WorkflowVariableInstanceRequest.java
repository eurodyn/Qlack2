package com.eurodyn.qlack2.be.workflow.api.request.runtime;

import java.util.List;
import java.util.Map;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class WorkflowVariableInstanceRequest extends QSignedRequest {

	private String id;
	private Long processInstanceId;
	private String variableName;
	private Object variableData;
	
	public WorkflowVariableInstanceRequest() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Long getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(Long instanceId) {
		this.processInstanceId = instanceId;
	}
	
	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	
	public Object getVariableData() {
		return variableData;
	}

	public void setVariableData(Object variableData) {
		this.variableData = variableData;
	}
}
