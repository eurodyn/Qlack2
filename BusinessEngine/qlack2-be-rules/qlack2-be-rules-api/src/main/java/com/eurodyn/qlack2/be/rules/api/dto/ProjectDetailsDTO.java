package com.eurodyn.qlack2.be.rules.api.dto;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetailsDTO {

	private String id;
	private String name;

	private List<WorkingSetDTO> workingSets = new ArrayList<>();
	private List<RuleDTO> rules = new ArrayList<>();
	private List<DataModelDTO> dataModels = new ArrayList<>();
	private List<LibraryDTO> libraries = new ArrayList<>();

	private List<CategoryDTO> categories = new ArrayList<>();

	// -- Constructors

	public ProjectDetailsDTO() {
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

	public List<WorkingSetDTO> getWorkingSets() {
		return workingSets;
	}

	public void setWorkingSets(List<WorkingSetDTO> workingSets) {
		this.workingSets = workingSets;
	}

	public List<RuleDTO> getRules() {
		return rules;
	}

	public void setRules(List<RuleDTO> rules) {
		this.rules = rules;
	}

	public List<DataModelDTO> getDataModels() {
		return dataModels;
	}

	public void setDataModels(List<DataModelDTO> dataModels) {
		this.dataModels = dataModels;
	}

	public List<LibraryDTO> getLibraries() {
		return libraries;
	}

	public void setLibraries(List<LibraryDTO> libraries) {
		this.libraries = libraries;
	}

	public List<CategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryDTO> categories) {
		this.categories = categories;
	}

}
