package com.eurodyn.qlack2.webdesktop.api.request.security;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetAllowedGroupsForOperationRequest extends QSignedRequest {
	private String operationName;
	private String resourceObjectId;
	private boolean checkAncestors;
	private boolean includeRelatives;
	private boolean includeUsers;

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getResourceObjectId() {
		return resourceObjectId;
	}

	public void setResourceObjectId(String resourceObjectId) {
		this.resourceObjectId = resourceObjectId;
	}

	public boolean isCheckAncestors() {
		return checkAncestors;
	}

	public void setCheckAncestors(boolean checkAncestors) {
		this.checkAncestors = checkAncestors;
	}

	public boolean isIncludeRelatives() {
		return includeRelatives;
	}

	public void setIncludeRelatives(boolean includeRelatives) {
		this.includeRelatives = includeRelatives;
	}

	public boolean isIncludeUsers() {
		return includeUsers;
	}

	public void setIncludeUsers(boolean includeUsers) {
		this.includeUsers = includeUsers;
	}
}
