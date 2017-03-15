package com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.request.config;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetManagedGroupsRequest extends QSignedRequest {
	private boolean includeUsers;
	private boolean includeRelatives;
	private String applicationId;

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

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
}
