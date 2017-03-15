package com.eurodyn.qlack2.util.atmosphere.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

public class QUnsupportedTopic extends QException {
	private static final long serialVersionUID = 2633195317934255841L;
	public QUnsupportedTopic(String msg) {
		super(msg);
	}
}
