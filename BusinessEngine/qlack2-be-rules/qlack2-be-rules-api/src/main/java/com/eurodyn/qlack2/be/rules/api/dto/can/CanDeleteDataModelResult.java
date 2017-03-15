package com.eurodyn.qlack2.be.rules.api.dto.can;

public class CanDeleteDataModelResult {

	private boolean result;

	private boolean lockedByOtherUser;

	private boolean containedInWorkingSet;

	private boolean parentOfDataModel;

	private boolean containedInDataModel;

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

	public boolean isParentOfDataModel() {
		return parentOfDataModel;
	}

	public void setParentOfDataModel(boolean parentOfDataModel) {
		this.parentOfDataModel = parentOfDataModel;
	}

	public boolean isContainedInDataModel() {
		return containedInDataModel;
	}

	public void setContainedInDataModel(boolean containedInDataModel) {
		this.containedInDataModel = containedInDataModel;
	}

}
