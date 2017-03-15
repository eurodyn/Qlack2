package com.eurodyn.qlack2.webdesktop.api.request.security;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class AllowSecureOperationForUserRequest extends QSignedRequest {
	private String userId;
	private String operationName;
	private String resourceObjectId;

	public AllowSecureOperationForUserRequest() {
	}

	public AllowSecureOperationForUserRequest(String userId,
			String operationName) {
		this.userId = userId;
		this.operationName = operationName;
	}

	public AllowSecureOperationForUserRequest(String groupId,
			String operationName, String resourceObjectId) {
		this.userId = groupId;
		this.operationName = operationName;
		this.resourceObjectId = resourceObjectId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

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

}
