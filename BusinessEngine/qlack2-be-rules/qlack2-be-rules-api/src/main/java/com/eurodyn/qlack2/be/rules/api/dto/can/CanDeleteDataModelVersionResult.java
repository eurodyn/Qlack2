package com.eurodyn.qlack2.be.rules.api.dto.can;

import java.util.List;

public class CanDeleteDataModelVersionResult {

	private boolean result;

	private boolean containedInWorkingSetVersions;

	private List<String> workingSetVersions;

	private boolean parentOfDataModelVersions;

	private boolean containedInDataModelVersions;

	private List<String> dataModelVersions;

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

	public boolean isParentOfDataModelVersions() {
		return parentOfDataModelVersions;
	}

	public void setParentOfDataModelVersions(boolean parentOfDataModelVersions) {
		this.parentOfDataModelVersions = parentOfDataModelVersions;
	}

	public boolean isContainedInDataModelVersions() {
		return containedInDataModelVersions;
	}

	public void setContainedInDataModelVersions(boolean containedInDataModelVersions) {
		this.containedInDataModelVersions = containedInDataModelVersions;
	}

	public List<String> getDataModelVersions() {
		return dataModelVersions;
	}

	public void setDataModelVersions(List<String> dataModelVersions) {
		this.dataModelVersions = dataModelVersions;
	}

}
