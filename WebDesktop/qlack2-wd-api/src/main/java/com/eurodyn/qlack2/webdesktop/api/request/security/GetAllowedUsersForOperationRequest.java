package com.eurodyn.qlack2.webdesktop.api.request.security;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetAllowedUsersForOperationRequest extends QSignedRequest {
	private String operationName;
	private String resourceObjectId;
	private boolean checkUserGroups;

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

	public boolean isCheckUserGroups() {
		return checkUserGroups;
	}

	public void setCheckUserGroups(boolean checkUserGroups) {
		this.checkUserGroups = checkUserGroups;
	}
}
