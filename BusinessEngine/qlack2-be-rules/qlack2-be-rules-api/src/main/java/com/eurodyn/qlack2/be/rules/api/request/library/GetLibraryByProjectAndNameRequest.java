package com.eurodyn.qlack2.be.rules.api.request.library;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetLibraryByProjectAndNameRequest extends QSignedRequest {

	private String projectId;
	private String name;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
