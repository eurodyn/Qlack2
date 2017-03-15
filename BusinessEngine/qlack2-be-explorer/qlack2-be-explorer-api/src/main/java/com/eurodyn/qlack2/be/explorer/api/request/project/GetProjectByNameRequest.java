package com.eurodyn.qlack2.be.explorer.api.request.project;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetProjectByNameRequest extends QSignedRequest {
	private String name;

	public GetProjectByNameRequest() {
	}

	public GetProjectByNameRequest(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
