package com.eurodyn.qlack2.webdesktop.api.request.group;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetGroupRequest extends QSignedRequest {
	private String groupId;
	private boolean includeRelatives;
	private boolean includeUsers;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
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
