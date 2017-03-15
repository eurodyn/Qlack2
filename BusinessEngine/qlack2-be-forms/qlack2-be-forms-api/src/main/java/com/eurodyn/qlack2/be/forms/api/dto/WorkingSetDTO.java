package com.eurodyn.qlack2.be.forms.api.dto;

public class WorkingSetDTO {
	/**
	 * Working set version id.
	 */
	private String id;

	/**
	 * Working set version name
	 */
	private String name;

	/**
	 * Working set name
	 */
	private String workingSetName;

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

	public String getWorkingSetName() {
		return workingSetName;
	}

	public void setWorkingSetName(String workingSetName) {
		this.workingSetName = workingSetName;
	}

}
