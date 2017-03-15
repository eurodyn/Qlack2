package com.eurodyn.qlack2.be.forms.api.request.form;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetFormIdByNameRequest extends QSignedRequest {
	private String formName;

	private String projectId;

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
}
