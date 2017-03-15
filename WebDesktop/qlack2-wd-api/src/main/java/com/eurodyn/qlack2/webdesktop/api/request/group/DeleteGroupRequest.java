package com.eurodyn.qlack2.webdesktop.api.request.group;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class DeleteGroupRequest extends QSignedRequest {
	private String id;

	public DeleteGroupRequest(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
