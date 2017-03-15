package com.eurodyn.qlack2.be.rules.api.dto.can;

import java.util.List;

public class CanGetWorkingSetVersionModelsJarResult {

	private boolean result;

	private boolean permissionDenied;

	private List<String> dataModelVersions;

	// -- Accessors

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public boolean isPermissionDenied() {
		return permissionDenied;
	}

	public void setPermissionDenied(boolean permissionDenied) {
		this.permissionDenied = permissionDenied;
	}

	public List<String> getDataModelVersions() {
		return dataModelVersions;
	}

	public void setDataModelVersions(List<String> dataModelVersions) {
		this.dataModelVersions = dataModelVersions;
	}

}
