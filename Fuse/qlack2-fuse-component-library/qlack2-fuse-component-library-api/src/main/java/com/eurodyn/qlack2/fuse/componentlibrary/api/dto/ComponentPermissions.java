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
package com.eurodyn.qlack2.fuse.componentlibrary.api.dto;

/**
 * Constants to be used when requesting/checking permissions on behalf of a gadget.
 * @author European Dynamics SA
 */
public class ComponentPermissions {

    /**
     * Allow the gadget to post an entry to the user's homepage.
     */
    public final static String POST_TO_HOME = "post_to_home";
    /**
     * Allow the gadget to post an entry to the user's colleagues' homepages.
     */
    public final static String POST_TO_COLLEAGUES_HOME = "post_to_colleagues_home";
    /**
     * Allow the gadget to send emails to the user.
     */
    public final static String EMAIL_USER = "email_user";
    /**
     * Allow the gadget to send emails to the user's colleagues'.
     */
    public final static String EMAIL_COLLEAGUE = "email_colleague";
    /**
     * Allow the gadget to access user-related information even if the user is not
     * presently online. A user is considered online from the time it logs-in into the
     * system up to an amount of time specified by the application configuration parameter
     * 'qlack.fuse.modules.api.considerUserLoggedInForMinutes' (with a default value
     * of 24hrs).
     */
    public final static String OFFLINE_ACCESS = "offline_access";
    /**
     * Allow the gadget to read the incoming mailbox of the user.
     */
    public final static String MAILBOX = "mailbox";
    /**
     * Allow the gadget to post status updates for the user.
     */
    public final static String STATUS_UPDATE = "status_update";
}
