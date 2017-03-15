package com.eurodyn.qlack2.be.workflow.impl.dto;

import java.util.List;

public class AuditWorkflowDTO {
	private String id;
	private String name;
	private String description;
	private boolean active;
	private List<AuditCategoryDTO> categories;
	private List<AuditWorkflowVersionDTO> versions;

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

	public List<AuditCategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<AuditCategoryDTO> categories) {
		this.categories = categories;
	}

	public List<AuditWorkflowVersionDTO> getVersions() {
		return versions;
	}

	public void setVersions(List<AuditWorkflowVersionDTO> versions) {
		this.versions = versions;
	}

}
