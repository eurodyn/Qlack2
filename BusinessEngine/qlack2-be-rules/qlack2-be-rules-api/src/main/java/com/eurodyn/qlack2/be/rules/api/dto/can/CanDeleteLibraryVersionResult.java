package com.eurodyn.qlack2.be.rules.api.dto.can;

import java.util.List;

public class CanDeleteLibraryVersionResult {

	private boolean result;

	private boolean containedInWorkingSetVersions;

	private List<String> workingSetVersions;

	// -- Accessors

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public boolean isContainedInWorkingSetVersions() {
		return containedInWorkingSetVersions;
	}

	public void setContainedInWorkingSetVersions(boolean containedInWorkingSetVersions) {
		this.containedInWorkingSetVersions = containedInWorkingSetVersions;
	}

	public List<String> getWorkingSetVersions() {
		return workingSetVersions;
	}

	public void setWorkingSetVersions(List<String> workingSetVersions) {
		this.workingSetVersions = workingSetVersions;
	}

}
