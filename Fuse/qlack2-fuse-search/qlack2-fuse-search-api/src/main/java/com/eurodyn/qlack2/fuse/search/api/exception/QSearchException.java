package com.eurodyn.qlack2.fuse.search.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

/**
 * A generic wrapping-exception for this module.
 */
public class QSearchException extends QException {
	private static final long serialVersionUID = -2125009988734613110L;

	/**
	 * Default empty constructor.
	 */
	public QSearchException() {
		super();
	}

	/**
	 * A constructor with a specific message.
	 * 
	 * @param msg
	 *            The message to include for this exception.
	 */
	public QSearchException(String msg) {
		super(msg);
	}

	/**
	 * A constructor with a specific message and an underlying exception cause
	 * (root exception).
	 * 
	 * @param msg
	 *            The message to include for this exception.
	 * @param e
	 *            The root exception for this exception.
	 */
	public QSearchException(String msg, Throwable e) {
		super(msg, e);
	}
}
