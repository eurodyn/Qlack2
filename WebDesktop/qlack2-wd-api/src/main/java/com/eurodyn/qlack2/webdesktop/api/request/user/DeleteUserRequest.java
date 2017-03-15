package com.eurodyn.qlack2.webdesktop.api.request.user;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class DeleteUserRequest extends QSignedRequest {
	private String userId;

	public DeleteUserRequest(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
