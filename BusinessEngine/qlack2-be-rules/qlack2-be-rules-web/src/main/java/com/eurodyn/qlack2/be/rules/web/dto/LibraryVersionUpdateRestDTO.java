package com.eurodyn.qlack2.be.rules.web.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class LibraryVersionUpdateRestDTO {

	@NotNull
	@NotEmpty
	private String id;

	@NotNull
	@NotEmpty
	private String contentJAR;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContentJAR() {
		return contentJAR;
	}

	public void setContentJAR(String contentJAR) {
		this.contentJAR = contentJAR;
	}

}
