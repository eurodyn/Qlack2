package com.eurodyn.qlack2.fuse.cm.api.storage;

import com.eurodyn.qlack2.common.util.exception.QException;

public class QStorageException extends QException {
	private static final long serialVersionUID = 1L;

	public QStorageException(String message, Throwable cause) {
		super(message, cause);
	}
}
