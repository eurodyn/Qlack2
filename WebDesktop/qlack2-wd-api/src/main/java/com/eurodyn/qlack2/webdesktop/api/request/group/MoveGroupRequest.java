package com.eurodyn.qlack2.webdesktop.api.request.group;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class MoveGroupRequest extends QSignedRequest {
	private String id;
	private String newParentId;

	public MoveGroupRequest(String id, String newParentId) {
		this.id = id;
		this.newParentId = newParentId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNewParentId() {
		return newParentId;
	}

	public void setNewParentId(String newParentId) {
		this.newParentId = newParentId;
	}
}
