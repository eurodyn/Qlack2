package com.eurodyn.qlack2.be.rules.api.request.runtime;

import java.util.List;
import java.util.Map;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class StatelessExecuteRequest extends QSignedRequest {

	private WorkingSetRuleVersionPair pair;

	private Map<String, byte[]> globals;

	private List<byte[]> facts;

	// -- Accessors

	public WorkingSetRuleVersionPair getPair() {
		return pair;
	}

	public void setPair(WorkingSetRuleVersionPair pair) {
		this.pair = pair;
	}

	public Map<String, byte[]> getGlobals() {
		return globals;
	}

	public void setGlobals(Map<String, byte[]> globals) {
		this.globals = globals;
	}

	public List<byte[]> getFacts() {
		return facts;
	}

	public void setFacts(List<byte[]> facts) {
		this.facts = facts;
	}

}
