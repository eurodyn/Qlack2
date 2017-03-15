package com.eurodyn.qlack2.webdesktop.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

/**
 * Thrown by the user management service when an illegal user action is
 * attempted. Illegal actions are not allowed by the business model and should
 * not be possible to perform through the UI. However our current implementation
 * may still allow for them to be performed through the UI, until this is fixed
 * we need to provide user-friendly feedback.
 *
 * @author European Dynamics SA
 */
public class IllegalUserActionException extends QException {
	private static final long serialVersionUID = 1L;

	private static final String SUPER_ADMIN_IN_GROUPS = "validation.super_admin_must_not_belong_to_any_groups";
	private static final String CANNOT_MANAGE_SUPER_ADMIN = "validation.domain_admin_cannot_manage_super_admin";
	private static final String CANNOT_MANAGE_FOREIGN_DOMAIN = "validation.domain_admin_cannot_manage_foreign_domain";

	public static IllegalUserActionException forSuperAdminInGroups() {
		return new IllegalUserActionException(SUPER_ADMIN_IN_GROUPS);
	}

	public static IllegalUserActionException forCannotManageSuperAdmin() {
		return new IllegalUserActionException(CANNOT_MANAGE_SUPER_ADMIN);
	}

	public static IllegalUserActionException forCannotManageForeignDomain() {
		return new IllegalUserActionException(CANNOT_MANAGE_FOREIGN_DOMAIN);
	}

	private IllegalUserActionException(String message) {
		super(message);
	}
}
