package com.eurodyn.qlack2.be.workflow.api.request.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetWorkflowVersionIdByNameRequest extends QSignedRequest {
	private String workflowVersionName;

	private String workflowId;

	public String getWorkflowVersionName() {
		return workflowVersionName;
	}

	public void setWorkflowVersionName(String workflowVersionName) {
		this.workflowVersionName = workflowVersionName;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
}
