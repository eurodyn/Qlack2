package com.eurodyn.qlack2.be.rules.web.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class RuleVersionCreateRestDTO {

	@NotNull
	@NotEmpty
	@Length(min = 1, max = 255)
	private String name;

	@Length(min = 0, max = 1024)
	private String description;

	private String basedOnId;

	// -- Accessors

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

	public String getBasedOnId() {
		return basedOnId;
	}

	public void setBasedOnId(String basedOnId) {
		this.basedOnId = basedOnId;
	}

}
