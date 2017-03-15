package com.eurodyn.qlack2.be.rules.api.dto.can;

public class CanDeleteLibraryResult {

	private boolean result;

	private boolean lockedByOtherUser;

	private boolean containedInWorkingSet;

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

	public boolean isContainedInWorkingSet() {
		return containedInWorkingSet;
	}

	public void setContainedInWorkingSet(boolean containedInWorkingSet) {
		this.containedInWorkingSet = containedInWorkingSet;
	}

}
