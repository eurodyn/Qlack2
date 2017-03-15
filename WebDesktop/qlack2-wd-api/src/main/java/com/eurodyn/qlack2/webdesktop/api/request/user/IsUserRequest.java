package com.eurodyn.qlack2.webdesktop.api.request.user;

public class IsUserRequest {
	private String userId;

	public IsUserRequest(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
