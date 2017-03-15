package com.eurodyn.qlack2.be.workflow.api.request.workflow;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class DeleteWorkflowRequest extends QSignedRequest {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
