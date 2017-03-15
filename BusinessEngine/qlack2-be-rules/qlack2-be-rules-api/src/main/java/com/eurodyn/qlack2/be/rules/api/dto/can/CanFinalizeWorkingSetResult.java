package com.eurodyn.qlack2.be.rules.api.dto.can;

import java.util.List;

public class CanFinalizeWorkingSetResult {
	private boolean result;

	// Cannot finalize, must also contain the reported data model versions
	private boolean incomplete;

	// Can finalize and will cascade to the reported versions
	private boolean cascade;

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

	public boolean isCascade() {
		return cascade;
	}

	public void setCascade(boolean cascade) {
		this.cascade = cascade;
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
