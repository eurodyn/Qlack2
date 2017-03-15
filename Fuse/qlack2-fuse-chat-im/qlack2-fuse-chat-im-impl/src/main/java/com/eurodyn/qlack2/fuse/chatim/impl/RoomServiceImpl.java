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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.StringUtils;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.chatim.api.RoomService;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomDTO;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomPropertyDTO;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomStatisticsDTO;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomUserDTO;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomWordFilterDTO;
import com.eurodyn.qlack2.fuse.chatim.api.exception.QChatIMException;
import com.eurodyn.qlack2.fuse.chatim.impl.model.ChaProperties;
import com.eurodyn.qlack2.fuse.chatim.impl.model.ChaRoomHasParticipants;
import com.eurodyn.qlack2.fuse.chatim.impl.model.ChaRooms;
import com.eurodyn.qlack2.fuse.chatim.impl.model.ChaWordFilter;
import com.eurodyn.qlack2.fuse.chatim.impl.util.LookupHelper;

/**
 *
 * @author European Dynamics SA
 */
@Transactional
public class RoomServiceImpl implements RoomService {
	public static final Logger LOGGER = Logger.getLogger(RoomServiceImpl.class
			.getName());
	@PersistenceContext(unitName = "fuse-chatim")
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param roomDTO
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QChatIMException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public String createRoom(RoomDTO roomDTO) throws QChatIMException {

		// Create the chat room.
		ChaRooms chatRoom = new ChaRooms();
		chatRoom.setCreatedBy(roomDTO.getCreatedByUserID() != null ? roomDTO
				.getCreatedByUserID() : "system");
		chatRoom.setCreatedOn(roomDTO.getCreatedOn() != null ? roomDTO
				.getCreatedOn() : System.currentTimeMillis());
		chatRoom.setTitle(roomDTO.getTitle());
		if (roomDTO.getTargetCommunityID() != null) {
			chatRoom.setGroupId(roomDTO.getTargetCommunityID());
		}
		em.persist(chatRoom);

		// Post a notification about the event.
		// if
		// (PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.ChatIM.realtime.JMS.notifications").equals("true"))
		// {
		// ChatMessage message = new ChatMessage();
		// message.setType(ChatMessage.MSGTYPE_ROOM_CREATED);
		// message.setStringProperty(ChatMessage.PROPERTY__ROOM_NAME,
		// roomDTO.getTitle());
		// message.setStringProperty(ChatMessage.PROPERTY__ROOMID,
		// chatRoom.getId());
		// try {
		// Messenger.post(connectionFactory, notificationTopic, message);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		// throw new QChatIMException(QChatIMException.CODES.ERR_CHA_0001,
		// ex.getLocalizedMessage());
		// }
		// }

		return chatRoom.getId();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param roomID
	 *            {@inheritDoc}
	 * @param userID
	 *            {@inheritDoc}
	 * @throws QChatIMException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void joinRoom(String roomID, String userID) throws QChatIMException {

		if (StringUtils.isEmpty(roomID)) {
			throw new QChatIMException("The ID of the room can not be empty.");
		}
		if (StringUtils.isEmpty(userID)) {
			throw new QChatIMException(
					"The ID of the user roomID can not be empty.");
		}

		// Get the requested room.
		ChaRooms chatRoom = LookupHelper.getRoom(em, roomID);
		Query q = em
				.createQuery("select chp from ChaRoomHasParticipants chp where "
						+ "chp.roomId = :room and chp.userId = :userId");
		q.setParameter("room", chatRoom);
		q.setParameter("userId", userID);
		List l = q.getResultList();
		if (l.isEmpty()) {
			ChaRoomHasParticipants crhp = new ChaRoomHasParticipants();
			crhp.setJoinedOn(System.currentTimeMillis());
			crhp.setRoomId(chatRoom);
			crhp.setUserId(userID);
			em.persist(crhp);
		}

		// Post a notification about the event.
		// if (PropertiesLoaderSingleton.getInstance()
		// .getProperty("QlackFuse.ChatIM.realtime.JMS.notifications")
		// .equals("true")) {
		// ChatMessage message = new ChatMessage();
		// message.setType(ChatMessage.MSGTYPE_JOIN_ROOM);
		// message.setStringProperty(ChatMessage.PROPERTY__ROOMID, roomID);
		// message.setStringProperty(ChatMessage.PROPERTY__ROOM_NAME,
		// chatRoom.getTitle());
		// message.setStringProperty(ChatMessage.PROPERTY__SRC_USERID, userID);
		// try {
		// Messenger.post(connectionFactory, notificationTopic, message);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		// throw new QChatIMException(QChatIMException.CODES.ERR_CHA_0001,
		// ex.getLocalizedMessage());
		// }
		// }
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param roomID
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public RoomUserDTO[] getRoomUsers(String roomID) {
		ChaRooms chatRoom = LookupHelper.getRoom(em, roomID);
		Set<ChaRoomHasParticipants> participants = chatRoom.getChaRoomHasParticipantses();
		ArrayList<RoomUserDTO> retVal = new ArrayList<RoomUserDTO>();
		Iterator<ChaRoomHasParticipants> i = participants.iterator();
		while (i.hasNext()) {
			ChaRoomHasParticipants crhp = i.next();
			retVal.add(new RoomUserDTO(crhp.getUserId(), crhp.getJoinedOn()));
		}

		RoomUserDTO[] roomDTOs = (RoomUserDTO[]) retVal
				.toArray(new RoomUserDTO[retVal.size()]);

		return roomDTOs;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param userID
	 *            {@inheritDoc}
	 * @param roomID
	 *            {@inheritDoc}
	 * @throws QChatIMException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void leaveRoom(String userID, String roomID) throws QChatIMException {
		// Get the requested room.
		ChaRooms chatRoom = LookupHelper.getRoom(em, roomID);

		// Check if the user is already a member of that room (i.e. since when
		// the user
		// leaves the chat page the application automatically removes the user
		// from the room,
		// this is an exceptional case).
		Query q = em
				.createQuery("select chp from ChaRoomHasParticipants chp where "
						+ "chp.roomId = :room and chp.userId = :userId");
		q.setParameter("room", chatRoom);
		q.setParameter("userId", userID);
		List<ChaRoomHasParticipants> l = q.getResultList();
		if (l.size() > 0) {
			Iterator<ChaRoomHasParticipants> i = l.iterator();
			ChaRoomHasParticipants crhp = i.next();
			em.remove(crhp);

			// Post a notification about the event.
			// if (PropertiesLoaderSingleton.getInstance()
			// .getProperty("QlackFuse.ChatIM.realtime.JMS.notifications")
			// .equals("true")) {
			// ChatMessage message = new ChatMessage();
			// message.setType(ChatMessage.MSGTYPE_LEAVE_ROOM);
			// message.setStringProperty(ChatMessage.PROPERTY__ROOMID, roomID);
			// message.setStringProperty(ChatMessage.PROPERTY__ROOM_NAME,
			// chatRoom.getTitle());
			// message.setStringProperty(ChatMessage.PROPERTY__SRC_USERID,
			// userID);
			// try {
			// Messenger.post(connectionFactory, notificationTopic,
			// message);
			// } catch (JMSException ex) {
			// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			// throw new QChatIMException(
			// QChatIMException.CODES.ERR_CHA_0001,
			// ex.getLocalizedMessage());
			// }
			// }
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param userID
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void leaveAllRooms(String userID) {
		Query q = em
				.createQuery("select chp from ChaRoomHasParticipants chp where "
						+ "chp.userId = :userId");
		q.setParameter("userId", userID);
		List<ChaRoomHasParticipants> l = q.getResultList();

		if (l.size() > 0) {
			Iterator<ChaRoomHasParticipants> i = l.iterator();
			while (i.hasNext()) {
				ChaRoomHasParticipants crhp = i.next();
				em.remove(crhp);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param groupIDs
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	public RoomDTO[] listAvailableRoomsForGroups(String[] groupIDs) {
		RoomDTO[] retVal;

		Query q = em
				.createQuery("select cr from ChaRooms cr where cr.groupId in (:groupIDs)");
		q.setParameter("groupIDs", Arrays.asList(groupIDs));
		retVal = convertChaRoomsToRoomDTOA(q.getResultList());

		return retVal;
	}

	private RoomDTO[] convertChaRoomsToRoomDTOA(List l) {
		RoomDTO[] retVal = new RoomDTO[0];
		if (l != null) {
			retVal = new RoomDTO[l.size()];
			Iterator<ChaRooms> i = l.iterator();
			int counter = 0;
			while (i.hasNext()) {
				ChaRooms chatRoom = i.next();
				RoomDTO roomDTO = new RoomDTO();
				if (chatRoom.getCreatedBy() != null) {
					roomDTO.setCreatedByUserID(chatRoom.getCreatedBy());
				}
				roomDTO.setCreatedOn(chatRoom.getCreatedOn());
				if (chatRoom.getGroupId() != null) {
					roomDTO.setTargetCommunityID(chatRoom.getGroupId());
				}
				roomDTO.setTitle(chatRoom.getTitle());
				roomDTO.setId(chatRoom.getId());
				retVal[counter++] = roomDTO;
			}
		}

		return retVal;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param roomDTO
	 *            {@inheritDoc}
	 * @throws QChatIMException
	 *             {@inheritDoc}
	 */
	@Override
	public void removeRoom(RoomDTO roomDTO) throws QChatIMException {
		// Get the requested room.
		ChaRooms chatRoom = LookupHelper.getRoom(em, roomDTO.getId());

		// remove the room
		em.remove(chatRoom);

		// Post a notification about the event.
		// if (PropertiesLoaderSingleton.getInstance()
		// .getProperty("QlackFuse.ChatIM.realtime.JMS.notifications")
		// .equals("true")) {
		// ChatMessage message = new ChatMessage();
		// message.setType(ChatMessage.MSGTYPE_ROOM_DELETED);
		// message.setStringProperty(ChatMessage.PROPERTY__ROOMID,
		// roomDTO.getId());
		// message.setStringProperty(ChatMessage.PROPERTY__ROOM_NAME,
		// chatRoom.getTitle());
		// try {
		// Messenger.post(connectionFactory, notificationTopic, message);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		// throw new QChatIMException(QChatIMException.CODES.ERR_CHA_0001,
		// ex.getLocalizedMessage());
		// }
		// }
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param room
	 *            {@inheritDoc}
	 * @param pagingParams
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	public RoomDTO[] searchRooms(RoomDTO room, PagingParams pagingParams) {
		// TODO Kaskoura
		// Criteria criteria = CriteriaFactory.createCriteria("ChaRooms");
		// if (room.getId() != null) {
		// criteria.add(Restrictions.eq("id", room.getId()));
		// }
		// if (room.getTitle() != null) {
		// criteria.add(Restrictions.eq("title", room.getTitle()));
		// }
		// if (room.getCreatedByUserID() != null) {
		// criteria.add(Restrictions.eq("createdBy",
		// room.getCreatedByUserID()));
		// }
		// if (room.getTargetCommunityID() != null) {
		// criteria.add(Restrictions.eq("groupId",
		// room.getTargetCommunityID()));
		// }
		// Query query = criteria.prepareQuery(em);
		// if (pagingParams != null) {
		// query = ApplyPagingParams.apply(query, pagingParams);
		// }
		// List<ChaRooms> retVal = query.getResultList();
		//
		// return convertChaRoomsToRoomDTOA(retVal);

		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param roomProperty
	 *            {@inheritDoc}
	 * @throws QChatIMException
	 *             {@inheritDoc}
	 */
	@Override
	public void setRoomProperty(RoomPropertyDTO roomProperty)
			throws QChatIMException {
		ChaRooms chatRoom = LookupHelper.getRoom(em, roomProperty.getRoomId());
		ChaProperties chaProperty = new ChaProperties(chatRoom,
				roomProperty.getPropertyName(), roomProperty.getPropertyValue());
		em.persist(chaProperty);

		// Post a notification about the event.
		// if (PropertiesLoaderSingleton.getInstance()
		// .getProperty("QlackFuse.ChatIM.realtime.JMS.notifications")
		// .equals("true")) {
		// ChatMessage message = new ChatMessage();
		// message.setType(ChatMessage.MSGTYPE_ROOM_PROPERTY_SET);
		// message.setStringProperty(ChatMessage.PROPERTY__ROOMID,
		// roomProperty.getRoomId());
		// message.setStringProperty(ChatMessage.PROPERTY__ROOM_NAME,
		// chatRoom.getTitle());
		// message.setStringProperty(ChatMessage.PROPERTY__ROOM_PROPERTY_NAME,
		// roomProperty.getPropertyName());
		// message.setStringProperty(
		// ChatMessage.PROPERTY__ROOM_PROPERTY_VALUE,
		// roomProperty.getPropertyValue());
		// try {
		// Messenger.post(connectionFactory, notificationTopic, message);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		// throw new QChatIMException(QChatIMException.CODES.ERR_CHA_0001,
		// ex.getLocalizedMessage());
		// }
		// }
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param roomId
	 *            {@inheritDoc}
	 * @param propertyName
	 *            {@inheritDoc}
	 * @param recipientUserID
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QChatIMException
	 *             {@inheritDoc}
	 */
	@Override
	public RoomPropertyDTO getRoomProperty(String roomId, String propertyName,
			String recipientUserID) throws QChatIMException {
		RoomPropertyDTO dto = null;

		ChaRooms chatRoom = LookupHelper.getRoom(em, roomId);
		Set<ChaProperties> properties = chatRoom.getChaPropertieses();
		if (properties != null && !properties.isEmpty()) {
			for (Iterator iter = properties.iterator(); iter.hasNext();) {
				ChaProperties chaProperty = (ChaProperties) iter.next();
				if (chaProperty.getPropertyName().equals(propertyName)) {
					dto = new RoomPropertyDTO(chaProperty.getId(),
							chaProperty.getPropertyName(),
							chaProperty.getPropertyValue());
				}
			}
		}

			// Post a notification about the event.
//			if (PropertiesLoaderSingleton.getInstance()
//					.getProperty("QlackFuse.ChatIM.realtime.JMS.notifications")
//					.equals("true")) {
//				ChatMessage message = new ChatMessage();
//				message.setType(ChatMessage.MSGTYPE_ROOM_PROPERTY_GET);
//				message.setStringProperty(ChatMessage.PROPERTY__ROOMID, roomId);
//				message.setStringProperty(ChatMessage.PROPERTY__ROOM_NAME,
//						chatRoom.getTitle());
//				message.setStringProperty(
//						ChatMessage.PROPERTY__ROOM_PROPERTY_NAME,
//						dto.getPropertyName());
//				message.setStringProperty(
//						ChatMessage.PROPERTY__ROOM_PROPERTY_VALUE,
//						dto.getPropertyValue());
//				message.setStringProperty(QlackMessage.PRIVATE_USERID,
//						recipientUserID);
//				Messenger.post(connectionFactory, notificationTopic, message);
//			}
//		} catch (JMSException ex) {
//			LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
//			throw new QChatIMException(QChatIMException.CODES.ERR_CHA_0001,
//					ex.getLocalizedMessage());
//		}

		return dto;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param roomId
	 *            {@inheritDoc}
	 * @param recipientUserID
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QChatIMException
	 *             {@inheritDoc}
	 */
	@Override
	public String getRoomStatistics(String roomId, String recipientUserID)
			throws QChatIMException {
		StringWriter xml = new StringWriter();
		try {
			ChaRooms chatRoom = LookupHelper.getRoom(em, roomId);
			RoomStatisticsDTO dto = new RoomStatisticsDTO();
			JAXBContext context;
			context = JAXBContext.newInstance(RoomStatisticsDTO.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			dto.setNumberOfUsers(chatRoom.getChaRoomHasParticipantses().size());
			dto.setNumberOfEntries(chatRoom.getChaRoomHasMessageses().size());
			dto.setTitle(chatRoom.getTitle());
			dto.setCreatedOn(chatRoom.getCreatedOn() + "");
			dto.setCreatedBy(chatRoom.getCreatedBy());
			marshaller.marshal(dto, xml);
			xml.flush();

			// Post a notification about the event.
			// if (PropertiesLoaderSingleton.getInstance()
			// .getProperty("QlackFuse.ChatIM.realtime.JMS.notifications")
			// .equals("true")) {
			// ChatMessage message = new ChatMessage();
			// message.setType(ChatMessage.MSGTYPE_STATISTICS);
			// message.setStringProperty(ChatMessage.PROPERTY__ROOM_NAME,
			// chatRoom.getTitle());
			// message.setStringProperty(ChatMessage.PROPERTY__ROOMID,
			// chatRoom.getId());
			// message.setStringProperty(QlackMessage.PRIVATE_USERID,
			// recipientUserID);
			// message.setBody(xml.toString());
			// Messenger.post(connectionFactory, notificationTopic, message);
			// }
		} catch (JAXBException ex) {
			LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			throw new QChatIMException(ex.getLocalizedMessage());
		}
		// catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		// throw new QChatIMException(QChatIMException.CODES.ERR_CHA_0001,
		// ex.getLocalizedMessage());
		// }

		return xml.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param filter
	 *            {@inheritDoc}
	 * @throws QChatIMException
	 *             {@inheritDoc}
	 */
	@Override
	public void setRoomFilter(RoomWordFilterDTO filter) throws QChatIMException {
		ChaWordFilter chaWordFilter;
		ChaRooms chatRoom = LookupHelper.getRoom(em, filter.getRoomId());
		Query q = em
				.createQuery("select cwf from ChaWordFilter cwf where cwf.roomId = :roomID");
		q.setParameter("roomID", chatRoom);
		List result = q.getResultList();
		if (result.size() > 0) {
			chaWordFilter = (ChaWordFilter) result.get(0);
		} else {
			chaWordFilter = new ChaWordFilter(chatRoom, filter.getFilter());
		}
		chaWordFilter.setFilter(filter.getFilter());
		em.persist(chaWordFilter);

		// Post a notification about the event.
		// if (PropertiesLoaderSingleton.getInstance()
		// .getProperty("QlackFuse.ChatIM.realtime.JMS.notifications")
		// .equals("true")) {
		// ChatMessage message = new ChatMessage();
		// message.setType(ChatMessage.MSGTYPE_ROOM_FILTER_SET);
		// message.setStringProperty(ChatMessage.PROPERTY__ROOMID,
		// filter.getRoomId());
		// message.setStringProperty(ChatMessage.PROPERTY__ROOM_FILTER_VALUE,
		// filter.getFilter());
		// try {
		// Messenger.post(connectionFactory, notificationTopic, message);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		// throw new QChatIMException(QChatIMException.CODES.ERR_CHA_0001,
		// ex.getLocalizedMessage());
		// }
		// }
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param roomId
	 *            {@inheritDoc}
	 * @param recipientUserID
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QChatIMException
	 *             {@inheritDoc}
	 */
	@Override
	public RoomWordFilterDTO getRoomFilter(String roomId, String recipientUserID)
			throws QChatIMException {
		RoomWordFilterDTO retVal = new RoomWordFilterDTO();
		retVal.setRoomId(roomId);

		Query q = em
				.createQuery("select cwf from ChaWordFilter cwf where cwf.roomId.id = :roomId");
		q.setParameter("roomId", roomId);

		Object result = q.getSingleResult();
		if (result != null) {
			retVal.setFilter(((ChaWordFilter) result).getFilter());
		}

		// Post a notification about the event.
		// if (PropertiesLoaderSingleton.getInstance()
		// .getProperty("QlackFuse.ChatIM.realtime.JMS.notifications")
		// .equals("true")) {
		// ChatMessage message = new ChatMessage();
		// message.setType(ChatMessage.MSGTYPE_ROOM_FILTER_GET);
		// message.setStringProperty(ChatMessage.PROPERTY__ROOMID, roomId);
		// message.setStringProperty(ChatMessage.PROPERTY__ROOM_FILTER_VALUE,
		// retVal.getFilter());
		// message.setStringProperty(QlackMessage.PRIVATE_USERID,
		// recipientUserID);
		// try {
		// Messenger.post(connectionFactory, notificationTopic, message);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		// throw new QChatIMException(QChatIMException.CODES.ERR_CHA_0001,
		// ex.getLocalizedMessage());
		// }
		// }

		return retVal;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param roomId
	 *            {@inheritDoc}
	 * @throws QChatIMException
	 *             {@inheritDoc}
	 */
	@Override
	public void removeRoomFilter(String roomId) throws QChatIMException {
		// TODO since we have a single filter for each room, replace this code
		// with a more efficient version.
		ChaRooms chatRoom = LookupHelper.getRoom(em, roomId);
		Set<ChaWordFilter> filters = chatRoom.getChaWordFilters();
		Iterator<ChaWordFilter> itr = filters.iterator();
		while (itr.hasNext()) {
			ChaWordFilter cf = itr.next();
			em.remove(cf);
		}

		// Post a notification about the event.
		// if (PropertiesLoaderSingleton.getInstance()
		// .getProperty("QlackFuse.ChatIM.realtime.JMS.notifications")
		// .equals("true")) {
		// ChatMessage message = new ChatMessage();
		// message.setType(ChatMessage.MSGTYPE_ROOM_FILTER_DELETED);
		// message.setStringProperty(ChatMessage.PROPERTY__ROOMID,
		// chatRoom.getId());
		// message.setStringProperty(ChatMessage.PROPERTY__ROOM_NAME,
		// chatRoom.getTitle());
		// try {
		// Messenger.post(connectionFactory, notificationTopic, message);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		// throw new QChatIMException(QChatIMException.CODES.ERR_CHA_0001,
		// ex.getLocalizedMessage());
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
	 * @return {@inheritDoc}
	 */
	@Override
	public Long getRoomJoiningTimeForUser(String roomID, String userID) {
		//TODO Kaskoura
		Long retVal = null;
		// Criteria criteria = CriteriaFactory
		// .createCriteria("ChaRoomHasParticipants");
		// criteria.createAlias("roomId", "room");
		// criteria.add(Restrictions.eq("userId", userID));
		// criteria.add(Restrictions.eq("room.id", roomID));
		// Query q = criteria.prepareQuery(em);
		// List<ChaRoomHasParticipants> l = q.getResultList();
		// if ((l != null) && (!l.isEmpty())) {
		// retVal = Long.valueOf(l.get(0).getJoinedOn());
		// }

		return retVal;
	}

}
