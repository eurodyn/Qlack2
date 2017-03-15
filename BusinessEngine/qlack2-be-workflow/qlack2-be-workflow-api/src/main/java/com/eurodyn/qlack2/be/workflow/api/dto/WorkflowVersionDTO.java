package com.eurodyn.qlack2.be.workflow.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.eurodyn.qlack2.be.workflow.api.dto.ConditionDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;

public class WorkflowVersionDTO {

	private String id;
	private String name;
	private String description;
	private int state;
	private String content;
	private UserDTO createdBy;
	private long createdOn;
	private UserDTO lastModifiedBy;
	private long lastModifiedOn;
	private UserDTO lockedBy;
	private Long lockedOn;
	private boolean enableTesting;
	private String processId;
	
	private List<ConditionDTO> conditions = new ArrayList<>();

	public WorkflowVersionDTO() {
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
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
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
	
	public UserDTO getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(UserDTO lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public long getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(long lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}
	
	public UserDTO getLockedBy() {
		return lockedBy;
	}
	
	public void setLockedBy(UserDTO lockedBy) {
		this.lockedBy = lockedBy;
	}

	public Long getLockedOn() {
		return lockedOn;
	}

	public void setLockedOn(Long lockedOn) {
		this.lockedOn = lockedOn;
	}
	
	public List<ConditionDTO> getConditions() {
		return conditions;
	}

	public void setConditions(List<ConditionDTO> conditions) {
		this.conditions = conditions;
	}
	
	public boolean isEnableTesting() {
        return enableTesting;
	}
	
	public void setEnableTesting(boolean enableTesting) {
	        this.enableTesting = enableTesting;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}
}
