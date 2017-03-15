package com.eurodyn.qlack2.be.forms.api.request.orbeon;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class RetrieveFormRequest extends QSignedRequest {

	private String formVersionRequestId;

	public String getFormVersionRequestId() {
		return formVersionRequestId;
	}

	public void setFormVersionRequestId(String formVersionRequestId) {
		this.formVersionRequestId = formVersionRequestId;
	}

}
