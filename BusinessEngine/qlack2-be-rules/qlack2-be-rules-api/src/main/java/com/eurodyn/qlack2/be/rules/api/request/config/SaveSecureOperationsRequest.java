package com.eurodyn.qlack2.be.rules.api.request.config;

import java.util.List;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;
import com.eurodyn.qlack2.webdesktop.api.dto.SecureOperationAccessDTO;

public class SaveSecureOperationsRequest extends QSignedRequest {
	private String subjectId;
	private String resourceId;
	private List<SecureOperationAccessDTO> operations;

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public List<SecureOperationAccessDTO> getOperations() {
		return operations;
	}

	public void setOperations(List<SecureOperationAccessDTO> operations) {
		this.operations = operations;
	}
}
