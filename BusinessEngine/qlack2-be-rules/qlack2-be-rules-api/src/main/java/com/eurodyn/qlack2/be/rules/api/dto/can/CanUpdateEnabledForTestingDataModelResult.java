package com.eurodyn.qlack2.be.rules.api.dto.can;

import java.util.List;

public class CanUpdateEnabledForTestingDataModelResult {
	private boolean result;

	// Cannot update enbaled-for-testing data model version, violates containment of reported working set versions
	private boolean restrict;

	private List<String> workingSetVersions;

	// -- Accessors

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public boolean isRestrict() {
		return restrict;
	}

	public void setRestrict(boolean restrict) {
		this.restrict = restrict;
	}

	public List<String> getWorkingSetVersions() {
		return workingSetVersions;
	}

	public void setWorkingSetVersions(List<String> workingSetVersions) {
		this.workingSetVersions = workingSetVersions;
	}

}
