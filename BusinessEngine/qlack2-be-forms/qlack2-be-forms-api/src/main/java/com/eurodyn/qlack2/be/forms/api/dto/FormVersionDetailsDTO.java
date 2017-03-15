package com.eurodyn.qlack2.be.forms.api.dto;

import java.util.List;

import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;

public class FormVersionDetailsDTO {
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
	private List<ConditionDTO> conditions;
	private List<TranslationDTO> translations;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public List<TranslationDTO> getTranslations() {
		return translations;
	}

	public void setTranslations(List<TranslationDTO> translations) {
		this.translations = translations;
	}

}
