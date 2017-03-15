package com.eurodyn.qlack2.be.forms.web.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FormDetailsRDTO {
	@NotNull
	@NotEmpty
	@Length(min = 1, max = 255)
	private String name;

	@Length(min = 0, max = 1024)
	private String description;

	@NotNull
	@NotEmpty
	private String projectId;

	private boolean active;

	private List<String> categories;

	private List<String> locales;

	private String versionId;

	@Length(min = 0, max = 1024)
	private String versionDescription;

	private String versionContent;

	@Valid
	private List<ConditionRDTO> versionConditions;

	@Valid
	private List<TranslationRDTO> versionTranslations;

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

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public List<String> getLocales() {
		return locales;
	}

	public void setLocales(List<String> locales) {
		this.locales = locales;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public String getVersionDescription() {
		return versionDescription;
	}

	public void setVersionDescription(String versionDescription) {
		this.versionDescription = versionDescription;
	}

	public String getVersionContent() {
		return versionContent;
	}

	public void setVersionContent(String versionContent) {
		this.versionContent = versionContent;
	}

	public List<ConditionRDTO> getVersionConditions() {
		return versionConditions;
	}

	public void setVersionConditions(List<ConditionRDTO> versionConditions) {
		this.versionConditions = versionConditions;
	}

	public List<TranslationRDTO> getVersionTranslations() {
		return versionTranslations;
	}

	public void setVersionTranslations(List<TranslationRDTO> versionTranslations) {
		this.versionTranslations = versionTranslations;
	}

}
