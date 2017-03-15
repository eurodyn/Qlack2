package com.eurodyn.qlack2.be.forms.api.request.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetFormVersionIdByNameRequest extends QSignedRequest {
	private String formVersionName;

	private String formId;

	public String getFormVersionName() {
		return formVersionName;
	}

	public void setFormVersionName(String formVersionName) {
		this.formVersionName = formVersionName;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}
}
