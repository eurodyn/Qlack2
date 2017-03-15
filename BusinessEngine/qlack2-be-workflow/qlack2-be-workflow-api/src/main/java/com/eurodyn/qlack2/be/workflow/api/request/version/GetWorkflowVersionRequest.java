package com.eurodyn.qlack2.be.workflow.api.request.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetWorkflowVersionRequest extends QSignedRequest {

	private String versionId;

	public GetWorkflowVersionRequest() {
		super();
	}

	public String getVersionId() {
		return versionId;
	}
	
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

}
