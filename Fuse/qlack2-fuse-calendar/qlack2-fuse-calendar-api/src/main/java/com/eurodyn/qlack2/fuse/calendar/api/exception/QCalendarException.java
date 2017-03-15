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
package com.eurodyn.qlack2.fuse.calendar.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

/**
 *
 * @author European Dynamics SA
 */
public class QCalendarException extends QException {
	private static final long serialVersionUID = 3182917362640042337L;

	// public static enum CODES implements ExceptionCode {
	// ERR_CALENDAR_0001, //A calendar with the specified id does not exist
	// ERR_CALENDAR_0002, //A calendar item with the specified id does not exist
	// ERR_CALENDAR_0003, //A calendar participant with the specified id does
	// not exist
	// ERR_CALENDAR_0004, //A calendar supporting object with the specified id
	// does not exist
	// ERR_CALENDAR_0005, //Participant status cannot be set to "pending" after
	// participant creation
	// ERR_CALENDAR_0006, //I/O error
	// ERR_CALENDAR_0007, //ical parsing error
	// ERR_CALENDAR_0008, //ical validation error
	// ERR_CALENDAR_0009; //JMS error
	// }

	public QCalendarException(String message) {
		super(message);
	}

}