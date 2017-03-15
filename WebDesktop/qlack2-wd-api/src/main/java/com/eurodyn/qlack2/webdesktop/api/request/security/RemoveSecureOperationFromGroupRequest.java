package com.eurodyn.qlack2.webdesktop.api.request.security;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class RemoveSecureOperationFromGroupRequest extends QSignedRequest {
	private String groupId;
	private String operationName;
	private String resourceObjectId;

	public RemoveSecureOperationFromGroupRequest() {
	}

	public RemoveSecureOperationFromGroupRequest(String groupId,
			String operationName) {
		this.groupId = groupId;
		this.operationName = operationName;
	}

	public RemoveSecureOperationFromGroupRequest(String groupId,
			String operationName, String resourceObjectId) {
		this.groupId = groupId;
		this.operationName = operationName;
		this.resourceObjectId = resourceObjectId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
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
