package com.eurodyn.qlack2.be.forms.api.request.category;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class UpdateCategoryRequest extends QSignedRequest {

	private String id;
	private String name;
	private String description;

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

}
