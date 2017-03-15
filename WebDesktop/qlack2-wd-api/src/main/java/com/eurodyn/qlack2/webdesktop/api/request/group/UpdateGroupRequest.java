package com.eurodyn.qlack2.webdesktop.api.request.group;

import java.util.List;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class UpdateGroupRequest extends QSignedRequest {
	private String id;
	private String name;
	private String description;
	private List<String> userIds;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}
}
