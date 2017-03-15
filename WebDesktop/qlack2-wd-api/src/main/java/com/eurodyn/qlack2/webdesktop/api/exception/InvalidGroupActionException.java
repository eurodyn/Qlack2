package com.eurodyn.qlack2.webdesktop.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

/**
 * Thrown by the group management service when an invalid group action is
 * attempted. Invalid actions are not allowed by the business model but may
 * still be possible to perform through the UI, thus we need to provide
 * user-friendly feedback.
 *
 * @author European Dynamics SA
 */
public class InvalidGroupActionException extends QException {
	private static final long serialVersionUID = 1L;

	private InvalidGroupActionException(String message) {
		super(message);
	}
}
