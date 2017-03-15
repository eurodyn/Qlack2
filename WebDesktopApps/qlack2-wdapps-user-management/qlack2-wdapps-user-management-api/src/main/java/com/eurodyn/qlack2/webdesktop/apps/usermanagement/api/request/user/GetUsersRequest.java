package com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.user;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetUsersRequest extends QSignedRequest {
	private String groupId;
	private boolean includeGroups;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public boolean isIncludeGroups() {
		return includeGroups;
	}

	public void setIncludeGroups(boolean includeGroups) {
		this.includeGroups = includeGroups;
	}
}
