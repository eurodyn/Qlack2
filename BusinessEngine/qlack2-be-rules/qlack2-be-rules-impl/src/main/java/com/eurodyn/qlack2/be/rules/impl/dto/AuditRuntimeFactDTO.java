package com.eurodyn.qlack2.be.rules.impl.dto;

public class AuditRuntimeFactDTO {
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
