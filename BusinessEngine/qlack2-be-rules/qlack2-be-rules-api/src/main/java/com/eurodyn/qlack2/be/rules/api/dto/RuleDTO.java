package com.eurodyn.qlack2.be.rules.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;

public class RuleDTO {

	private String projectId;

	private String id;
	private String name;
	private String description;
	private boolean active;

	private long createdOn;
	private UserDTO createdBy;
	private long lastModifiedOn;
	private UserDTO lastModifiedBy;

	private List<String> categoryIds = new ArrayList<>();

	private List<RuleVersionDTO> versions;

	// -- Constructors

	public RuleDTO() {
	}

	// -- Accessors

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
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

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public UserDTO getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserDTO createdBy) {
		this.createdBy = createdBy;
	}

	public long getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(long lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public UserDTO getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(UserDTO lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public List<String> getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(List<String> categoryIds) {
		this.categoryIds = categoryIds;
	}

	public List<RuleVersionDTO> getVersions() {
		return versions;
	}

	public void setVersions(List<RuleVersionDTO> versions) {
		this.versions = versions;
	}

}
