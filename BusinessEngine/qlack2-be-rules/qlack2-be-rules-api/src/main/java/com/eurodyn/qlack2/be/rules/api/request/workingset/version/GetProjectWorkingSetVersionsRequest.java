package com.eurodyn.qlack2.be.rules.api.request.workingset.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetProjectWorkingSetVersionsRequest extends QSignedRequest {

	private final String projectId;

	public GetProjectWorkingSetVersionsRequest(String projectId) {
		this.projectId = projectId;
	}

	public String getProjectId() {
		return projectId;
	}

}
