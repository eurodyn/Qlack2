package com.eurodyn.qlack2.be.forms.api.request.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetFormVersionTranslationsRequest extends QSignedRequest {
	private String formVersionId;

	public String getFormVersionId() {
		return formVersionId;
	}

	public void setFormVersionId(String formVersionId) {
		this.formVersionId = formVersionId;
	}

}
