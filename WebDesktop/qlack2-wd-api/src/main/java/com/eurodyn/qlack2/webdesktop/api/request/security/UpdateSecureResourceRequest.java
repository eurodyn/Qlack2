package com.eurodyn.qlack2.webdesktop.api.request.security;


public class UpdateSecureResourceRequest {
	private String objectId;
	private String name;
	private String description;

	public UpdateSecureResourceRequest(String objectId, String name,
			String description) {
		this.objectId = objectId;
		this.name = name;
		this.description = description;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
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
}
