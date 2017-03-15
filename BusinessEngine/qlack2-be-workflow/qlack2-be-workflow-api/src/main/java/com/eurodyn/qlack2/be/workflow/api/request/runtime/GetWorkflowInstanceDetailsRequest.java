package com.eurodyn.qlack2.be.workflow.api.request.runtime;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetWorkflowInstanceDetailsRequest extends QSignedRequest {

	private Long instanceId;

	public GetWorkflowInstanceDetailsRequest() {
		super();
	}

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

}
