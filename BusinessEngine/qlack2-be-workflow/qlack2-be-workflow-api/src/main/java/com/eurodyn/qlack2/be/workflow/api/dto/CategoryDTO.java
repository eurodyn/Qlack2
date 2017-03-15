package com.eurodyn.qlack2.be.workflow.api.dto;

import java.util.List;

import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;

public class CategoryDTO {

	private String id;
	private String name;
	private String description;
	private UserDTO createdBy;
	private long createdOn;
	private UserDTO lastModifiedBy;
	private long lastModifiedOn;
	private List<WorkflowDTO> workflows;

	// -- Constructors

	public CategoryDTO() {
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

	public List<WorkflowDTO> getWorkflows() {
		return workflows;
	}

	public void setWorkflows(List<WorkflowDTO> workflows) {
		this.workflows = workflows;
	}

}
