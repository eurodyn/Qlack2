package com.eurodyn.qlack2.be.workflow.impl.util;


/**
 * The operations which can be assigned to a user through the access management
 * functionality of the Forms application.
 * @author European Dynamics SA
 *
 */
public enum SecureOperation {
	WFL_VIEW_WORKFLOW(false, true, true),
	WFL_MANAGE_WORKFLOW(false, true, true),
	WFL_LOCK_WORKFLOW(false, true, true),
	WFL_UNLOCK_ANY_WORKFLOW(false, true, true),
	WFL_MANAGE_CATEGORY(false, true, false),
	WFL_VIEW_RUNTIME(false, true, true),
	WFL_EXECUTE_RUNTIME(false, true, true),
	WFL_CONFIGURE(true, false, false),
	WFL_MANAGED(false, false, false);

	// Whether this operation is generic, ie. it does not apply to
	// a specific resource
	private boolean generic;
	// Whether this operation is applied on a specific resource
	// (project or form respectively)
	private boolean onProject;
	private boolean onWorkflow;

	private SecureOperation(boolean generic, boolean onProject, boolean onWorkflow) {
		this.generic = generic;
		this.onProject = onProject;
		this.onWorkflow = onWorkflow;
	}

	public boolean isGeneric() {
		return generic;
	}

	public boolean isOnProject() {
		return onProject;
	}

	public boolean isOnWorkflow() {
		return onWorkflow;
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
