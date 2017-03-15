package com.eurodyn.qlack2.be.forms.impl.util;


/**
 * The operations which can be assigned to a user through the access management
 * functionality of the Forms application.
 * @author European Dynamics SA
 *
 */
public enum SecureOperation {
	FRM_VIEW_FORM(false, true, true),
	FRM_MANAGE_FORM(false, true, true),
	FRM_LOCK_FORM(false, true, true),
	FRM_UNLOCK_ANY_FORM(false, true, true),
	FRM_VIEW_RENDERED_FORM(false, true, true),
	FRM_MANAGE_CATEGORY(false, true, false),
	FRM_CONFIGURE(true, false, false),
	FRM_MANAGED(false, false, false);

	// Whether this operation is generic, ie. it does not apply to
	// a specific resource
	private boolean generic;
	// Whether this operation is applied on a specific resource
	// (project or form respectively)
	private boolean onProject;
	private boolean onForm;

	private SecureOperation(boolean generic, boolean onProject, boolean onForm) {
		this.generic = generic;
		this.onProject = onProject;
		this.onForm = onForm;
	}

	public boolean isGeneric() {
		return generic;
	}

	public boolean isOnProject() {
		return onProject;
	}

	public boolean isOnForm() {
		return onForm;
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
