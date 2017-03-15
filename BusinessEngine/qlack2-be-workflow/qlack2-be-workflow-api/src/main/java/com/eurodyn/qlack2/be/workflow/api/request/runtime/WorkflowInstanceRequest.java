package com.eurodyn.qlack2.be.workflow.api.request.runtime;

import java.util.List;
import java.util.Map;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class WorkflowInstanceRequest extends QSignedRequest {

	private String id;
	private Map<String, Object> parameters;
	
	private List<byte[]> facts;

	public WorkflowInstanceRequest() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	public List<byte[]> getFacts() {
		return facts;
	}

	public void setFacts(List<byte[]> facts) {
		this.facts = facts;
	}

}
