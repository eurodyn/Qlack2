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
package com.eurodyn.qlack2.fuse.forum.api.dto;

/**
 *
 * @author European Dynamics SA.
 */
public class ForumConstants {
    //Forum statuses
    public static final short FORUM_STATUS_LOCKED = 0;
    public static final short FORUM_STATUS_UNLOCKED = 1;

    //Topic statuses
    public static final short TOPIC_STATUS_LOCKED = 0;
    public static final short TOPIC_STATUS_UNLOCKED = 1;

    //Forum moderated properties
    public static final short FORUM_NOT_MODERATED = 0;
    public static final short FORUM_MODERATED = 1;
    public static final short FORUM_SUPPORTS_MODERATION = 2;

    //Moderation statuses
    public static final short MODERATION_STATUS_REJECTED = 0;
    public static final short MODERATION_STATUS_ACCEPTED = 1;
    public static final short MODERATION_STATUS_PENDING = 2;
}
