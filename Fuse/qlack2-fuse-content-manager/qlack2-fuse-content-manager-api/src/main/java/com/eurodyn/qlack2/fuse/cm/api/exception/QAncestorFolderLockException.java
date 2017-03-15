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
package com.eurodyn.qlack2.fuse.cm.api.exception;

public class QAncestorFolderLockException extends QNodeLockException {
	
	private static final long serialVersionUID = 2789638483007504036L;

	/**
	 * Constructor method.
	 * 
	 * @param message
	 *            The message of the exception.
	 */
	public QAncestorFolderLockException(String message) {
		super(message);
	}

	/**
	 * Constructor method.
	 * 
	 * @param message
	 *            The message of the exception.
	 * @param conflictNodeID
	 *            The ID of the node which appears to have a lock conflict.
	 * @param conflictNodeName
	 *            The name of the node which appears to have a lock conflict.
	 */
	public QAncestorFolderLockException(String message, String conflictNodeID,
			String conflictNodeName) {
		super(message, conflictNodeID, conflictNodeName);
	}

	

}
