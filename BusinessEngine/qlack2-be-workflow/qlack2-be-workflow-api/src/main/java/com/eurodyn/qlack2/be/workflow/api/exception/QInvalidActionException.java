package com.eurodyn.qlack2.be.workflow.api.exception;

import java.text.MessageFormat;

import com.eurodyn.qlack2.common.util.exception.QException;

public class QInvalidActionException extends QException {
	
	private static final long serialVersionUID = -6166817621128992398L;

	public QInvalidActionException(String msg) {
		super(msg);
	}

	public QInvalidActionException(Throwable cause) {
		super(cause);
	}
}
