package com.eurodyn.qlack2.be.rules.web.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class WorkingSetVersionRestDTO {

	@NotNull
	@NotEmpty
	private String id;

	@Length(min = 0, max = 1024)
	private String description;

	private List<String> ruleVersionIds = new ArrayList<>();

	private List<String> dataModelVersionIds = new ArrayList<>();

	private List<String> libraryVersionIds = new ArrayList<>();

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

	public List<String> getRuleVersionIds() {
		return ruleVersionIds;
	}

	public void setRuleVersionIds(List<String> ruleVersionIds) {
		this.ruleVersionIds = ruleVersionIds;
	}

	public List<String> getDataModelVersionIds() {
		return dataModelVersionIds;
	}

	public void setDataModelVersionIds(List<String> dataModelVersionIds) {
		this.dataModelVersionIds = dataModelVersionIds;
	}

	public List<String> getLibraryVersionIds() {
		return libraryVersionIds;
	}

	public void setLibraryVersionIds(List<String> libraryVersionIds) {
		this.libraryVersionIds = libraryVersionIds;
	}

}
