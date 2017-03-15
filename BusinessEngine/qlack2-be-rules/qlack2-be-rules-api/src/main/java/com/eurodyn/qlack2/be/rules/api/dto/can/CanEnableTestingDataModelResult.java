package com.eurodyn.qlack2.be.rules.api.dto.can;

import java.util.List;

public class CanEnableTestingDataModelResult {
	private boolean result;

	private boolean cascade;

	// Can enable testing and will cascade to versions
	private List<String> versions;

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public boolean isCascade() {
		return cascade;
	}

	public void setCascade(boolean cascade) {
		this.cascade = cascade;
	}

	public List<String> getVersions() {
		return versions;
	}

	public void setVersions(List<String> versions) {
		this.versions = versions;
	}

}
