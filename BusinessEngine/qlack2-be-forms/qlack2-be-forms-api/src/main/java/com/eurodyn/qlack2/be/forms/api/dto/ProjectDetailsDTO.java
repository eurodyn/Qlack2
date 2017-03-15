package com.eurodyn.qlack2.be.forms.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;

public class ProjectDetailsDTO {
	private String id;
	private String name;
	private List<CategoryDTO> categories = new ArrayList<>();
	private List<FormDTO> forms = new ArrayList<>();

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

	public List<CategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryDTO> categories) {
		this.categories = categories;
	}

	public List<FormDTO> getForms() {
		return forms;
	}

	public void setForms(List<FormDTO> forms) {
		this.forms = forms;
	}

}
