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
package com.eurodyn.qlack2.fuse.simm.api.dto;

/**
 *
 * @author European Dynamics SA
 */
public class SIMMConstants {
    /* Notifications */
    public static final byte NOTIFICATION_STATUS_UNREAD = 0;
    public static final byte NOTIFICATION_STATUS_READ = 1;

    /* Groups */
    public static final byte GROUP_STATUS_SUSPENDED = 0;
    public static final byte GROUP_STATUS_APPROVED = 1;

    /* The status a user has in a group */
    public static final byte GROUP_USER_STATUS_REQUESTED_NEW = 0;
    public static final byte GROUP_USER_STATUS_ACCEPTED = 1;
    public static final byte GROUP_USER_STATUS_INVITED = 2;
    public static final byte GROUP_USER_STATUS_BANNED = 3;

    /* The privacy type of a group */
    public static final byte GROUP_PRIVACY_PRIVATE = 0;
    public static final byte GROUP_PRIVACY_PUBLIC = 1;
    public static final byte GROUP_PRIVACY_INVITED = 2;

    /* Activities posted in the homepage can undergo acceptance if necessary */
    public static final byte HOME_PAGE_ACTIVITY_STATUS_NEW = 1;
    public static final byte HOME_PAGE_ACTIVITY_STATUS_APPROVED = 2;

    // Regex for allowed group name
    public static final String TITLE_REGEX = "[\\W&&[^\\s]]";
    // A safety, to replace invalid chars in the group name.
    public static final String TITLE_REGEX_REPLACEMENT = "_";
//    public static final byte GROUP_ACTIVITY_NEW = 0;
//    public static final byte GROUP_ACTIVITY_APPROVED = 1;
//    public static final byte GROUP_ACTIVITY_ABUSED = 2;
//    public static final byte GROUP_ACTIVITY_BLOCKED = 3;

}
