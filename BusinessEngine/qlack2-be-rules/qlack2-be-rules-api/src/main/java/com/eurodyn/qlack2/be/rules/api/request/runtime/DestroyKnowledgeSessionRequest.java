package com.eurodyn.qlack2.be.rules.api.request.runtime;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class DestroyKnowledgeSessionRequest extends QSignedRequest {

	private String sessionId;

	// -- Accessors

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
