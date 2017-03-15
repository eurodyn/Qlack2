/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
package com.eurodyn.qlack2.common.util.exception;

import java.io.Serializable;

/**
 * A generic superclass to indicate any kind of runtime exception. This needs to
 * remain a RuntimeException in order for Aries to correctly rollback
 * transactions.
 * 
 * @author European Dynamics SA
 */
public abstract class QException extends RuntimeException implements Serializable {
	private static final long serialVersionUID = 4808786528779863568L;

	protected QException() {
	}

	protected QException(String message) {
		super(message);
	}

	protected QException(String message, Throwable cause) {
		super(message, cause);
	}

	protected QException(Throwable cause) {
		super(cause);
	}

	protected QException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
