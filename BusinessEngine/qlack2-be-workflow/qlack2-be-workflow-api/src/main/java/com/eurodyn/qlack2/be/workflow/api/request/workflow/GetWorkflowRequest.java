package com.eurodyn.qlack2.be.workflow.api.request.workflow;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetWorkflowRequest extends QSignedRequest {

	private String projectId;

	private String workflowId;

	public GetWorkflowRequest() {
		super();
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

}
