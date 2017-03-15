package com.eurodyn.qlack2.be.explorer.api.request.project;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class UpdateProjectRequest extends QSignedRequest {
	private String id;
	private String name;
	private String description;
	private boolean active;
	private boolean rules;
	private boolean workflows;
	private boolean forms;

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
}
