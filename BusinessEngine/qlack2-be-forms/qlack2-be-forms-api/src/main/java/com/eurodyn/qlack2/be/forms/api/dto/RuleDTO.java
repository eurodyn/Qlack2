package com.eurodyn.qlack2.be.forms.api.dto;

public class RuleDTO {
	/**
	 * Rule version id
	 */
	private String id;

	/**
	 * Rule version name
	 */
	private String name;

	/**
	 * Rule name
	 */
	private String ruleName;

	/**
	 * Working set version id
	 */
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
