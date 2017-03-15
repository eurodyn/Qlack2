package com.eurodyn.qlack2.be.explorer.impl.util;


/**
 * The operations which can be assigned to a user through the access management
 * functionality of the Projects Explorer application.
 * @author European Dynamics SA
 *
 */
public enum SecureOperation {
	EXP_VIEW_PROJECT(false, true),
	EXP_MANAGE_PROJECT(true, true),
	EXP_CONFIGURE(true, false),
	EXP_MANAGED(false, false);
	
	// Whether this operation is generic, ie. it does not apply to 
	// a specific resource
	private boolean generic;
	// Whether this operation is applied on a specific resource
	private boolean onResource;
	
	private SecureOperation(boolean generic, boolean onResource) {
		this.generic = generic;
		this.onResource = onResource;
	}

	public boolean isGeneric() {
		return generic;
	}
	
	
	public boolean isOnResource() {
		return onResource;
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
