package com.eurodyn.qlack2.webdesktop.api.request.security;

public class DeleteSecureResourceRequest {
	private String resourceObjectId;

	public DeleteSecureResourceRequest(String resourceObjectId) {
		this.resourceObjectId = resourceObjectId;
	}

	public String getResourceObjectId() {
		return resourceObjectId;
	}

	public void setResourceObjectId(String resourceObjectId) {
		this.resourceObjectId = resourceObjectId;
	}
}
