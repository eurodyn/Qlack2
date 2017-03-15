package com.eurodyn.qlack2.be.workflow.api.request.project;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetWorkingSetRulesRequest extends QSignedRequest {
	private String projectId;
	private String workingSetId;
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getWorkingSetId() {
		return workingSetId;
	}
	public void setWorkingSetId(String workingSetId) {
		this.workingSetId = workingSetId;
	}


}
