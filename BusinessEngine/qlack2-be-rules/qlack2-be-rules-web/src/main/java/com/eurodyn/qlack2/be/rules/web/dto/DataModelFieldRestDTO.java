package com.eurodyn.qlack2.be.rules.web.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class DataModelFieldRestDTO {

	private String id;

	@NotNull
	@NotEmpty
	@Length(min = 1, max = 255)
	@Pattern(regexp = "(?i)^[A-Z_][0-9A-Z_]*$", message = "qlack.validation.JavaIdentifier")
	private String name;

	@NotNull
	@NotEmpty
	private String fieldTypeId;

	private String fieldTypeVersionId;

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

	public String getFieldTypeVersionId() {
		return fieldTypeVersionId;
	}

	public void setFieldTypeVersionId(String fieldTypeVersionId) {
		this.fieldTypeVersionId = fieldTypeVersionId;
	}

}
