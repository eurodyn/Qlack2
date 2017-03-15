package com.eurodyn.qlack2.webdesktop.apps.usermanagement.impl.util;

import com.eurodyn.qlack2.webdesktop.api.util.Constants;

/**
 * The operations which can be assigned to a user through the access management
 * functionality of the User Manager application.
 * @author European Dynamics SA
 *
 */
public enum SecureOperation {
	WD_MANAGE_GROUPS,
	WD_MANAGE_USERS,
	USERMANAGEMENT_CONFIGURE,
	USERMANAGEMENT_MANAGED(false);
	
	private boolean uiManaged;
	
	private SecureOperation() {
		this.uiManaged = true;
	}
	
	private SecureOperation(boolean uiManaged) {
		this.uiManaged = uiManaged;
	}
	
	public boolean isUiManaged() {
		return uiManaged;
	}

	/**
	 * Override toString in order to ensure that operation names
	 * for WD operations will be retrieved by the Web Desktop
	 */
	@Override
	public String toString() {
		if (this.equals(SecureOperation.WD_MANAGE_GROUPS)) {
			return Constants.OP_MANAGE_GROUPS;
		} else if (this.equals(SecureOperation.WD_MANAGE_USERS)) {
			return Constants.OP_MANAGE_USERS;
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
