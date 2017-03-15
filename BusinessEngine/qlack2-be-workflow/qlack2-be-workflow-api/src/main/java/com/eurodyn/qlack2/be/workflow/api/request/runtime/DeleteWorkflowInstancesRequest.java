package com.eurodyn.qlack2.be.workflow.api.request.runtime;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class DeleteWorkflowInstancesRequest extends QSignedRequest {

	private String workflowId;

	public DeleteWorkflowInstancesRequest() {
		super();
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String id) {
		this.workflowId = id;
	}

}
