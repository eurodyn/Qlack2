package com.eurodyn.qlack2.be.rules.api.request.project;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetProjectWithResourcesRequest extends QSignedRequest {

	private final String projectId;
	private final boolean updateRecentProjects;

	public GetProjectWithResourcesRequest(String projectId) {
		this.projectId = projectId;
		this.updateRecentProjects = true;
	}

	public GetProjectWithResourcesRequest(String projectId, boolean updateRecentProjects) {
		this.projectId = projectId;
		this.updateRecentProjects = updateRecentProjects;
	}

	public String getProjectId() {
		return projectId;
	}

	public boolean isUpdateRecentProjects() {
		return updateRecentProjects;
	}

}
