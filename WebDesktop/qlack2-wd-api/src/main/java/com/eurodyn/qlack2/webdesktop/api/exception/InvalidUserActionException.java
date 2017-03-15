package com.eurodyn.qlack2.webdesktop.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

/**
 * Thrown by the user management service when an invalid user action is
 * attempted. Invalid actions are not allowed by the business model but may
 * still be possible to perform through the UI, thus we need to provide
 * user-friendly feedback.
 *
 * @author European Dynamics SA
 */
public class InvalidUserActionException extends QException {
	private static final long serialVersionUID = 1L;

	private static final String IN_SINGLE_DOMAIN = "validation.user_must_belong_to_single_domain";
	private static final String IN_DOMAIN_ADMIN_DOMAIN = "validation.user_must_belong_to_domain_admin_domain";

	public static InvalidUserActionException forSingleDomain() {
		return new InvalidUserActionException(IN_SINGLE_DOMAIN);
	}

	public static InvalidUserActionException forDomainAdminDomain() {
		return new InvalidUserActionException(IN_DOMAIN_ADMIN_DOMAIN);
	}

	private InvalidUserActionException(String message) {
		super(message);
	}
}
