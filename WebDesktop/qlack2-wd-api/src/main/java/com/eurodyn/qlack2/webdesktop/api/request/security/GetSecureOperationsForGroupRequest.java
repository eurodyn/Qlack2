package com.eurodyn.qlack2.webdesktop.api.request.security;

import java.util.List;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetSecureOperationsForGroupRequest extends QSignedRequest {
	private String groupId;
	private List<String> operations;
	private String resourceObjectId;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public List<String> getOperations() {
		return operations;
	}

	public void setOperations(List<String> operations) {
		this.operations = operations;
	}

	public String getResourceObjectId() {
		return resourceObjectId;
	}

	public void setResourceObjectId(String resourceObjectId) {
		this.resourceObjectId = resourceObjectId;
	}
}
