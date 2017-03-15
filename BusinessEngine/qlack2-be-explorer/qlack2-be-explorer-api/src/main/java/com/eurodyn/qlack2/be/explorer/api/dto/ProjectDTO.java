package com.eurodyn.qlack2.be.explorer.api.dto;

import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;

public class ProjectDTO {
	private String id;
	private String name;
	private String description;
	private boolean active;
	private boolean rules;
	private boolean workflows;
	private boolean forms;
	private UserDTO createdBy;
	private long createdOn;
	private UserDTO lastModifiedBy;
	private long lastModifiedOn;

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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isRules() {
		return rules;
	}

	public void setRules(boolean rules) {
		this.rules = rules;
	}

	public boolean isWorkflows() {
		return workflows;
	}

	public void setWorkflows(boolean workflows) {
		this.workflows = workflows;
	}

	public boolean isForms() {
		return forms;
	}

	public void setForms(boolean forms) {
		this.forms = forms;
	}

	public UserDTO getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserDTO createdBy) {
		this.createdBy = createdBy;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public UserDTO getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(UserDTO lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public long getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(long lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}
}
