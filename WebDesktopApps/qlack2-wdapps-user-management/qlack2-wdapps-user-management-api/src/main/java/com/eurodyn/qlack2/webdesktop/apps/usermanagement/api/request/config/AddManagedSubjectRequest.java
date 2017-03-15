package com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.config;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class AddManagedSubjectRequest extends QSignedRequest {
	private String subjectId;

	public AddManagedSubjectRequest(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
}
