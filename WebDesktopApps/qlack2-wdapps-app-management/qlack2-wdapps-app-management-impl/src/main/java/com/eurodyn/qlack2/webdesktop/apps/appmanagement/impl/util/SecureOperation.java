package com.eurodyn.qlack2.webdesktop.apps.appmanagement.impl.util;

import com.eurodyn.qlack2.webdesktop.api.util.Constants;

/**
 * The operations which can be assigned to a user through the access management
 * functionality of the Application Manager application.
 * @author European Dynamics SA
 *
 */
public enum SecureOperation {
	WD_ACCESS_APPLICATION(true, false),
	WD_UPDATE_APPLICATION(true, true),
	APPMANAGEMENT_CONFIGURE(true, true),
	APPMANAGEMENT_MANAGED(false);
	
	// Whether this operation is managed through the UI and therefore
	// should be displayed in the Access Management page allowing the
	// admin to change user / group permissions o
	private boolean uiManaged;
	// Whether this operation is generic, ie. it does not apply to 
	// a specific resource
	private boolean generic;
	
	private SecureOperation(boolean uiManaged) {
		this.uiManaged = uiManaged;
	}
	
	private SecureOperation(boolean uiManaged, boolean generic) {
		this.uiManaged = uiManaged;
		this.generic = generic;
	}
	
	public boolean isUiManaged() {
		return uiManaged;
	}

	public boolean isGeneric() {
		return generic;
	}

	/**
	 * Override toString in order to ensure that operation names
	 * for WD operations will be retrieved by the Web Desktop
	 */
	@Override
	public String toString() {
		if (this.equals(SecureOperation.WD_ACCESS_APPLICATION)) {
			return Constants.OP_ACCESS_APPLICATION;
		} else if (this.equals(SecureOperation.WD_UPDATE_APPLICATION)) {
			return Constants.OP_UPDATE_APPLICATION;
		} else {
			return super.toString();
		}
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
