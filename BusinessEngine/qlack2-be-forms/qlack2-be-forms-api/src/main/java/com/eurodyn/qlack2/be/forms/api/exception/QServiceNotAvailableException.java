package com.eurodyn.qlack2.be.forms.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

public class QServiceNotAvailableException extends QException {

	private static final long serialVersionUID = -1747576660119817604L;

	public QServiceNotAvailableException(String message) {
		super(message);
	}

}
