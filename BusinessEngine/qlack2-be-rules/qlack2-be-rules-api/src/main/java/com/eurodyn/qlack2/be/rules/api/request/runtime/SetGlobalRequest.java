package com.eurodyn.qlack2.be.rules.api.request.runtime;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class SetGlobalRequest extends QSignedRequest {

	private String sessionId;

	private String globalId;

	private byte[] global;

	// -- Accessors

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getGlobalId() {
		return globalId;
	}

	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}

	public byte[] getGlobal() {
		return global;
	}

	public void setGlobal(byte[] global) {
		this.global = global;
	}

}
