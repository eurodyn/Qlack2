package com.eurodyn.qlack2.be.rules.web.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class RuleVersionImportRestDTO {

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
