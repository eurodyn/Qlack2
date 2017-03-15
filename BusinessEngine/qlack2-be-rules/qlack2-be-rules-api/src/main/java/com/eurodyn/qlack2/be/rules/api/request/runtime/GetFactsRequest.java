package com.eurodyn.qlack2.be.rules.api.request.runtime;

import java.util.List;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetFactsRequest extends QSignedRequest {

	private String sessionId;

	private List<String> factIds;

	// -- Accessors

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public List<String> getFactIds() {
		return factIds;
	}

	public void setFactIds(List<String> factIds) {
		this.factIds = factIds;
	}

}
