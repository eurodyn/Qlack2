package com.eurodyn.qlack2.be.workflow.api.request.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class ImportWorkflowVersionRequest extends QSignedRequest {

	private String workflowId;
	private byte[] versionContent;

	public byte[] getVersionContent() {
		return versionContent;
	}

	public void setVersionContent(byte[] versionContent) {
		this.versionContent = versionContent;
	}
	
	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
}
