package com.eurodyn.qlack2.be.forms.api.request.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetFormVersionsRequest extends QSignedRequest {
	private String formId;

	public GetFormVersionsRequest() {
		super();
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

}
