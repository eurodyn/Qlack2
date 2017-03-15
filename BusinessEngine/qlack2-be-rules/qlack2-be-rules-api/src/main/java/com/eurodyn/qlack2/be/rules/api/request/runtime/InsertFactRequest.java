package com.eurodyn.qlack2.be.rules.api.request.runtime;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class InsertFactRequest extends QSignedRequest {

	private String sessionId;

	private byte[] fact;

	// -- Accessors

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public byte[] getFact() {
		return fact;
	}

	public void setFact(byte[] fact) {
		this.fact = fact;
	}

}
