package com.eurodyn.qlack2.be.workflow.api.request.project;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetProjectResourcesRequest extends QSignedRequest {
	private String projectId;
	private boolean updateRecentProjects = false;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	
	public boolean isUpdateRecentProjects() {
		return updateRecentProjects;
	}

	public void setUpdateRecentProjects(boolean updateRecentProjects) {
		this.updateRecentProjects = updateRecentProjects;
	}
}
