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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.fuse.chatim.api.IMMessageService;
import com.eurodyn.qlack2.fuse.chatim.api.dto.MessageDTO;
import com.eurodyn.qlack2.fuse.chatim.api.exception.QChatIMException;
import com.eurodyn.qlack2.fuse.chatim.impl.model.ChaIm;
import com.eurodyn.qlack2.fuse.chatim.impl.util.IMessage;

/**
 *
 * @author European Dynamics SA
 */
@Transactional
public class IMMessageServiceImpl implements IMMessageService {
	public static final Logger LOGGER = Logger
			.getLogger(IMMessageServiceImpl.class.getName());
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
	public String sendMessage(MessageDTO messageDTO)
			throws QChatIMException {
		ChaIm im = new ChaIm();
		im.setFromUserId(messageDTO.getFromID());
		im.setToUserId(messageDTO.getToID());
		im.setMessage(messageDTO.getMessage());
		im.setSentOn(System.currentTimeMillis());
		em.persist(im);

		// Post a notification about the event.
//		if (PropertiesLoaderSingleton.getInstance()
//				.getProperty("QlackFuse.ChatIM.realtime.JMS.notifications")
//				.equals("true")) {
//			IMessage iMessage = new IMessage();
//			iMessage.setType(IMessage.MSGTYPE_INCOMING_IM);
//			iMessage.setStringProperty(IMessage.PROPERTY__SRC_USERID,
//					messageDTO.getFromID());
//			iMessage.setStringProperty(IMessage.PRIVATE_USERID,
//					messageDTO.getToID());
//			iMessage.setBody(messageDTO.getMessage());
//			try {
//				Messenger.post(connectionFactory, notificationTopic, iMessage);
//			} catch (JMSException ex) {
//				LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
//				throw new QChatIMException(
//						QChatIMException.CODES.ERR_CHA_0001,
//						ex.getMessage());
//			}
//		}

		return im.getId();
	}

}