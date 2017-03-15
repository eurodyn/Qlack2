package com.eurodyn.qlack2.be.rules.impl.dto;

public class AuditRuntimeSessionDTO {
	private String workingSetVersionId;
	private String sessionId;

	// -- Accessors

	public String getWorkingSetVersionId() {
		return workingSetVersionId;
	}

	public void setWorkingSetVersionId(String workingSetVersionId) {
		this.workingSetVersionId = workingSetVersionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
