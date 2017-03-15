package com.eurodyn.qlack2.be.workflow.api.request.runtime;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetWorkflowInstancesRequest extends QSignedRequest {

	private String projectId;

	public GetWorkflowInstancesRequest() {
		super();
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

}
