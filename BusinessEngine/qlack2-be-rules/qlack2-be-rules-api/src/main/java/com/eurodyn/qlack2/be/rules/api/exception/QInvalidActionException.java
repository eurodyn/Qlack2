package com.eurodyn.qlack2.be.rules.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

public class QInvalidActionException extends QException {

	private static final long serialVersionUID = -3021674524806106707L;

	public QInvalidActionException(String message) {
		super(message);
	}

}
