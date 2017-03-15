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
package com.eurodyn.qlack2.fuse.simm.api.notifications;

import java.io.Serializable;

import com.eurodyn.qlack2.fuse.simm.api.dto.NotificationDTO;

/**
 *
 * @author European Dynamics SA
 */
public abstract class Notification implements Serializable {
	private static final long serialVersionUID = -6785510804739065517L;

	protected String fromUserID;

	public Notification(String userID) {
		this.fromUserID = userID;
	}

	public NotificationDTO getNotification(String toUserID, String itemID) {
		return getNotification(toUserID, itemID, null, null, null);
	}

	public NotificationDTO getNotification(String toUserID, String customTitle,
			String customDescription) {
		return getNotification(toUserID, customTitle, customDescription, null,
				null);
	}

	public NotificationDTO getNotification(String toUserID, String customTitle,
			String customDescription, String link) {
		return getNotification(toUserID, customTitle, customDescription, link,
				null);
	}

	public NotificationDTO getNotification(String toUserID, String customTitle,
			String customDescription, String link, String customIconURL) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setCreatedOn(System.currentTimeMillis());
		notificationDTO.setFromUserID(fromUserID);
		notificationDTO.setToUserID(toUserID);
		notificationDTO.setType("system");
		notificationDTO.setTitle(customTitle);
		notificationDTO.setDescription(customDescription);
		notificationDTO.setCustomIconURL(customIconURL);
		notificationDTO.setLink(link);

		return notificationDTO;
	}

}