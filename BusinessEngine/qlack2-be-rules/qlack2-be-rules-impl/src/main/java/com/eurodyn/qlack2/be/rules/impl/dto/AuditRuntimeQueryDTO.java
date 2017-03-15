package com.eurodyn.qlack2.be.rules.impl.dto;

import java.util.Set;

public class AuditRuntimeQueryDTO {
	private String sessionId;
	private String query;
	private Set<String> identifiers;

	// -- Accessors

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Set<String> getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(Set<String> identifiers) {
		this.identifiers = identifiers;
	}

}
