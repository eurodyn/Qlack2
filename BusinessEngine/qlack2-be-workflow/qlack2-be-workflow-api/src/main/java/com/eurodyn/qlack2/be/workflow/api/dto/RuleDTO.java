package com.eurodyn.qlack2.be.workflow.api.dto;

public class RuleDTO {
	
	private String id;

	private String name;

	private String ruleName;

	private String workingSetId;

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

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getWorkingSetId() {
		return workingSetId;
	}

	public void setWorkingSetId(String workingSetId) {
		this.workingSetId = workingSetId;
	}

}
