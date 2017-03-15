package com.eurodyn.qlack2.fuse.rules.api;

import java.util.List;
import java.util.Map;

public class StatelessExecutionResults {

	private Map<String, byte[]> globals;

	private List<byte[]> facts;

	// -- Accessors

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
