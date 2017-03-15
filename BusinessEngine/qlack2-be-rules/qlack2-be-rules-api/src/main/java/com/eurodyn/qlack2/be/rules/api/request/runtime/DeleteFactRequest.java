package com.eurodyn.qlack2.be.rules.api.request.runtime;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class DeleteFactRequest extends QSignedRequest {

	private String sessionId;

	private String factId;

	// -- Accessors

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getFactId() {
		return factId;
	}

	public void setFactId(String factId) {
		this.factId = factId;
	}

}
