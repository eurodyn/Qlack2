package com.eurodyn.qlack2.be.rules.api.dto;

import java.util.List;

import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;

public class WorkingSetVersionDTO {

	private String workingSetId;

	private String id;
	private String name;
	private String description;

	private VersionState state;
	private long createdOn;
	private UserDTO createdBy;
	private long lastModifiedOn;
	private UserDTO lastModifiedBy;
	private Long lockedOn;
	private UserDTO lockedBy;

	private List<RuleVersionDTO> rules;
	private List<DataModelVersionDTO> dataModels;
	private List<LibraryVersionDTO> libraries;

	// -- Constructors

	public WorkingSetVersionDTO() {
	}

	// -- Accessors

	public String getWorkingSetId() {
		return workingSetId;
	}

	public void setWorkingSetId(String workingSetId) {
		this.workingSetId = workingSetId;
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

	public VersionState getState() {
		return state;
	}

	public void setState(VersionState state) {
		this.state = state;
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

	public Long getLockedOn() {
		return lockedOn;
	}

	public void setLockedOn(Long lockedOn) {
		this.lockedOn = lockedOn;
	}

	public UserDTO getLockedBy() {
		return lockedBy;
	}

	public void setLockedBy(UserDTO lockedBy) {
		this.lockedBy = lockedBy;
	}

	public List<RuleVersionDTO> getRules() {
		return rules;
	}

	public void setRules(List<RuleVersionDTO> rules) {
		this.rules = rules;
	}

	public List<DataModelVersionDTO> getDataModels() {
		return dataModels;
	}

	public void setDataModels(List<DataModelVersionDTO> dataModels) {
		this.dataModels = dataModels;
	}

	public List<LibraryVersionDTO> getLibraries() {
		return libraries;
	}

	public void setLibraries(List<LibraryVersionDTO> libraries) {
		this.libraries = libraries;
	}

}
