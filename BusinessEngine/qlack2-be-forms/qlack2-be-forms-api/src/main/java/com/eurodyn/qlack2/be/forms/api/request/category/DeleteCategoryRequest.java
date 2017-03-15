package com.eurodyn.qlack2.be.forms.api.request.category;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class DeleteCategoryRequest extends QSignedRequest {
	private String categoryId;

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

}
