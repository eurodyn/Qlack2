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
package com.eurodyn.qlack2.fuse.forum.impl.util;


/**
 *
 * @author European Dynamics SA
 */
//public class ForumMessage extends QlackMessage {
	public class ForumMessage {

    private final static String COMPONENT_NAME = "Forum";
    public static final String MSGTYPE__FORUM_CREATED = "FORUM_CREATED";
    public static final String MSGTYPE__FORUM_DELETE= "CREATE_DELETE_GROUP_FORUM";
    public static final String MSGTYPE__TOPIC_CREATED = "TOPIC_CREATED";
    public static final String MSGTYPE__POST_MESSAGE = "POST_MESSAGE";
    public static final String MSGTYPE__ACCEPT_TOPIC = "ACCEPT_TOPIC";
    public static final String MSGTYPE__REJECT_TOPIC = "REJECT_TOPIC";
    public static final String MSGTYPE__ACCEPT_MESSAGE = "ACCEPT_MESSAGE";
    public static final String MSGTYPE__REJECT_MESSAGE = "REJECT_MESSAGE";

    public static final String PROPERTY__FORUM_ID = "FORUM_ID";
    public static final String PROPERTY__FORUM_TITLE = "FORUM_TITLE";
    public static final String PROPERTY__TOPIC_ID = "TOPIC_ID";
    public static final String PROPERTY__TOPIC_TITLE = "TOPIC_TITLE";
    public static final String PROPERTY__MESSAGE_ID = "MESSAGE_ID";

    public ForumMessage() {
//        this.setComponent(COMPONENT_NAME);
    }
}
