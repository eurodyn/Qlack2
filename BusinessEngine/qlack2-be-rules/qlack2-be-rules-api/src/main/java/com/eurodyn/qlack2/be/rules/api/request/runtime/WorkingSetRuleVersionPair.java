package com.eurodyn.qlack2.be.rules.api.request.runtime;

public class WorkingSetRuleVersionPair {

	private String workingSetVersionId;

	private String ruleVersionId;

	// -- Constuctors

	public WorkingSetRuleVersionPair() {
	}

	public WorkingSetRuleVersionPair(String workingSetVersionId, String ruleVersionId) {
		this.workingSetVersionId = workingSetVersionId;
		this.ruleVersionId = ruleVersionId;
	}

	// -- Accessors

	public String getWorkingSetVersionId() {
		return workingSetVersionId;
	}

	public void setWorkingSetVersionId(String workingSetVersionId) {
		this.workingSetVersionId = workingSetVersionId;
	}

	public String getRuleVersionId() {
		return ruleVersionId;
	}

	public void setRuleVersionId(String ruleVersionId) {
		this.ruleVersionId = ruleVersionId;
	}

}
