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
package com.eurodyn.qlack2.fuse.wiki.impl.util;


/**
 *
 * @author European Dynamics SA
 */
//public class WikiMessage extends QlackMessage {
	public class WikiMessage {

    private final static String COMPONENT_NAME = "Wiki";
    public static final String MSGTYPE__WIKI_ENTRY_UPDATED = "WIKI_ENTRY_UPDATED";

    public static final String PROPERTY__WIKI_ENTRY_ID = "WIKI_ENTRY_ID";
    public static final String PROPERTY__WIKI_ENTRY_TITLE = "WIKI_ENTRY_TITLE";

    public WikiMessage() {
        //this.setComponent(COMPONENT_NAME);
    }
}
