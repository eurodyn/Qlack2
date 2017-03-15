package com.eurodyn.qlack2.be.forms.impl.dto;

import java.util.List;

public class AuditFormDTO {
	private String id;
	private String name;
	private String description;
	private boolean active;
	private List<AuditCategoryDTO> categories;
	private List<String> locales;
	private List<AuditFormVersionDTO> formVersions;

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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<AuditCategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<AuditCategoryDTO> categories) {
		this.categories = categories;
	}

	public List<String> getLocales() {
		return locales;
	}

	public void setLocales(List<String> locales) {
		this.locales = locales;
	}

	public List<AuditFormVersionDTO> getFormVersions() {
		return formVersions;
	}

	public void setFormVersions(List<AuditFormVersionDTO> formVersions) {
		this.formVersions = formVersions;
	}

}
