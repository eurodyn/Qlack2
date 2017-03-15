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

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.fuse.chatim.api.MessageService;
import com.eurodyn.qlack2.fuse.chatim.api.dto.MessageDTO;
import com.eurodyn.qlack2.fuse.chatim.api.exception.QChatIMException;
import com.eurodyn.qlack2.fuse.chatim.impl.model.ChaRoomHasMessages;
import com.eurodyn.qlack2.fuse.chatim.impl.model.ChaRooms;
import com.eurodyn.qlack2.fuse.chatim.impl.model.ChaWordFilter;
import com.eurodyn.qlack2.fuse.chatim.impl.util.LookupHelper;

/**
 *
 * @author European Dynamics SA
 */
@Transactional
public class MessageServiceImpl implements MessageService {
	public static final Logger LOGGER = Logger
			.getLogger(MessageServiceImpl.class.getName());
	@PersistenceContext(unitName = "fuse-chatim")
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param messageDTO
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QChatIMException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public String sendMessage(MessageDTO messageDTO) throws QChatIMException {
		LOGGER.log(Level.FINEST, "Posting message on room {0}.",
				messageDTO.getRoomID());
		String retVal = messageDTO.getMessage();
		ChaRooms room = (ChaRooms) em.find(ChaRooms.class,
				messageDTO.getRoomID());

		// Check if a filter needs to be applied to this messages.
		// TODO caching...
		String message = messageDTO.getMessage();
		Set<ChaWordFilter> filter = room.getChaWordFilters();
		if (!filter.isEmpty()) {
			String[] filters = filter.iterator().next().getFilter().split(",");
			for (String i : filters) {
				message = message.replaceAll(i, "****");
			}
		}

		ChaRoomHasMessages crhm = new ChaRoomHasMessages();
		crhm.setMessage(message);
		crhm.setRoomId(LookupHelper.getRoom(em, messageDTO.getRoomID()));
		crhm.setSentOn(messageDTO.getDate());
		crhm.setUserId(messageDTO.getFromID());

		em.persist(crhm);

		// Post a notification about the event.
		// if
		// (PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.ChatIM.realtime.JMS.notifications").equals("true"))
		// {
		// ChatMessage chatMessage = new ChatMessage();
		// chatMessage.setType(ChatMessage.MSGTYPE_ROOM_MESSAGE_POSTED);
		// chatMessage.setStringProperty(ChatMessage.PROPERTY__ROOM_NAME,
		// room.getTitle());
		// chatMessage.setStringProperty(ChatMessage.PROPERTY__ROOMID,
		// room.getId());
		// chatMessage.setStringProperty(ChatMessage.PROPERTY__SRC_USERID,
		// messageDTO.getFromID());
		// chatMessage.setBody(message);
		// try {
		// Messenger.post(connectionFactory, notificationTopic, chatMessage);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		// throw new QChatIMException(QChatIMException.CODES.ERR_CHA_0001,
		// ex.getMessage());
		// }
		// }

		return retVal;
	}

}
