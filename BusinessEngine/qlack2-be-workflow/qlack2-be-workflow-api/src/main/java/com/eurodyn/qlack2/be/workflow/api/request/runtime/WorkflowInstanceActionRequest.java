package com.eurodyn.qlack2.be.workflow.api.request.runtime;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class WorkflowInstanceActionRequest extends QSignedRequest {

	private String id;
	private Long processInstanceId;

	public WorkflowInstanceActionRequest() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Long getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

}
