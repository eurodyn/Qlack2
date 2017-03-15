package com.eurodyn.qlack2.be.forms.api.request.project;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetProjectRequest extends QSignedRequest {
	private String projectId;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

}
