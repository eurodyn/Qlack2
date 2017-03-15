package com.eurodyn.qlack2.be.rules.api.request.runtime;

import java.util.List;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class FireRulesRequest extends QSignedRequest {

	private String sessionId;

	private List<String> ruleVersionIds;

	// -- Accessors

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public List<String> getRuleVersionIds() {
		return ruleVersionIds;
	}

	public void setRuleVersionIds(List<String> ruleVersionIds) {
		this.ruleVersionIds = ruleVersionIds;
	}

}
