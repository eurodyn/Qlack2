package com.eurodyn.qlack2.be.workflow.impl.dto;

import java.util.List;

import com.eurodyn.qlack2.be.workflow.api.dto.ConditionDTO;

public class AuditWorkflowVersionDTO {
	private String id;
	private String name;
	private String description;
	private int state;
	private boolean locked;
	private List<ConditionDTO> conditions;

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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public List<ConditionDTO> getConditions() {
		return conditions;
	}

	public void setConditions(List<ConditionDTO> conditions) {
		this.conditions = conditions;
	}

}
