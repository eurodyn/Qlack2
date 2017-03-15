package com.eurodyn.qlack2.webdesktop.api.request.user;


public class GetUserUncheckedRequest {
	private String userId;
	private boolean includeGroups;

	public GetUserUncheckedRequest(String userId, boolean includeGroups) {
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
