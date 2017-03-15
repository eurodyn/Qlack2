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
package com.eurodyn.qlack2.fuse.simm.api;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.simm.api.dto.NotificationDTO;
import com.eurodyn.qlack2.fuse.simm.api.exception.QSIMMException;

/**
 *
 * @author European Dynamics SA
 */
public interface NotificationService {

    /**
     * Create a notification
     * @param notificationDTO
     * @return
     */
    String createNotification(NotificationDTO notificationDTO) throws QSIMMException;

    /**
     * Retrieve all pending notification for a user
     * @param userID
     * @param paging
     * @return
     */
    NotificationDTO[] getPendingNotifications(String userID, PagingParams paging);

    /**
     * Get all notification that has been created before provided time for a user
     * @param userID
     * @param timeBeforeToCheck
     * @param paging
     * @return
     */
    NotificationDTO[] getNotificationsByTime(String userID, long timeBeforeToCheck, PagingParams paging);

    /**
     * Get provided no of notifications for a user
     * @param userID
     * @param numberOfNotifications
     * @return
     */
    NotificationDTO[] getNotificationsByNumber(String userID, int numberOfNotifications);

    /**
     * Get all notifications for a user
     *
     * @param userID
     * @param paging
     * @return
     */
    NotificationDTO[] getNotifications(String userID, PagingParams paging);

    /**
     * Mark all pending notifications as read
     *
     * @param userID
     */
    void markPendingNotificationsAsRead(String userID);

    /**
     * Mark provided notifications as read
     *
     * @param notificationID
     */
    void markNotificationsAsRead(String[] notificationID);

    /**
     * Get all notifications that has been created after provided time for a user and for a provided type
     *
     * @param userID
     * @param type
     * @param notificationCreatedAfterTime
     * @return
     */
    NotificationDTO[] getNotificationsForAType(String userID, String type, long notificationCreatedAfterTime);
}
