package com.eurodyn.qlack2.be.rules.api.request.config;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetManagedGroupsRequest extends QSignedRequest {
	private boolean includeUsers;
	private boolean includeRelatives;
	private String resourceId;

	public boolean isIncludeUsers() {
		return includeUsers;
	}

	public void setIncludeUsers(boolean includeUsers) {
		this.includeUsers = includeUsers;
	}

	public boolean isIncludeRelatives() {
		return includeRelatives;
	}

	public void setIncludeRelatives(boolean includeRelatives) {
		this.includeRelatives = includeRelatives;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
}
