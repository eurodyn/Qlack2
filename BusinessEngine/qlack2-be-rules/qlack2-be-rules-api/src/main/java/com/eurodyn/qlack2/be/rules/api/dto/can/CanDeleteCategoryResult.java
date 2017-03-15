package com.eurodyn.qlack2.be.rules.api.dto.can;

public class CanDeleteCategoryResult {

	private boolean result;

	private boolean assignedToResources;

	// -- Accessors

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public boolean isAssignedToResources() {
		return assignedToResources;
	}

	public void setAssignedToResources(boolean assignedToResources) {
		this.assignedToResources = assignedToResources;
	}

}
