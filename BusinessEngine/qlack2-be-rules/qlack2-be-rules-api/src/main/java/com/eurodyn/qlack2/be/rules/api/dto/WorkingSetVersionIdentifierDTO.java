package com.eurodyn.qlack2.be.rules.api.dto;

public class WorkingSetVersionIdentifierDTO {

	private String workingSetId;
	private String workingSetName;

	private String id;
	private String name;

	// -- Constructors

	public WorkingSetVersionIdentifierDTO() {
	}

	// -- Accessors

	public String getWorkingSetId() {
		return workingSetId;
	}

	public void setWorkingSetId(String workingSetId) {
		this.workingSetId = workingSetId;
	}

	public String getWorkingSetName() {
		return workingSetName;
	}

	public void setWorkingSetName(String workingSetName) {
		this.workingSetName = workingSetName;
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

}
