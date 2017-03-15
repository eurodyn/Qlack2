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
package com.eurodyn.qlack2.fuse.calendar.api.dto;

/**
 *
 * @author European Dynamics SA
 */
public class CalendarConstants {
    public final static short ON_CLASH_IGNORE = 0;           //In case of an id clash ignore the imported item and keep the existing one
    public final static short ON_CLASH_REPLACE = 1;          //In case of an id clash replace the existing item with the imported one
    public final static short ON_CLASH_KEEP_LATEST = 2;      //In case of an id clash keep the latest of the two items (the imported and the existing one) according to their lastModifiedOn property

}
