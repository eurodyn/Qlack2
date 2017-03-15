package com.eurodyn.qlack2.be.rules.impl.util;


/**
 * The operations which can be assigned to a user through the access management
 * functionality of the Forms application.
 * @author European Dynamics SA
 *
 */
public enum SecureOperation {
	RUL_MANAGE_CATEGORY(false, true, false, false, false, false),

	RUL_VIEW_WORKING_SET(false, true, false, false, false, true),
	RUL_MANAGE_WORKING_SET(false, true, false, false, false, true),
	RUL_LOCK_WORKING_SET(false, true, false, false, false, true),
	RUL_UNLOCK_ANY_WORKING_SET(false, true, false, false, false, true),

	RUL_VIEW_RULE(false, true, true, false, false, false),
	RUL_MANAGE_RULE(false, true, true, false, false, false),
	RUL_LOCK_RULE(false, true, true, false, false, false),
	RUL_UNLOCK_ANY_RULE(false, true, true, false, false, false),

	RUL_VIEW_DATA_MODEL(false, true, false, false, true, false),
	RUL_MANAGE_DATA_MODEL(false, true, false, false, true, false),
	RUL_LOCK_DATA_MODEL(false, true, false, false, true, false),
	RUL_UNLOCK_ANY_DATA_MODEL(false, true, false, false, true, false),

	RUL_VIEW_LIBRARY(false, true, false, true, false, false),
	RUL_MANAGE_LIBRARY(false, true, false, true, false, false),
	RUL_LOCK_LIBRARY(false, true, false, true, false, false),
	RUL_UNLOCK_ANY_LIBRARY(false, true, false, true, false, false),

	RUL_EXECUTE_WORKING_SET(false, true, false, false, false, true),
	RUL_CONFIGURE(true, false, false, false, false, false),
	RUL_MANAGED(false, false, false, false, false, false);

	// Whether this operation is generic, ie. it does not apply to
	// a specific resource
	private boolean generic;
	// Whether this operation is applied on a specific resource
	private boolean onProject;
	private boolean onRule;
	private boolean onLibrary;
	private boolean onDataModel;
	private boolean onWorkingSet;

	private SecureOperation(boolean generic, boolean onProject, boolean onRule,
			boolean onLibrary, boolean onDataModel, boolean onWorkingSet) {
		this.generic = generic;
		this.onProject = onProject;
		this.onRule = onRule;
		this.onLibrary = onLibrary;
		this.onDataModel = onDataModel;
		this.onWorkingSet = onWorkingSet;
	}

	public boolean isGeneric() {
		return generic;
	}

	public boolean isOnProject() {
		return onProject;
	}

	public boolean isOnRule() {
		return onRule;
	}

	public boolean isOnLibrary() {
		return onLibrary;
	}

	public boolean isOnDataModel() {
		return onDataModel;
	}

	public boolean isOnWorkingSet() {
		return onWorkingSet;
	}

	public static boolean contains(String value) {
	    for (SecureOperation operation : SecureOperation.values()) {
	        if (operation.toString().equals(value)) {
	            return true;
	        }
	    }
	    return false;
	}
}
