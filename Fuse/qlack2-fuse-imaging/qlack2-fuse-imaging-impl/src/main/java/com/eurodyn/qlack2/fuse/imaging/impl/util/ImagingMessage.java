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
package com.eurodyn.qlack2.fuse.imaging.impl.util;


/**
 *
 * @author European Dynamics SA
 */
//public class ImagingMessage extends QlackMessage {
	public class ImagingMessage {

    private final static String COMPONENT_NAME = "IMAGING";

    public static final String MSGTYPE_FOLDER_CREATED               = "FOLDER_CREATED";
    public static final String MSGTYPE_FOLDER_UPDATED               = "FOLDER_UPDATED";
    public static final String MSGTYPE_FOLDER_DELETED               = "FOLDER_DELETED";
    public static final String MSGTYPE_FOLDER_ATTRIBUTE_CREATED     = "FOLDER_ATTRIBUTE_CREATED";
    public static final String MSGTYPE_FOLDER_ATTRIBUTE_UPDATED     = "FOLDER_ATTRIBUTE_UPDATED";
    public static final String MSGTYPE_FOLDER_ATTRIBUTE_DELETED     = "FOLDER_ATTRIBUTE_DELETED";
    public static final String MSGTYPE_IMAGE_CREATED                = "IMAGE_CREATED";
    public static final String MSGTYPE_IMAGE_UPDATED                = "IMAGE_UPDATED";
    public static final String MSGTYPE_IMAGE_DELETED                = "IMAGE_DELETED";
    public static final String MSGTYPE_IMAGE_ATTRIBUTE_CREATED      = "IMAGE_ATTRIBUTE_CREATED";
    public static final String MSGTYPE_IMAGE_ATTRIBUTE_UPDATED      = "IMAGE_ATTRIBUTE_UPDATED";
    public static final String MSGTYPE_IMAGE_ATTRIBUTE_DELETED      = "IMAGE_ATTRIBUTE_DELETED";

    public static final String PROPERTY__PARENT_FOLDER_ID           = "PARENT_FOLDER_ID";
    public static final String PROPERTY__FOLDER_ID                  = "FOLDER_ID";
    public static final String PROPERTY__FOLDER_NAME                = "FOLDER_NAME";
    public static final String PROPERTY__IMAGE_ID                   = "IMAGE_ID";
    public static final String PROPERTY__IMAGE_NAME                 = "IMAGE_NAME";
    public static final String PROPERTY__ATTRIBUTE_ID               = "ATTRIBUTE_ID";
    public static final String PROPERTY__ATTRIBUTE_NAME             = "ATTRIBUTE_NAME";


    public ImagingMessage() {
//        this.setComponent(COMPONENT_NAME);
    }
}
