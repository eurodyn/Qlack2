package com.eurodyn.qlack2.webdesktop.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

/**
 * Thrown by the group management service when an illegal group action is
 * attempted. Illegal actions are not allowed by the business model and should
 * not be possible to perform through the UI. However our current implementation
 * may still allow for them to be performed through the UI, until this is fixed
 * we need to provide user-friendly feedback.
 *
 * @author European Dynamics SA
 */
public class IllegalGroupActionException extends QException {
	private static final long serialVersionUID = 1L;

	private static final String CANNOT_MANAGE_DOMAIN = "validation.domain_admin_cannot_manage_domain";
	private static final String CANNOT_MANAGE_FOREIGN_GROUP = "validation.domain_admin_cannot_manage_foreign_group";
	private static final String CANNOT_MANAGE_FOREIGN_USERS = "validation.domain_admin_cannot_manage_foreign_users";

	public static IllegalGroupActionException forCannotManageDomain() {
		return new IllegalGroupActionException(CANNOT_MANAGE_DOMAIN);
	}

	public static IllegalGroupActionException forCannotManageForeignGroup() {
		return new IllegalGroupActionException(CANNOT_MANAGE_FOREIGN_GROUP);
	}

	public static IllegalGroupActionException forCannotManageForeignUsers() {
		return new IllegalGroupActionException(CANNOT_MANAGE_FOREIGN_USERS);
	}

	private IllegalGroupActionException(String message) {
		super(message);
	}
}
