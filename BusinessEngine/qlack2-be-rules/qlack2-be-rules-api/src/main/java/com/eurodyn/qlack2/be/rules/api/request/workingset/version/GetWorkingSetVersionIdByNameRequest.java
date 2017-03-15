package com.eurodyn.qlack2.be.rules.api.request.workingset.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetWorkingSetVersionIdByNameRequest extends QSignedRequest {

	private String projectId;
	private String workingSetName;
	private String name;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getWorkingSetName() {
		return workingSetName;
	}

	public void setWorkingSetName(String workingSetName) {
		this.workingSetName = workingSetName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
