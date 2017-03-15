package com.eurodyn.qlack2.common.util.exception;

/**
 * A generic exception superclass to facilitate marking of
 * authentication-related exceptions.
 *
 */
public class QSecurityAuthenticationException extends QSecurityException {
	private static final long serialVersionUID = -7341692118839270522L;

	public QSecurityAuthenticationException() {
		super();
	}

	public QSecurityAuthenticationException(String msg) {
		super(msg);
	}
}
