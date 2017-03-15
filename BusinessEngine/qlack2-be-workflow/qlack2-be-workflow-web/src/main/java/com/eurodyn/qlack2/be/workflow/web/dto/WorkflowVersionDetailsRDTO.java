package com.eurodyn.qlack2.be.workflow.web.dto;

import java.util.List;

import javax.validation.Valid;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowVersionDetailsRDTO {
	private String id;

	private String name;

	@Length(min = 0, max = 1024)
	private String description;

	private String content;

	//@AssertValid(appliesTo = { ConstraintTarget.VALUES })
	@Valid
	private List<ConditionRDTO> versionConditions;

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

	public List<ConditionRDTO> getVersionConditions() {
		return versionConditions;
	}

	public void setVersionConditions(List<ConditionRDTO> versionConditions) {
		this.versionConditions = versionConditions;
	}

}
