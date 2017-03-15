package com.eurodyn.qlack2.be.rules.api.request.runtime;

import java.util.List;
import java.util.Map;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class StatelessMultiExecuteRequest extends QSignedRequest {

	private List<WorkingSetRuleVersionPair> pairs;

	private Map<String, byte[]> globals;

	private List<byte[]> facts;

	// -- Accessors

	public List<WorkingSetRuleVersionPair> getPairs() {
		return pairs;
	}

	public void setPairs(List<WorkingSetRuleVersionPair> pairs) {
		this.pairs = pairs;
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
