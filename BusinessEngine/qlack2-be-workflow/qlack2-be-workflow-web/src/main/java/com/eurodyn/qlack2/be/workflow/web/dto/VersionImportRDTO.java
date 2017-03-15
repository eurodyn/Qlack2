package com.eurodyn.qlack2.be.workflow.web.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VersionImportRDTO {

	@NotNull
	@NotEmpty
	private String contentVersion;

	public String getContentVersion() {
		return contentVersion;
	}

	public void setContentVersion(String content) {
		this.contentVersion = content;
	}

}
