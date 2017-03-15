package com.eurodyn.qlack2.webdesktop.api.dto;

public class SecureOperationAccessDTO {
	private String operation;
	private Boolean access;
	
	public SecureOperationAccessDTO(){}

	public SecureOperationAccessDTO(String operation, Boolean access) {
		this.operation = operation;
		this.access = access;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Boolean getAccess() {
		return access;
	}

	public void setAccess(Boolean access) {
		this.access = access;
	}
}
