package com.eurodyn.qlack2.be.workflow.api.request.category;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetCategoriesRequest extends QSignedRequest {

	private String projectId;

	public GetCategoriesRequest() {
		super();
	}

	public String getProjectId() {
		return projectId;
	}
	
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

}
