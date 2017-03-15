package com.eurodyn.qlack2.webdesktop.apps.appmanagement.impl.dto;

import java.util.List;

import com.eurodyn.qlack2.webdesktop.api.dto.SecureOperationAccessDTO;

public class SecureOperationAuditDTO {
	private String subjectId;
	private String applicationId;
	private List<SecureOperationAccessDTO> operations;

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public List<SecureOperationAccessDTO> getOperations() {
		return operations;
	}

	public void setOperations(List<SecureOperationAccessDTO> operations) {
		this.operations = operations;
	}
}
