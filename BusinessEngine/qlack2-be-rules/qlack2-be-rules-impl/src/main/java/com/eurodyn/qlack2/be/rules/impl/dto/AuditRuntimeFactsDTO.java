package com.eurodyn.qlack2.be.rules.impl.dto;

import java.util.List;

public class AuditRuntimeFactsDTO {
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
