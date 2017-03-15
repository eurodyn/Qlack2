package com.eurodyn.qlack2.be.workflow.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.eurodyn.qlack2.be.workflow.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;

public class ProjectDTO {

	private String id;
	private String title;
	private String description;
	private Long createdOn;
	private UserDTO createdBy;

	private List<WorkflowDTO> workflows = new ArrayList<>();
	private List<CategoryDTO> categories = new ArrayList<>();

	// -- Constructors

	public ProjectDTO() {
	}

	// -- Accessors

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
	
	public Long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Long createdOn) {
		this.createdOn = createdOn;
	}

	public List<WorkflowDTO> getWorkflows() {
		return workflows;
	}

	public void setWorkflows(List<WorkflowDTO> workflows) {
		this.workflows = workflows;
	}
	
	public List<CategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryDTO> categories) {
		this.categories = categories;
	}

}
