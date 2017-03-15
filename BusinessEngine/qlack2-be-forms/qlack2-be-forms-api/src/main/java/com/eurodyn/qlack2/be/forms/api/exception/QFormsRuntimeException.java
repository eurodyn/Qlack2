package com.eurodyn.qlack2.be.forms.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

public class QFormsRuntimeException extends QException {

	private static final long serialVersionUID = 4816907240818870385L;

	public QFormsRuntimeException(String message) {
		super(message);
	}

	public QFormsRuntimeException(Throwable cause) {
		super(cause);
	}

}
