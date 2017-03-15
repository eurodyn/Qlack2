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
package com.eurodyn.qlack2.fuse.chatim.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.Order;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.fuse.chatim.api.ChatUserService;
import com.eurodyn.qlack2.fuse.chatim.api.dto.ActionOnUserDTO;
import com.eurodyn.qlack2.fuse.chatim.api.exception.QChatIMException;
import com.eurodyn.qlack2.fuse.chatim.impl.model.ChaActionOnUser;
import com.eurodyn.qlack2.fuse.chatim.impl.model.ChaRooms;
import com.eurodyn.qlack2.fuse.chatim.impl.util.ChatMessage;
import com.eurodyn.qlack2.fuse.chatim.impl.util.LookupHelper;

/**
 *
 * @author European Dynamics SA
 */
@Transactional
public class ChatUserServiceImpl implements ChatUserService {
	public static final Logger LOGGER = Logger.getLogger(ChatUserServiceImpl.class
			.getName());
	@PersistenceContext(unitName = "fuse-chatim")
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param roomID
	 *            {@inheritDoc}
	 * @param userID
	 *            {@inheritDoc}
	 * @param actionID
	 *            {@inheritDoc}
	 * @param reason
	 *            {@inheritDoc}
	 * @param period
	 *            {@inheritDoc}
	 * @param userFullname
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public ActionOnUserDTO performAction(String roomID, String userID,
			String actionID, String reason, long period, String userFullname)
			throws QChatIMException {
		ChaRooms chatRoom = LookupHelper.getRoom(em, roomID);
		ChaActionOnUser action = getLastActionForUser(roomID, userID, actionID);
		if (action == null) {
			action = new ChaActionOnUser(chatRoom, userID, actionID,
					System.currentTimeMillis());
		}
		action.setCreatedOn(System.currentTimeMillis());
		action.setReason(reason);

		if (period != 0) {
			action.setActionPeriod(System.currentTimeMillis() + period);
		}
		em.persist(action);

		// Post a notification about the event.
		// if
		// (PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.ChatIM.realtime.JMS.notifications").equals("true"))
		// {
		// ChatMessage message = new ChatMessage();
		// message.setType(ChatMessage.MSGTYPE_ACTION_ON_USER);
		// message.setStringProperty(ChatMessage.PROPERTY__ROOMID, roomID);
		// message.setStringProperty(ChatMessage.PROPERTY__ACTIONID, actionID);
		// message.setStringProperty(ChatMessage.PROPERTY__ACTION_FOR_USERID,
		// userID);
		// message.setStringProperty(ChatMessage.PROPERTY__ACTION_FOR_USERFULLNAME,
		// userFullname);
		// if (!org.apache.commons.lang.StringUtils.isEmpty(reason)) {
		// message.setStringProperty(ChatMessage.PROPERTY__ACTION_DESCRIPTION,
		// reason);
		// }
		// if (period != 0) {
		// message.setStringProperty(ChatMessage.PROPERTY__ACTION_PERIOD,
		// String.valueOf(action.getActionPeriod()));
		// }
		//
		// try {
		// Messenger.post(connectionFactory, notificationTopic, message);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		// throw new QChatIMException(QChatIMException.CODES.ERR_CHA_0001,
		// ex.getMessage());
		// }
		// }

		return convertActionOnUserTODTO(action);
	}

	/**
	 * Utility method to convert a ChaActionOnUser model class to
	 * ActionOnUserDTO DTO.
	 *
	 * @param actionOnUser
	 *            The model class ChaActionOnUser.
	 * @return An ActionOnUserDTO DTO representing the ChaActionOnUser model
	 *         class passed.
	 */
	private ActionOnUserDTO convertActionOnUserTODTO(
			ChaActionOnUser actionOnUser) {
		ActionOnUserDTO aoudto = new ActionOnUserDTO();
		aoudto.setId(actionOnUser.getId());
		aoudto.setActionId(actionOnUser.getActionId());
		aoudto.setUserId(actionOnUser.getUserId());
		aoudto.setRoomId(actionOnUser.getRoomId().getId());
		aoudto.setCreatedOn(actionOnUser.getCreatedOn());
		aoudto.setReason(actionOnUser.getReason());
		if (actionOnUser.getActionPeriod() != null) {
			aoudto.setActionPeriod(actionOnUser.getActionPeriod());
		}
		return aoudto;

	}

	/**
	 * Checks the last action of a particular type that was performed for a user
	 * on a specific room. 'Last', refers to the time that the action was
	 * performed, so the most recent action of the given type will be returned.
	 *
	 * @param roomID
	 *            The ID of the room for which the action was performed.
	 * @param userID
	 *            The ID of the user on which the action was performed.
	 * @param actionID
	 *            The ID of the action that was performed. This is an arbitrary
	 *            value that the caller defines according to its own business
	 *            logic.
	 * @return The action that was found matching the given parameters or null
	 *         if no action can be found.
	 */
	private ChaActionOnUser getLastActionForUser(String roomID, String userID,
			String actionID) {
		// TODO Kaskoura
		// ChaRooms chatRoom = LookupHelper.getRoom(em, roomID);
		//
		// Criteria criteria =
		// CriteriaFactory.createCriteria("ChaActionOnUser");
		// criteria.add(Restrictions.eq("actionId", actionID));
		// criteria.add(Restrictions.eq("roomId", chatRoom));
		// criteria.add(Restrictions.eq("userId", userID));
		// criteria.addOrder(Order.desc("createdOn"));
		// Query query = criteria.prepareQuery(em);
		// query.setMaxResults(1);
		// List results = query.getResultList();
		//
		// return results.isEmpty() ? null
		// : (ChaActionOnUser)results.get(0);
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param roomID
	 *            {@inheritDoc}
	 * @param userID
	 *            {@inheritDoc}
	 * @param actionID
	 *            {@inheritDoc}
	 * @param userFullname
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void removeActionPerformed(String roomID, String userID,
			String actionID, String userFullname) throws QChatIMException {
		ChaActionOnUser action = getLastActionForUser(roomID, userID, actionID);
		if (action != null) {
			em.remove(action);
		}

		// Post a notification about the event.
		// if
		// (PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.ChatIM.realtime.JMS.notifications").equals("true"))
		// {
		// ChatMessage message = new ChatMessage();
		// message.setType(ChatMessage.MSGTYPE_ACTION_ON_USER_REMOVED);
		// message.setStringProperty(ChatMessage.PROPERTY__ROOMID, roomID);
		// message.setStringProperty(ChatMessage.PROPERTY__ACTIONID, actionID);
		// message.setStringProperty(ChatMessage.PROPERTY__ACTION_FOR_USERID,
		// userID);
		// message.setStringProperty(ChatMessage.PROPERTY__ACTION_FOR_USERFULLNAME,
		// userFullname);
		// try {
		// Messenger.post(connectionFactory, notificationTopic, message);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		// throw new QChatIMException(QChatIMException.CODES.ERR_CHA_0001,
		// ex.getMessage());
		// }
		// }
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param roomID
	 *            {@inheritDoc}
	 * @param userID
	 *            {@inheritDoc}
	 * @param actionID
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public boolean isActionActive(String roomID, String userID, String actionID) {
		// TODO Kaskoura
		// ChaRooms chatRoom = LookupHelper.getRoom(em, roomID);
		// Criteria criteria =
		// CriteriaFactory.createCriteria("ChaActionOnUser");
		// criteria.add(Restrictions.eq("actionId", actionID));
		// criteria.add(Restrictions.eq("roomId", chatRoom));
		// criteria.add(Restrictions.eq("userId", userID));
		// criteria.add(Restrictions.gt("actionPeriod",
		// System.currentTimeMillis()));
		// Query query = criteria.prepareQuery(em);
		// query.setMaxResults(1);
		//
		// return (!query.getResultList().isEmpty());

		return false;
	}

}