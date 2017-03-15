package com.eurodyn.qlack2.be.rules.api.dto.can;

public class CanDeleteWorkingSetResult {

	private boolean result;

	private boolean lockedByOtherUser;

	private boolean usedByOtherComponents;

	// -- Accessors

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public boolean isLockedByOtherUser() {
		return lockedByOtherUser;
	}

	public void setLockedByOtherUser(boolean lockedByOtherUser) {
		this.lockedByOtherUser = lockedByOtherUser;
	}

	public boolean isUsedByOtherComponents() {
		return usedByOtherComponents;
	}

	public void setUsedByOtherComponents(boolean usedByOtherComponents) {
		this.usedByOtherComponents = usedByOtherComponents;
	}

}
