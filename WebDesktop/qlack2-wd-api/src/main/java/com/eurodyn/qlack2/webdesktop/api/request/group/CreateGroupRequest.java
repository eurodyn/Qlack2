package com.eurodyn.qlack2.webdesktop.api.request.group;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class CreateGroupRequest extends QSignedRequest {
	private String name;
	private String description;
	private String parentGroupId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParentGroupId() {
		return parentGroupId;
	}

	public void setParentGroupId(String parentGroupId) {
		this.parentGroupId = parentGroupId;
	}
}
