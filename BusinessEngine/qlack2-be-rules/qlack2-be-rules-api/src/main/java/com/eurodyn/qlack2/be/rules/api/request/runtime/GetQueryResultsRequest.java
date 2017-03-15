package com.eurodyn.qlack2.be.rules.api.request.runtime;

import java.util.List;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetQueryResultsRequest extends QSignedRequest {

	private String sessionId;

	private String query;

	private List<byte[]> arguments;

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

	public List<byte[]> getArguments() {
		return arguments;
	}

	public void setArguments(List<byte[]> arguments) {
		this.arguments = arguments;
	}

}
