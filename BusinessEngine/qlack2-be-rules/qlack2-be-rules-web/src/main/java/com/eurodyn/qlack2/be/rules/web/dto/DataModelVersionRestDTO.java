package com.eurodyn.qlack2.be.rules.web.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class DataModelVersionRestDTO {

	@NotNull
	@NotEmpty
	private String id;

	@Length(min = 0, max = 1024)
	private String description;

	@NotNull
	@NotEmpty
	@Length(min = 1, max = 255)
	@Pattern(regexp = "(?i)^([A-Z_][0-9A-Z_]*)(\\.[A-Z_][0-9A-Z_]*)*$", message = "qlack.validation.JavaPackage")
	private String modelPackage;

	private String parentModelVersionId;

	@Valid
	private List<DataModelFieldRestDTO> fields;

	// -- Accessors

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getModelPackage() {
		return modelPackage;
	}

	public void setModelPackage(String modelPackage) {
		this.modelPackage = modelPackage;
	}

	public String getParentModelVersionId() {
		return parentModelVersionId;
	}

	public void setParentModelVersionId(String parentModelVersionId) {
		this.parentModelVersionId = parentModelVersionId;
	}

	public List<DataModelFieldRestDTO> getFields() {
		return fields;
	}

	public void setFields(List<DataModelFieldRestDTO> fields) {
		this.fields = fields;
	}

}
