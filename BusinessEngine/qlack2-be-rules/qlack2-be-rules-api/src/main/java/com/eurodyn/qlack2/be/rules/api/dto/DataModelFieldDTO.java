package com.eurodyn.qlack2.be.rules.api.dto;

public class DataModelFieldDTO {

	private String id;
	private String name;

	private String fieldTypeId;
	private String fieldTypeName;
	private String fieldTypeVersionId;
	private String fieldTypeVersionName;

	// -- Constructors

	public DataModelFieldDTO() {
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

	public String getFieldTypeId() {
		return fieldTypeId;
	}

	public void setFieldTypeId(String fieldTypeId) {
		this.fieldTypeId = fieldTypeId;
	}

	public String getFieldTypeName() {
		return fieldTypeName;
	}

	public void setFieldTypeName(String fieldTypeName) {
		this.fieldTypeName = fieldTypeName;
	}

	public String getFieldTypeVersionId() {
		return fieldTypeVersionId;
	}

	public void setFieldTypeVersionId(String fieldTypeVersionId) {
		this.fieldTypeVersionId = fieldTypeVersionId;
	}

	public String getFieldTypeVersionName() {
		return fieldTypeVersionName;
	}

	public void setFieldTypeVersionName(String fieldTypeVersionName) {
		this.fieldTypeVersionName = fieldTypeVersionName;
	}

}
