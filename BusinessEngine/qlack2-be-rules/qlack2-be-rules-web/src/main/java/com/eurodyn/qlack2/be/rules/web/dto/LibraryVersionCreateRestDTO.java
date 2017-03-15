package com.eurodyn.qlack2.be.rules.web.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class LibraryVersionCreateRestDTO {

	@NotNull
	@NotEmpty
	@Length(min = 1, max = 255)
	private String name;

	@Length(min = 0, max = 1024)
	private String description;

	@NotNull
	@NotEmpty
	private String contentJAR; // the uniqueIdentifier of uploaded file

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

	public String getContentJAR() {
		return contentJAR;
	}

	public void setContentJAR(String contentJAR) {
		this.contentJAR = contentJAR;
	}

}
