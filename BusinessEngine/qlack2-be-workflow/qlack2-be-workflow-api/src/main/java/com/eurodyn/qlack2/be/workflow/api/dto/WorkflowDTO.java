package com.eurodyn.qlack2.be.workflow.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowVersionDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;

public class WorkflowDTO {

	private String id;
	private String name;
	private String description;
	private String projectId;
	private boolean active;
	private UserDTO createdBy;
	private long createdOn;
	private UserDTO lastModifiedBy;
	private long lastModifiedOn;

	private List<String> categories = new ArrayList<>();

	private List<WorkflowVersionDTO> versions = new ArrayList<>();

	// -- Constructors

	public WorkflowDTO() {
	}

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
	
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public void setLastModifiedOn(long lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public UserDTO getLastModifiedBy() {
		return lastModifiedBy;
	}

	public long getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedBy(UserDTO lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public List<WorkflowVersionDTO> getVersions() {
		return versions;
	}

	public void setVersions(List<WorkflowVersionDTO> versions) {
		this.versions = versions;
	}

}
