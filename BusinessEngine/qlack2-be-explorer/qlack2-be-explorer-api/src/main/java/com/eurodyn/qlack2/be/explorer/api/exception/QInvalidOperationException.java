package com.eurodyn.qlack2.be.explorer.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

public class QInvalidOperationException extends QException {

	private static final long serialVersionUID = 111693150226098613L;

	public QInvalidOperationException(String message) {
		super(message);
	}

}
