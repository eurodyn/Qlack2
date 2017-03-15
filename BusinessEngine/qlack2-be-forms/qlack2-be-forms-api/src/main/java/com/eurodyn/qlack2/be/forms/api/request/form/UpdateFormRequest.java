package com.eurodyn.qlack2.be.forms.api.request.form;

import java.util.List;

import com.eurodyn.qlack2.be.forms.api.dto.ConditionDTO;
import com.eurodyn.qlack2.be.forms.api.dto.TranslationDTO;
import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class UpdateFormRequest extends QSignedRequest {

	private String id;
	private String name;
	private String description;
	private String projectId;
	private boolean active;
	private List<String> categories;
	private List<String> locales;
	private String versionId;
	private String versionDescription;
	private String versionContent;
	private List<ConditionDTO> versionConditions;
	private List<TranslationDTO> versionTranslations;

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

	public List<ConditionDTO> getVersionConditions() {
		return versionConditions;
	}

	public void setVersionConditions(List<ConditionDTO> versionConditions) {
		this.versionConditions = versionConditions;
	}

	public List<TranslationDTO> getVersionTranslations() {
		return versionTranslations;
	}

	public void setVersionTranslations(List<TranslationDTO> versionTranslations) {
		this.versionTranslations = versionTranslations;
	}

}
