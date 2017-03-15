package com.eurodyn.qlack2.webdesktop.api.request.user;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetUserRequest extends QSignedRequest {
	private String userId;
	private boolean includeGroups;

	public GetUserRequest(String userId, boolean includeGroups) {
		this.userId = userId;
		this.includeGroups = includeGroups;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isIncludeGroups() {
		return includeGroups;
	}

	public void setIncludeGroups(boolean includeGroups) {
		this.includeGroups = includeGroups;
	}
}
