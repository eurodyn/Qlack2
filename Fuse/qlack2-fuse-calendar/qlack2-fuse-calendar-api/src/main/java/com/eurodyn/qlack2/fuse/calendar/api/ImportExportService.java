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

import java.util.Date;

import com.eurodyn.qlack2.fuse.calendar.api.exception.QCalendarException;

/**
 *
 * @author European Dynamics SA
 */
public interface ImportExportService {

	/**
	 * Exports a set to a calendar items to ical format.
	 *
	 * @param calendarId
	 *            The id of the calendar from which to export calendar items
	 * @param startDate
	 *            If not null then only the calendar items with a start date of
	 *            at least startDate will be exported.
	 * @param endDate
	 *            If not null then only the calendar items with a start date of
	 *            at most toDate will be exported.
	 * @param categoryIds
	 *            The categories of the calendar items to export. If this
	 *            parameter is null then all the calendar items satisfying the
	 *            rest of the constraints will be exported
	 * @param includeSupportingObjects
	 *            If true then the supporting objects of the retrieved calendar
	 *            items will also be included in the export
	 * @return A String holding the exported items in ical format.
	 * @throws QCalendarException
	 *             If an error happens during the output of the ical String.
	 */
	String exportCalendarItems(String calendarId, Date startDate, Date endDate,
			String[] categoryIds, boolean includeSupportingObjects)
			throws QCalendarException;

	/**
	 * Imports a set of calendar items to a calendar. The import process is as
	 * follows: For each ical vevent found in the data to import it's id is
	 * checked against the ids of the items already existing in the specified
	 * calendar. If an item with this id does not exist then the venent is
	 * inserted in the calendar with a new id, while if an item with this id
	 * already exists then the procedure defined by the onClashBehavior
	 * parameter takes place.
	 *
	 * @param calendarId
	 *            The id of the calendar to which to import the calendar items.
	 * @param importData
	 *            A String holding the items to be imported in ical format.
	 * @param onClashBehaviour
	 *            Defined how to treat cases of id clash.
	 * @throws QCalendarException
	 *             If the specified calendar does not exist or if an error
	 *             happens during the import of the ical String.
	 */
	void importCalendarItems(String calendarId, String importData,
			short onClashBehaviour) throws QCalendarException;

}
