package com.eurodyn.qlack2.be.explorer.impl.dto;

import java.util.List;

import com.eurodyn.qlack2.webdesktop.api.dto.SecureOperationAccessDTO;

public class SecureOperationAuditDTO {
	private String subjectId;
	private String projectId;
	private List<SecureOperationAccessDTO> operations;

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public List<SecureOperationAccessDTO> getOperations() {
		return operations;
	}

	public void setOperations(List<SecureOperationAccessDTO> operations) {
		this.operations = operations;
	}
}
