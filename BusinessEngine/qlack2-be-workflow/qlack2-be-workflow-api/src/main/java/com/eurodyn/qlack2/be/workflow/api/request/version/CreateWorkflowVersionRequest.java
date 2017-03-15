package com.eurodyn.qlack2.be.workflow.api.request.version;

import java.util.List;

import com.eurodyn.qlack2.be.workflow.api.dto.ConditionDTO;
import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class CreateWorkflowVersionRequest extends QSignedRequest {
	private String name;

	private String description;

	private String content;
	
	private List<ConditionDTO> conditions;

	private String workflowId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<ConditionDTO> getConditions() {
		return conditions;
	}

	public void setConditions(List<ConditionDTO> conditions) {
		this.conditions = conditions;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

}
