package com.eurodyn.qlack2.fuse.rules.api;

import com.eurodyn.qlack2.common.util.exception.QException;

public class QRulesRuntimeException extends QException {

	private static final long serialVersionUID = 4852993089765648460L;

	public QRulesRuntimeException(String message) {
		super(message);
	}

	public QRulesRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public QRulesRuntimeException(Throwable cause) {
		super(cause);
	}

}
