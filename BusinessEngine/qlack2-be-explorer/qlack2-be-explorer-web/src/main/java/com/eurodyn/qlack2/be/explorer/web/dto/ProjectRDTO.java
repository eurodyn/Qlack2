package com.eurodyn.qlack2.be.explorer.web.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectRDTO {
	@NotEmpty
	@Length(max = 255)
	private String name;
	@Length(max = 1024)
	private String description;

	private boolean active;
	private boolean rules;
	private boolean workflows;
	private boolean forms;

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
