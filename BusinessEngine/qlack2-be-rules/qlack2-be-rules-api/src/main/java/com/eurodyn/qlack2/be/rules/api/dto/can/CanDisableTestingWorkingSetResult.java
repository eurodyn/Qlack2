package com.eurodyn.qlack2.be.rules.api.dto.can;

public class CanDisableTestingWorkingSetResult {

	private boolean result;

	private boolean usedByOtherComponents;

	// -- Accessors

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public boolean isUsedByOtherComponents() {
		return usedByOtherComponents;
	}

	public void setUsedByOtherComponents(boolean usedByOtherComponents) {
		this.usedByOtherComponents = usedByOtherComponents;
	}

}
