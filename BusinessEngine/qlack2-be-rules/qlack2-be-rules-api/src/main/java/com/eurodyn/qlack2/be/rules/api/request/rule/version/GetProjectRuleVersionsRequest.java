package com.eurodyn.qlack2.be.rules.api.request.rule.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetProjectRuleVersionsRequest extends QSignedRequest {

	private final String projectId;

	public GetProjectRuleVersionsRequest(String projectId) {
		this.projectId = projectId;
	}

	public String getProjectId() {
		return projectId;
	}

}
