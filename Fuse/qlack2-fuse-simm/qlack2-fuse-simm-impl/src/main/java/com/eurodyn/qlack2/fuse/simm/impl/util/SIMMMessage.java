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
package com.eurodyn.qlack2.fuse.simm.impl.util;


/**
 *
 * @author European Dynamics SA
 */
public class SIMMMessage {

    private final static String COMPONENT_NAME = "SIMM";
    public final static String MSGTYPE__FRIENDSHIP_ACCEPTED             = "FRIENDSHIP_ACCEPTED";
    public final static String MSGTYPE__FRIENDSHIP_REQUESTED            = "FRIENDSHIP_REQUESTED";
    public final static String MSGTYPE__FRIENDSHIP_REJECTED             = "FRIENDSHIP_REJECTED";
    public final static String MSGTYPE__GROUP_INVITATION                = "GROUP_INVITATION";
    public final static String MSGTYPE__GROUP_REQUEST_ACCEPTED          = "GROUP_REQUEST_ACCEPTED";
    public final static String MSGTYPE__GROUP_REQUEST_REJECTED          = "GROUP_REQUEST_REJECTED";
    public final static String MSGTYPE__ACTIVITY_UPDATED                = "ACTIVITY_UPDATED";
    public final static String MSGTYPE__ACTIVITY_CREATED                = "ACTIVITY_CREATED";
    public final static String MSGTYPE__ACTIVITY_DELETED                = "ACTIVITY_DELETED";
    public final static String MSGTYPE__NOTIFICATION_CREATED            = "NOTIFICATION_CREATED";

    public final static String PROPERTY__HOMEPAGE_ID                    = "HOMEPAGE_ID";
    public final static String PROPERTY__ACTIVITY_ID                    = "ACTIVITY_ID";
    public final static String PROPERTY__ACTIVITY_TITLE                 = "ACTIVITY_TITLE";
    public final static String PROPERTY__PARENT_ACTIVITY_ID             = "PARENT_ACTIVITY_ID";
    public final static String PROPERTY__PARENT_ACTIVITY_TITLE          = "PARENT_ACTIVITY_TITLE";
    public final static String PROPERTY__ACTIVITY_CATEGORY_ID           = "ACTIVITY_CATEGORY_ID";
    public final static String PROPERTY__FRIEND_ID                      = "FRIEND_ID";
    public final static String PROPERTY__FRIENDSHIP_CONFIRMED_ON        = "FRIENDSHIP_CONFIRMED_ON";
    public final static String PROPERTY__GROUP_ID                       = "GROUP_ID";
    public final static String PROPERTY__GROUP_TITLE                    = "GROUP_TITLE";
    public final static String PROPERTY__GROUP_PRIVATE                  = "GROUP_PRIVACY";
    public final static String PROPERTY__NOTIFICATION_TITLE             = "NOTIFICATION_TITLE";
    public final static String PROPERTY__NOTIFICATION_DESCRIPTION       = "NOTIFICATION_DESCRIPTION";
    public final static String PROPERTY__NOTIFICATION_LINK              = "NOTIFICATION_LINK";
    public final static String PROPERTY__NOTIFICATION_TYPE              = "NOTIFICATION_TYPE";
    public final static String PROPERTY__NOTIFICATION_CUSTOMICON_URL    = "NOTIFICATION_CUSTOMICON_URL";

    public SIMMMessage() {
//        this.setComponent(COMPONENT_NAME);
    }

}