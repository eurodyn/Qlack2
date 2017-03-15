package com.eurodyn.qlack2.be.rules.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

public class QRuntimeManagementException extends QException {

	private static final long serialVersionUID = 5857255556938230719L;

	public QRuntimeManagementException(String message) {
		super(message);
	}

	public QRuntimeManagementException(String message, Throwable cause) {
		super(message, cause);
	}

	public QRuntimeManagementException(Throwable cause) {
		super(cause);
	}

}
