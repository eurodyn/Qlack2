package com.eurodyn.qlack2.be.forms.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

public class QImportExportException extends QException {

	private static final long serialVersionUID = -1123886758187378188L;

	public QImportExportException(String message) {
		super(message);
	}

	public QImportExportException(String message, Throwable cause) {
		super(message, cause);
	}

}
