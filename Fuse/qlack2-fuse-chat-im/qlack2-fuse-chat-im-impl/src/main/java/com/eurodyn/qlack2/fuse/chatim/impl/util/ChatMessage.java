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
package com.eurodyn.qlack2.fuse.chatim.impl.util;


/**
 *
 * @author European Dynamics SA
 */
//public class ChatMessage extends QlackMessage {
	public class ChatMessage  {

    private final static String COMPONENT_NAME = "CHAT";
    public final static String MSGTYPE_ROOM_CREATED                 = "ROOM_CREATED";
    public final static String MSGTYPE_JOIN_ROOM                    = "JOIN_ROOM";
    public final static String MSGTYPE_LEAVE_ROOM                   = "LEAVE_ROOM";
    public final static String MSGTYPE_ROOM_DELETED                 = "ROOM_DELETED";
    public final static String MSGTYPE_ROOM_PROPERTY_SET            = "ROOM_PROPERTY_SET";
    public final static String MSGTYPE_ROOM_PROPERTY_DELETED        = "ROOM_PROPERTY_DELETED";
    public final static String MSGTYPE_ROOM_PROPERTY_GET            = "ROOM_PROPERTY_GET";
    public final static String MSGTYPE_ROOM_FILTER_SET              = "ROOM_FILTER_SET";
    public final static String MSGTYPE_ROOM_FILTER_GET              = "ROOM_FILTER_GET";
    public final static String MSGTYPE_ROOM_FILTER_DELETED          = "ROOM_FILTER_DELETED";
    public final static String MSGTYPE_ROOM_MESSAGE_POSTED          = "ROOM_MESSAGE_POSTED";
    public final static String MSGTYPE_ACTION_ON_USER               = "ACTION_ON_USER";
    public final static String MSGTYPE_ACTION_ON_USER_REMOVED       = "ACTION_ON_USER_REMOVED";
    public final static String MSGTYPE_STATISTICS                   = "STATISTICS";
    public final static String MSGTYPE_IM                           = "IM";

    public final static String PROPERTY__ROOM_NAME                  = "ROOM_NAME";
    public final static String PROPERTY__ROOMID                     = "ROOMID";
    public final static String PROPERTY__ROOM_PROPERTY_NAME         = "ROOM_PROPERTY_NAME";
    public final static String PROPERTY__ROOM_PROPERTY_VALUE        = "ROOM_PROPERTY_VALUE";
    public final static String PROPERTY__ROOM_FILTER_VALUE          = "ROOM_FILTER_VALUE";
    public final static String PROPERTY__ACTIONID                   = "ACTIONID";
    public final static String PROPERTY__ACTION_FOR_USERID          = "ACTION_FOR_USERID";
    public final static String PROPERTY__ACTION_FOR_USERFULLNAME    = "ACTION_FOR_USERFULLNAME";
    public final static String PROPERTY__ACTION_DESCRIPTION         = "ACTION_DESCRIPTION";
    public final static String PROPERTY__ACTION_PERIOD              = "ACTION_PERIOD";

    public ChatMessage() {
//        this.setComponent(COMPONENT_NAME);
    }

}