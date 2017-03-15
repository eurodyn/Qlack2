package com.eurodyn.qlack2.be.rules.impl.dto;

public class AuditRuntimeGlobalDTO {
	private String sessionId;
	private String globalId;

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

}
