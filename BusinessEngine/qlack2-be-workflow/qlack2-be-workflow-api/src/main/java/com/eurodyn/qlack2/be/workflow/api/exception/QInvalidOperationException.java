package com.eurodyn.qlack2.be.workflow.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

public class QInvalidOperationException extends QException {

	private static final long serialVersionUID = 358488180820520850L;

	public QInvalidOperationException(String message) {
		super(message);
	}

}
