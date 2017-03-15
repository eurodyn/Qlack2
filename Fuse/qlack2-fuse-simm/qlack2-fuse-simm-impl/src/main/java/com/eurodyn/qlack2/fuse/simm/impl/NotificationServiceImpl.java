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
package com.eurodyn.qlack2.fuse.simm.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.simm.api.NotificationService;
import com.eurodyn.qlack2.fuse.simm.api.dto.NotificationDTO;
import com.eurodyn.qlack2.fuse.simm.api.dto.SIMMConstants;
import com.eurodyn.qlack2.fuse.simm.api.exception.QSIMMException;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimNotification;

/**
 *
 * @author European Dynamics SA
 */
@Transactional
public class NotificationServiceImpl implements NotificationService {
	public static final Logger LOGGER = Logger
			.getLogger(NotificationServiceImpl.class.getName());
	@PersistenceContext(unitName = "fuse-simm")
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	/**
	 * Create a notification
	 *
	 * @param notificationDTO
	 * @return
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public String createNotification(NotificationDTO notificationDTO)
			throws QSIMMException {
		// Persist the notification in the database.
		SimNotification notification = new SimNotification();
		notification.setCreatedOn(System.currentTimeMillis());
		notification.setDescription(notificationDTO.getDescription());
		notification.setFromUserId(notificationDTO.getFromUserID());
		notification.setLink(notificationDTO.getLink());
		notification.setStatus(SIMMConstants.NOTIFICATION_STATUS_UNREAD);
		notification.setTitle(notificationDTO.getTitle());
		notification.setToUserId(notificationDTO.getToUserID());
		notification.setNotfType(notificationDTO.getType());
		notification.setNotfIcon(notificationDTO.getCustomIconURL());
		em.persist(notification);

		// Post a realtime notification about the event.
		// if
		// (PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.SIMM.realtime.JMS.notifications").equals("true"))
		// {
		// SIMMMessage jmsMessage = new SIMMMessage();
		// jmsMessage.setType(SIMMMessage.MSGTYPE__NOTIFICATION_CREATED);
		// jmsMessage.setSrcUserID(notificationDTO.getFromUserID());
		// jmsMessage.setStringProperty(SIMMMessage.PRIVATE_USERID,
		// notificationDTO.getToUserID());
		// jmsMessage.setStringProperty(SIMMMessage.PROPERTY__NOTIFICATION_TITLE,
		// notificationDTO.getTitle() != null ? notificationDTO.getTitle() :
		// "");
		// jmsMessage.setStringProperty(SIMMMessage.PROPERTY__NOTIFICATION_DESCRIPTION,
		// notificationDTO.getDescription() != null ?
		// notificationDTO.getDescription() : "");
		// jmsMessage.setStringProperty(SIMMMessage.PROPERTY__NOTIFICATION_LINK,
		// notificationDTO.getLink() != null ? notificationDTO.getLink() : "");
		// jmsMessage.setStringProperty(SIMMMessage.PROPERTY__NOTIFICATION_CUSTOMICON_URL,
		// notificationDTO.getCustomIconURL() != null ?
		// notificationDTO.getCustomIconURL() : "");
		// jmsMessage.setStringProperty(SIMMMessage.PROPERTY__NOTIFICATION_TYPE,
		// notificationDTO.getType() != null ? notificationDTO.getType() : "");
		// jmsMessage.setStringProperty(SIMMMessage.PROPERTY__AUTOBALLOON,
		// notificationDTO.isShowAutoBalloon() ? "true" : "false");
		// try {
		// Messenger.post(connectionFactory, notificationTopic, jmsMessage);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage());
		// throw new
		// QlackFuseSIMMException(QlackFuseSIMMException.CODES.ERR_SIMM_0033,
		// ex.getLocalizedMessage());
		// }
		// }

		return (notification.getId());
	}

	/**
	 * Convert Query data to array of NotificationDTO
	 *
	 * @param q
	 * @return
	 */
	private NotificationDTO[] searchResultsToDTOArray(Query q) {
		ArrayList retVal = new ArrayList();

		for (Iterator<SimNotification> i = q.getResultList().iterator(); i
				.hasNext();) {
			SimNotification notification = i.next();
			NotificationDTO notificationDTO = new NotificationDTO();
			notificationDTO.setCreatedOn(notification.getCreatedOn());
			notificationDTO.setDescription(notification.getDescription());
			notificationDTO.setFromUserID(notification.getFromUserId());
			notificationDTO.setLink(notification.getLink());
			notificationDTO.setStatus(notification.getStatus());
			notificationDTO.setTitle(notification.getTitle());
			notificationDTO.setToUserID(notification.getToUserId());
			notificationDTO.setType(notification.getNotfType());
			notificationDTO.setCustomIconURL(notification.getNotfIcon());

			retVal.add(notificationDTO);
		}

		return (NotificationDTO[]) retVal.toArray(new NotificationDTO[retVal
				.size()]);
	}

	/**
	 * Retrieve all pending notification for a user
	 *
	 * @param userID
	 * @param paging
	 * @return
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public NotificationDTO[] getPendingNotifications(String userID,
			PagingParams paging) {
		Query q = em.createQuery("select n from SimNotification n where "
				+ "n.toUserId = :toUserId and n.status = :status "
				+ "order by n.createdOn desc");
		q.setParameter("toUserId", userID);
		q.setParameter("status", SIMMConstants.NOTIFICATION_STATUS_UNREAD);
		  if ((paging != null) && (paging.getCurrentPage() > -1)) {
			q.setFirstResult((paging.getCurrentPage() - 1) * paging.getPageSize());
			q.setMaxResults(paging.getPageSize());
		}

		NotificationDTO[] notificationDTOs = (searchResultsToDTOArray(q));
		return notificationDTOs;
	}

	/**
	 * @param userID
	 * @param timeBeforeToCheck
	 * @param paging
	 * @return
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public NotificationDTO[] getNotificationsByTime(String userID,
			long timeBeforeToCheck, PagingParams paging) {
		Query q = em.createQuery("select n from SimNotification n where "
				+ "n.toUserId = :toUserId and n.createdOn <= :createdOn "
				+ "order by n.createdOn desc");
		q.setParameter("toUserId", userID);
		q.setParameter("createdOn", timeBeforeToCheck);
		 if ((paging != null) && (paging.getCurrentPage() > -1)) {
			 q.setFirstResult((paging.getCurrentPage() - 1) * paging.getPageSize());
			 q.setMaxResults(paging.getPageSize());
		 }
		NotificationDTO[] notificationDTOs = (searchResultsToDTOArray(q));
		return notificationDTOs;
	}

	/**
	 * Get provided no of notifications for a user
	 *
	 * @param userID
	 * @param numberOfNotifications
	 * @return
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public NotificationDTO[] getNotificationsByNumber(String userID,
			int numberOfNotifications) {
		Query q = em.createQuery("select n from SimNotification n where "
				+ "n.toUserId = :toUserId " + "order by n.createdOn desc");
		q.setParameter("toUserId", userID);
		PagingParams paging = new PagingParams(numberOfNotifications, 1);
		if ((paging != null) && (paging.getCurrentPage() > -1)) {
			q.setFirstResult((paging.getCurrentPage() - 1) * paging.getPageSize());
			q.setMaxResults(paging.getPageSize());
		}

		NotificationDTO[] notificationDTOs = (searchResultsToDTOArray(q));
		return notificationDTOs;
	}

	/**
	 * Get all notifications for a user
	 *
	 * @param userID
	 * @param paging
	 * @return
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public NotificationDTO[] getNotifications(String userID, PagingParams paging) {
		Query q = em.createQuery("select n from SimNotification n where "
				+ "n.toUserId = :toUserId " + "order by n.createdOn desc");
		q.setParameter("toUserId", userID);
		if ((paging != null) && (paging.getCurrentPage() > -1)) {
			q.setFirstResult((paging.getCurrentPage() - 1) * paging.getPageSize());
			q.setMaxResults(paging.getPageSize());
		}

		// Update cache.
		NotificationDTO[] notificationDTOs = (searchResultsToDTOArray(q));
		return notificationDTOs;
	}

	/**
	 * Get all notifications that has been created after provided time for a
	 * user and for a provided type
	 *
	 * @param userID
	 * @param type
	 * @param notificationCreatedAfterTime
	 * @return
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public NotificationDTO[] getNotificationsForAType(String userID,
			String type, long notificationCreatedAfterTime) {
		Query q = em.createQuery("select n from SimNotification n where "
				+ "n.toUserId = :toUserId "
				+ (notificationCreatedAfterTime == 0 ? ""
						: "and n.createdOn >= :createdOn ")
				+ (type == null ? "" : "and n.notfType = :notfType ")
				+ "order by n.createdOn desc");
		q.setParameter("toUserId", userID);
		if (notificationCreatedAfterTime != 0) {
			q.setParameter("createdOn", notificationCreatedAfterTime);
		}
		if (type != null) {
			q.setParameter("notfType", type);
		}

		NotificationDTO[] notificationDTOs = (searchResultsToDTOArray(q));
		return notificationDTOs;
	}

	/**
	 * Mark all pending notifications as read
	 *
	 * @param userID
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void markPendingNotificationsAsRead(String userID) {
		Query q = em.createQuery("update SimNotification n "
				+ "set n.status = :newStatus "
				+ "where n.toUserId = :toUserId and n.status = :status ");
		q.setParameter("status", SIMMConstants.NOTIFICATION_STATUS_UNREAD);
		q.setParameter("newStatus", SIMMConstants.NOTIFICATION_STATUS_READ);
		q.setParameter("toUserId", userID);
		q.executeUpdate();
	}

	/**
	 * Mark provided notification as read
	 *
	 * @param notificationIDs
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void markNotificationsAsRead(String[] notificationIDs) {
		Query q = em.createQuery("update SimNotification n "
				+ "set n.status = :newStatus "
				+ "where n.id in (:notifications)");
		q.setParameter("newStatus", SIMMConstants.NOTIFICATION_STATUS_READ);
		q.setParameter("notifications", Arrays.asList(notificationIDs));
		q.executeUpdate();
	}

}
