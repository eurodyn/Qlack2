package com.eurodyn.qlack2.be.forms.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

public class QInvalidFormVersionStateException extends QException {

	private static final long serialVersionUID = 8943651855558740103L;

	public QInvalidFormVersionStateException(String message) {
		super(message);
	}
}
