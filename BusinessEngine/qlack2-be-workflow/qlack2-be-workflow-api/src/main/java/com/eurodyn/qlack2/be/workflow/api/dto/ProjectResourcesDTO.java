package com.eurodyn.qlack2.be.workflow.api.dto;

import java.util.ArrayList;
import java.util.List;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;

public class ProjectResourcesDTO {
	private String id;
	private String name;
	private Long createdOn;
	private UserDTO createdBy;
	private List<CategoryDTO> categories = new ArrayList<>();
	private List<WorkflowDTO> workflows = new ArrayList<>();

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

	public Long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Long createdOn) {
		this.createdOn = createdOn;
	}

	public UserDTO getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserDTO createdBy) {
		this.createdBy = createdBy;
	}

	public List<CategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryDTO> categories) {
		this.categories = categories;
	}

	public List<WorkflowDTO> getWorkflows() {
		return workflows;
	}

	public void setWorkflows(List<WorkflowDTO> workflows) {
		this.workflows = workflows;
	}

}
