package com.eurodyn.qlack2.be.rules.impl.dto;

import java.util.ArrayList;
import java.util.List;

import com.eurodyn.qlack2.be.rules.api.dto.DataModelFieldDTO;

public class AuditDataModelVersionDTO {
	private String id;
	private String name;
	private String description;

	private String modelPackage;

	private String parentModelId;
	private String parentModelName;

	private String parentModelVersionId;
	private String parentModelVersionName;

	private List<DataModelFieldDTO> fields = new ArrayList<>();

	private int state;
	private boolean locked;

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

	public String getModelPackage() {
		return modelPackage;
	}

	public void setModelPackage(String modelPackage) {
		this.modelPackage = modelPackage;
	}

	public String getParentModelId() {
		return parentModelId;
	}

	public void setParentModelId(String parentModelId) {
		this.parentModelId = parentModelId;
	}

	public String getParentModelName() {
		return parentModelName;
	}

	public void setParentModelName(String parentModelName) {
		this.parentModelName = parentModelName;
	}

	public String getParentModelVersionId() {
		return parentModelVersionId;
	}

	public void setParentModelVersionId(String parentModelVersionId) {
		this.parentModelVersionId = parentModelVersionId;
	}

	public String getParentModelVersionName() {
		return parentModelVersionName;
	}

	public void setParentModelVersionName(String parentModelVersionName) {
		this.parentModelVersionName = parentModelVersionName;
	}

	public List<DataModelFieldDTO> getFields() {
		return fields;
	}

	public void setFields(List<DataModelFieldDTO> fields) {
		this.fields = fields;
	}

}
