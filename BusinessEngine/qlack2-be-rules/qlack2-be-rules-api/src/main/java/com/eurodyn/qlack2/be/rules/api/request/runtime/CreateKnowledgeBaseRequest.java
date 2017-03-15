package com.eurodyn.qlack2.be.rules.api.request.runtime;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class CreateKnowledgeBaseRequest extends QSignedRequest {

	private String workingSetVersionId;

	// -- Accessors

	public String getWorkingSetVersionId() {
		return workingSetVersionId;
	}

	public void setWorkingSetVersionId(String workingSetVersionId) {
		this.workingSetVersionId = workingSetVersionId;
	}

}
