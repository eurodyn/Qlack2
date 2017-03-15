package com.eurodyn.qlack2.common.util.exception;

/**
 * A generic exception superclass to facilitate marking of any type of
 * security-related exceptions.
 *
 */
public abstract class QSecurityException extends QException {
	private static final long serialVersionUID = -8412287217789350614L;
	
	public QSecurityException() {
		super();
	}
	
	public QSecurityException(String msg) {
		super(msg);
	}
}
