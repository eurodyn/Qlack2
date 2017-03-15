package com.eurodyn.qlack2.be.rules.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

public class QInvalidConfigParamException extends QException {

	private static final long serialVersionUID = -3021674524806106707L;

	public QInvalidConfigParamException(String message) {
		super(message);
	}

}
