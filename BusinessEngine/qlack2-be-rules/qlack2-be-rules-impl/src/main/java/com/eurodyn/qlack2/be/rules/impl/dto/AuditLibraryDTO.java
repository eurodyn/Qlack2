package com.eurodyn.qlack2.be.rules.impl.dto;

import java.util.List;

public class AuditLibraryDTO {
	private String id;
	private String name;
	private String description;
	private boolean active;

	private List<String> categoryIds;

	private List<AuditLibraryVersionDTO> versions;

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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<String> getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(List<String> categoryIds) {
		this.categoryIds = categoryIds;
	}

	public List<AuditLibraryVersionDTO> getVersions() {
		return versions;
	}

	public void setVersions(List<AuditLibraryVersionDTO> versions) {
		this.versions = versions;
	}

}
