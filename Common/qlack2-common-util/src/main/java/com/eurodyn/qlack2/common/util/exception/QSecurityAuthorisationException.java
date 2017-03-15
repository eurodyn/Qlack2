package com.eurodyn.qlack2.common.util.exception;

/**
 * A generic exception superclass to facilitate marking of
 * authorisation-related exceptions.
 *
 */
public class QSecurityAuthorisationException extends QSecurityException {
	private static final long serialVersionUID = 3887709297788547031L;

	public QSecurityAuthorisationException() {
		super();
	}

	public QSecurityAuthorisationException(String msg) {
		super(msg);
	}
}
