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
package com.eurodyn.qlack2.fuse.forum.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

/**
 * Exception class for forum module
 *
 * @author European Dynamics SA.
 */
public class QForumException extends QException {
	private static final long serialVersionUID = -8818577616358038848L;

	// public static enum CODES implements ExceptionCode {
	//
	// ERR_FORUM_0001, // A forum with this title already exists in the system
	// ERR_FORUM_0002, // Required property value missing
	// ERR_FORUM_0003, // No Such forum present in system
	// ERR_FORUM_0004, // No Such topic present in system
	// ERR_FORUM_0005, // The attempted operation is not supported
	// ERR_FORUM_0006, // A topic with this title already exists in the
	// specified forum
	// ERR_FORUM_0007, // An invalid moderated property has been defined
	// ERR_FORUM_0008, // No such message present in the system
	// ERR_FORUM_0009, // JMS Exception
	// ERR_FORUM_0010, // No such message exists
	// ERR_FORUM_0011, // More than one results found where only a single result
	// was expected.
	// ERR_FORUM_0012 // Parent topic is not approved.
	// }

	/**
	 * @param message
	 */
	public QForumException(String message) {
		super(message);
	}

}
