package com.eurodyn.qlack2.be.rules.impl.dto;

import java.util.List;

public class AuditWorkingSetVersionDTO {
	private String id;
	private String name;
	private String description;

	private int state;
	private boolean locked;

	private List<String> ruleVersionIds;
	private List<String> dataModelVersionIds;
	private List<String> libraryVersionIds;

	// -- Accessors

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public List<String> getRuleVersionIds() {
		return ruleVersionIds;
	}

	public void setRuleVersionIds(List<String> ruleVersionIds) {
		this.ruleVersionIds = ruleVersionIds;
	}

	public List<String> getDataModelVersionIds() {
		return dataModelVersionIds;
	}

	public void setDataModelVersionIds(List<String> dataModelVersionIds) {
		this.dataModelVersionIds = dataModelVersionIds;
	}

	public List<String> getLibraryVersionIds() {
		return libraryVersionIds;
	}

	public void setLibraryVersionIds(List<String> libraryVersionIds) {
		this.libraryVersionIds = libraryVersionIds;
	}

}
