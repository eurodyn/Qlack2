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

package com.eurodyn.qlack2.fuse.blog.impl.util;


/**
 *
 * @author European Dynamics SA
 */
public class BlogMessage {
//public class BlogMessage extends QlackMessage {
    private final static String COMPONENT_NAME = "Blog";

    public static final String MSGTYPE_COMMENT_POSTED               = "COMMENT_POSTED";

    public static final String PROPERTY__POST_ID                    = "POST_ID";
    public static final String PROPERTY__POST_TITLE                 = "POST_TITLE";
    public static final String PROPERTY__COMMENT_ID                 = "COMMENT_ID";

    public BlogMessage() {
//        this.setComponent(COMPONENT_NAME);
    }

}
