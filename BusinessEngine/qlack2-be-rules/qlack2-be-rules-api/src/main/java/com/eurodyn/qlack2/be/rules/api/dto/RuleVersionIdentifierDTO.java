package com.eurodyn.qlack2.be.rules.api.dto;

public class RuleVersionIdentifierDTO {

	private String ruleId;
	private String ruleName;

	private String id;
	private String name;

	private String workingSetVersionId;

	// -- Constructors

	public RuleVersionIdentifierDTO() {
	}

	// -- Accessors

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWorkingSetVersionId() {
		return workingSetVersionId;
	}

	public void setWorkingSetVersionId(String workingSetVersionId) {
		this.workingSetVersionId = workingSetVersionId;
	}

}
