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
package com.eurodyn.qlack2.fuse.calendar.api;

import java.util.List;

import com.eurodyn.qlack2.fuse.calendar.api.dto.CalendarDTO;
import com.eurodyn.qlack2.fuse.calendar.api.exception.QCalendarException;

/**
 *
 * @author European Dynamics SA
 */
public interface CalendarService {

	/**
	 * Creates a new calendar. It is the caller's responsibility to have set the
	 * ownerId, createdOn and lastModifiedOn/By properties.
	 *
	 * @param calendar
	 *            The data of the calendar to create.
	 * @return The newly created calendar.
	 */
	CalendarDTO createCalendar(CalendarDTO calendar);

	/**
	 * Modifies an existing calendar. It is the caller's responsibility to have
	 * correctly set the lastModifiedOn/By properties.
	 *
	 * @param calendar
	 *            The data of the calendar to update. This method takes into
	 *            account the following properties of the CalendarDTO class if
	 *            they are not null: - id: The id of the calendar to update.
	 *            This property should always be not null. - lastModifiedBy: The
	 *            id of the user performing this update. - lastModifiedOn -
	 *            ownerId: The new owner id.
	 * @throws QCalendarException
	 *             If the specified calendar does not exist.
	 */
	void updateCalendar(CalendarDTO calendar) throws QCalendarException;

	/**
	 * Deletes a calendar
	 *
	 * @param calendar
	 *            The calendar to delete.
	 * @throws QCalendarException
	 *             If the specified calendar does not exist.
	 */
	void deleteCalendar(CalendarDTO calendar) throws QCalendarException;

	/**
	 * Retrieves a specific calendar
	 *
	 * @param calendarId
	 *            The id of the calendar to retrieve
	 * @return The information of the retrieved calendar. Please note that this
	 *         method retrieves only the calendar itself and not the relative
	 *         calendar items.
	 */
	CalendarDTO getCalendar(String calendarId);

	/**
	 * Retrieves all calendars created in the system
	 *
	 * @param includeDisabled
	 *            If true then disabled calendars will be included in the
	 *            results, otherwise only enabled calendars will be returned.
	 * @return A list of all the calendars created in the system. Please note
	 *         that this method retrieves only the calendars themselves and not
	 *         the relative calendar items.
	 */
	List<CalendarDTO> getCalendars(boolean includeDisabled);

	/**
	 * Retrieves all the calendars having a specific owner
	 *
	 * @param ownerId
	 *            The id of the owner of the calendars to retrieve
	 * @return A list of the calendars having the specified owner. Please note
	 *         that this method retrieves only the calendars themselves and not
	 *         the relative calendar items.
	 */
	List<CalendarDTO> getCalendars(String ownerId);

	/**
	 * Activates a calendar. Please note that this method also sets the
	 * lastModifiedOn field.
	 *
	 * @param calendarId
	 *            The id of the calendar to activate
	 * @param userId
	 *            The id of the user performing this action, which will be used
	 *            to set the lastModifiedBy field if the calendar is
	 *            successfully activated.
	 * @return True if the calendar was successfully activated, false otherwise
	 *         (if the calendar was already active).
	 * @throws QCalendarException
	 *             If the specified calendar does not exist.
	 */
	boolean activateCalendar(String calendarId, String userId)
			throws QCalendarException;

	/**
	 * Deactivates a calendar. Please note that this method also sets the
	 * lastModifiedOn field.
	 *
	 * @param calendarId
	 *            The id of the calendar to deactivate.
	 * @param userId
	 *            The id of the user performing this action, which will be used
	 *            to set the lastModifiedBy field if the calendar is
	 *            successfully deactivated.
	 * @return True if the calendar was successfully deactivated, false
	 *         otherwise (if the calendar was already deactivated).
	 * @throws QCalendarException
	 *             If the specified calendar does not exist.
	 */
	boolean deactivateCalendar(String calendarId, String userId)
			throws QCalendarException;

	/**
	 * Determines whether a calendar is active or not
	 *
	 * @param calendarId
	 *            The id of the calendar to check
	 * @return True if the calendar is active, false otherwise.
	 * @throws QCalendarException
	 *             If the specified calendar does not exist.
	 */
	boolean isCalendarActive(String calendarId) throws QCalendarException;
}
