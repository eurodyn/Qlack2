package com.eurodyn.qlack2.fuse.workflow.runtime.api;

import com.eurodyn.qlack2.common.util.exception.QException;

public class QWorkflowRuntimeException extends QException {

	private static final long serialVersionUID = -7892887428308971780L;

	public QWorkflowRuntimeException(String message) {
		super(message);
	}

	public QWorkflowRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
