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
package com.eurodyn.qlack2.fuse.clipboard.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

/**
 * @author European Dynamics SA
 */
public class QClipboardException extends QException {
	private static final long serialVersionUID = 1694330928522900649L;

	// public static enum CODES implements ExceptionCode {
	// ERR_CLB_0001, //Clipboard entry not found
	// ERR_CLB_0002; //Clipboard entry metadatum not found
	// }

	public QClipboardException(String message) {
		super(message);
	}

}
