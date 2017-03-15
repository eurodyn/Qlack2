package com.eurodyn.qlack2.be.workflow.api.request.category;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class CreateCategoryRequest extends QSignedRequest {

	private String name;

	private String description;

	private String projectId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

}
