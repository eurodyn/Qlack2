package com.eurodyn.qlack2.be.rules.api.request.project;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetProjectCategoriesRequest extends QSignedRequest {

	private final String projectId;

	public GetProjectCategoriesRequest(String projectId) {
		this.projectId = projectId;
	}

	public String getProjectId() {
		return projectId;
	}

}
