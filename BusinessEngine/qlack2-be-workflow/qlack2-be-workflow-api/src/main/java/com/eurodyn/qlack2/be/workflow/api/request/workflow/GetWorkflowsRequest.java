package com.eurodyn.qlack2.be.workflow.api.request.workflow;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetWorkflowsRequest extends QSignedRequest {

	private String projectId;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

}
