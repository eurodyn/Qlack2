package com.eurodyn.qlack2.be.rules.api.dto.can;

import java.util.List;

public class CanUpdateEnabledForTestingWorkingSetResult {
	private boolean result;

	// Cannot update enbaled-for-testing working set version, must also contain the reported data model versions
	private boolean incomplete;

	// Cannot update enbaled-for-testing working set version, the reported versions are not enabled-for-testing
	private boolean restrict;

	private List<String> ruleVersions;
	private List<String> dataModelVersions;
	private List<String> libraryVersions;

	// -- Accessors

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public boolean isIncomplete() {
		return incomplete;
	}

	public void setIncomplete(boolean incomplete) {
		this.incomplete = incomplete;
	}

	public boolean isRestrict() {
		return restrict;
	}

	public void setRestrict(boolean restrict) {
		this.restrict = restrict;
	}

	public List<String> getRuleVersions() {
		return ruleVersions;
	}

	public void setRuleVersions(List<String> ruleVersions) {
		this.ruleVersions = ruleVersions;
	}

	public List<String> getDataModelVersions() {
		return dataModelVersions;
	}

	public void setDataModelVersions(List<String> dataModelVersions) {
		this.dataModelVersions = dataModelVersions;
	}

	public List<String> getLibraryVersions() {
		return libraryVersions;
	}

	public void setLibraryVersions(List<String> libraryVersions) {
		this.libraryVersions = libraryVersions;
	}

}
