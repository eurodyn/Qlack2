package com.eurodyn.qlack2.be.workflow.api.request.workflow;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetWorkflowByNameRequest extends QSignedRequest {

	private String name;

	// -- Accessors

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
