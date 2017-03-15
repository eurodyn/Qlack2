package com.eurodyn.qlack2.be.rules.api.request.rule.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetRuleVersionIdByNameRequest extends QSignedRequest {

	private String projectId;
	private String ruleName;
	private String name;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
