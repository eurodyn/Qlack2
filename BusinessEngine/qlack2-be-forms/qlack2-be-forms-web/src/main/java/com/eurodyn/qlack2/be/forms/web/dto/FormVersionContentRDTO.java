package com.eurodyn.qlack2.be.forms.web.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;


public class FormVersionContentRDTO {

	@NotNull
	@NotEmpty
	private String file;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}
