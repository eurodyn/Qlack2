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
package com.eurodyn.qlack2.fuse.forum.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.forum.api.MessageService;
import com.eurodyn.qlack2.fuse.forum.api.dto.AttachmentDTO;
import com.eurodyn.qlack2.fuse.forum.api.dto.ForumConstants;
import com.eurodyn.qlack2.fuse.forum.api.dto.MessageDTO;
import com.eurodyn.qlack2.fuse.forum.api.exception.QForumException;
import com.eurodyn.qlack2.fuse.forum.api.exception.QMessageNotFound;
import com.eurodyn.qlack2.fuse.forum.api.exception.QOperationNotSupported;
import com.eurodyn.qlack2.fuse.forum.api.exception.QRequiredPropertyValueMissing;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmAttachment;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmForum;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmMessage;
import com.eurodyn.qlack2.fuse.forum.impl.model.QFrmMessage;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmTopic;
import com.eurodyn.qlack2.fuse.forum.impl.util.ConverterUtil;
import com.eurodyn.qlack2.fuse.forum.impl.util.LookupHelper;
import com.querydsl.jpa.impl.AbstractJPAQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * A Stateless Session EJB providing services to manage a topic messages. For
 * details regarding the functionality offered see the respective interfaces.
 *
 * @author European Dynamics SA
 */
@Transactional
public class MessageServiceImpl implements MessageService {
	// The class LOGGER.
	public static final Logger LOGGER = Logger
			.getLogger(MessageServiceImpl.class.getName());
	// The persistence context for database operations.
	@PersistenceContext(unitName = "fuse-forum")
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
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public MessageDTO postMessage(MessageDTO messageDTO) throws QForumException {
		LOGGER.log(Level.FINEST, "Creating new message for topic with id {0}",
				messageDTO.getTopicId());
		validateMessageTextForNotNull(messageDTO.getText().trim());
		FrmTopic topic = validateForumAndTopic(messageDTO.getTopicId());
		messageDTO.setCreatedOn(System.currentTimeMillis());

		if (topic.isModerated()) {
			messageDTO
					.setModerationStatus(ForumConstants.MODERATION_STATUS_PENDING);
		} else {
			messageDTO
					.setModerationStatus(ForumConstants.MODERATION_STATUS_ACCEPTED);
		}

		FrmMessage messageEntity = ConverterUtil
				.convert2MessageModel(messageDTO);
		messageEntity.setFrmTopicId(topic);
		em.persist(messageEntity);
		messageDTO.setId(messageEntity.getId());
		if (messageDTO.getAttachment() != null) {
			AttachmentDTO attachmentDTO = messageDTO.getAttachment();
			FrmAttachment atachment = ConverterUtil
					.convert2AttachmentModel(attachmentDTO);

			if (atachment != null) {
				atachment.setFrmMessageId(messageEntity);
				em.persist(atachment);
			}
		}

		// Post a notification about the event.
		// if
		// (PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.Forum.realtime.JMS.notifications").equals("true"))
		// {
		// ForumMessage jmsMessage = new ForumMessage();
		// jmsMessage.setType(ForumMessage.MSGTYPE__POST_MESSAGE);
		// jmsMessage.setSrcUserID(messageDTO.getCreatorId());
		// jmsMessage.setStringProperty(ForumMessage.PRIVATE_USERID,
		// topic.getCreatedBy());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__FORUM_ID,
		// topic.getFrmForumId().getId());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__FORUM_TITLE,
		// topic.getFrmForumId().getTitle());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__TOPIC_TITLE,
		// topic.getTitle());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__TOPIC_ID,
		// messageDTO.getTopicId());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__MESSAGE_ID,
		// messageDTO.getId());
		// try {
		// Messenger.post(connectionFactory, notificationTopic, jmsMessage);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage());
		// throw new QForumException(CODES.ERR_FORUM_0009,
		// ex.getLocalizedMessage());
		// }
		// }

		return messageDTO;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param messageId
	 *            {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteMessage(String messageId) throws QForumException {
		LOGGER.log(Level.FINEST, "Deleting message with id {0}", messageId);

		FrmMessage messageEntity = LookupHelper.retrieveMessage(messageId, em);
		if (messageEntity == null) {
			throw new QMessageNotFound("Message with id " + messageId
					+ " doesn't exist. It cannot be deleted.");
		}
		// Check message is not a root message
		if (isRootMessage(messageEntity.getFrmTopicId().getId(), messageId)) {
			throw new QMessageNotFound("Message with id " + messageId
					+ " is a root message. It can't be deleted.");
		}

		validateForumAndTopic(messageEntity.getFrmTopicId().getId());

		// remove message attachments
		Set<FrmAttachment> attachments = messageEntity.getFrmAttachments();
		if (attachments != null && attachments.size() > 0) {
			deleteAttachments(attachments);
		}

		em.remove(messageEntity);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param messageDTO
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public MessageDTO updateMessage(MessageDTO messageDTO)
			throws QForumException {
		LOGGER.log(Level.FINEST,
				"Updating message in a topic with message id {0}",
				messageDTO.getId());

		validateForumAndTopic(messageDTO.getTopicId());
		String newMessageText = messageDTO.getText();
		validateMessageTextForNotNull(newMessageText);
		FrmMessage messageEntity = LookupHelper.retrieveMessage(
				messageDTO.getId(), em);
		messageEntity.setText(newMessageText);
		em.merge(messageEntity);

		AttachmentDTO attachmentDTO = messageDTO.getAttachment();
		if (attachmentDTO != null) {
			updateAttachment(attachmentDTO, messageEntity);
		}

		return ConverterUtil.convert2MessageDTO(messageEntity);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param messageId
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public MessageDTO getMessageById(String messageId) throws QForumException {
		FrmMessage messageEntity = LookupHelper.retrieveMessage(messageId, em);
		if (messageEntity == null) {
			return null;
		}

		return ConverterUtil.convert2MessageDTO(messageEntity);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param topicId
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public List<MessageDTO> listMessages(String topicId) throws QForumException {
		LOGGER.log(Level.FINEST,
				"Getting list of messages in a topic with id {0}", topicId);
		return listMessages(topicId, null);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param topicId
	 *            {@inheritDoc}
	 * @param pagingParams
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public List<MessageDTO> listMessages(String topicId,
			PagingParams pagingParams) throws QForumException {
		LOGGER.log(Level.FINEST,
				"Getting list of messages with pagingParams in "
						+ "a topic with id ", topicId);

		List<MessageDTO> messagecDtoList = new ArrayList();
		List<FrmMessage> messageEntityList = new ArrayList();

		Query query = em.createQuery("select object(o) from FrmMessage as o "
				+ "where o.frmTopicId.id =:topic_id ORDER BY o.createdOn DESC");
		query.setParameter("topic_id", topicId);
		if ((pagingParams != null) && (pagingParams.getCurrentPage() > -1)) {
			query.setFirstResult((pagingParams.getCurrentPage() - 1)
					* pagingParams.getPageSize());
			query.setMaxResults(pagingParams.getPageSize());
		}
		messageEntityList = query.getResultList();
		for (FrmMessage model : messageEntityList) {
			messagecDtoList.add(ConverterUtil.convert2MessageDTO(model));
		}

		return messagecDtoList;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param messageId
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public short getMessageModerationStatus(String messageId)
			throws QForumException {
		LOGGER.log(Level.FINEST,
				"Retrieving the moderation status of message with id {0}",
				messageId);

		FrmMessage messageEntity = LookupHelper.retrieveMessage(messageId, em);
		if (messageEntity == null) {
			throw new QMessageNotFound("Message with id " + messageId
					+ "does not exist in the system.");
		}

		return messageEntity.getModerationStatus();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param attachmentId
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public AttachmentDTO getAttachment(String attachmentId)
			throws QForumException {
		LOGGER.log(Level.FINEST,
				"Getting the details of the attachment with id {0}",
				attachmentId);
		FrmAttachment attachment = null;

		attachment = em.find(FrmAttachment.class, attachmentId);
		if (attachment == null) {
			return null;
		}
		return ConverterUtil.convert2AttachmentDTO(attachment);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param messageId
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public boolean acceptMessage(String messageId) throws QForumException {
		boolean retVal = false;

		FrmMessage messageEntity = LookupHelper.retrieveMessage(messageId, em);
		if (messageEntity != null) {
			if (messageEntity.getModerationStatus() == ForumConstants.MODERATION_STATUS_PENDING) {

				if ((messageEntity.getFrmTopicId().getModerationStatus() == ForumConstants.MODERATION_STATUS_PENDING)
						|| (messageEntity.getFrmTopicId().getModerationStatus() == ForumConstants.MODERATION_STATUS_REJECTED)) {
					throw new QRequiredPropertyValueMissing("Topic '"
							+ messageEntity.getFrmTopicId().getId()
							+ "' is not approved.");
				}

				messageEntity
						.setModerationStatus(ForumConstants.MODERATION_STATUS_ACCEPTED);
				em.merge(messageEntity);
				// Post a notification about the event.
				// if
				// (PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.Forum.realtime.JMS.notifications").equals("true"))
				// {
				// ForumMessage jmsMessage = new ForumMessage();
				// jmsMessage.setType(ForumMessage.MSGTYPE__POST_MESSAGE);
				// jmsMessage.setSrcUserID(messageEntity.getCreatedBy());
				// jmsMessage.setStringProperty(ForumMessage.PRIVATE_USERID,
				// messageEntity.getCreatedBy());
				// jmsMessage.setStringProperty(ForumMessage.PROPERTY__TOPIC_TITLE,
				// messageEntity.getFrmTopicId().getTitle());
				// jmsMessage.setStringProperty(ForumMessage.PROPERTY__TOPIC_ID,
				// messageEntity.getFrmTopicId().getId());
				// jmsMessage.setStringProperty(ForumMessage.PROPERTY__MESSAGE_ID,
				// messageEntity.getId());
				// try {
				// Messenger.post(connectionFactory, notificationTopic,
				// jmsMessage);
				// } catch (JMSException ex) {
				// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage());
				// throw new QForumException(CODES.ERR_FORUM_0009,
				// ex.getLocalizedMessage());
				// }
				// }
				retVal = true;
			}
		}

		return retVal;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param messageId
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public boolean rejectMessage(String messageId) throws QForumException {
		boolean retVal = false;

		FrmMessage messageEntity = LookupHelper.retrieveMessage(messageId, em);
		if (messageEntity != null) {
			if (messageEntity.getModerationStatus() == ForumConstants.MODERATION_STATUS_PENDING) {
				messageEntity
						.setModerationStatus(ForumConstants.MODERATION_STATUS_REJECTED);
				em.merge(messageEntity);
				// Post a notification about the event.
				// if
				// (PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.Forum.realtime.JMS.notifications").equals("true"))
				// {
				// ForumMessage jmsMessage = new ForumMessage();
				// jmsMessage.setType(ForumMessage.MSGTYPE__REJECT_MESSAGE);
				// jmsMessage.setSrcUserID(messageEntity.getCreatedBy());
				// jmsMessage.setStringProperty(ForumMessage.PRIVATE_USERID,
				// messageEntity.getCreatedBy());
				// jmsMessage.setStringProperty(ForumMessage.PROPERTY__TOPIC_TITLE,
				// messageEntity.getFrmTopicId().getTitle());
				// jmsMessage.setStringProperty(ForumMessage.PROPERTY__TOPIC_ID,
				// messageEntity.getFrmTopicId().getId());
				// jmsMessage.setStringProperty(ForumMessage.PROPERTY__MESSAGE_ID,
				// messageEntity.getId());
				// try {
				// Messenger.post(connectionFactory, notificationTopic,
				// jmsMessage);
				// } catch (JMSException ex) {
				// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage());
				// throw new QForumException(CODES.ERR_FORUM_0009,
				// ex.getLocalizedMessage());
				// }
				// }
				retVal = true;
			}
		}

		return retVal;
	}

	/**
	 * Checks whether a message is the first message on a topic.
	 *
	 * @param topicId
	 *            The ID of the topic for which the check takes place.
	 * @param messageId
	 *            The ID of the message to be checked.
	 * @return true, if the message is the root message, false otherwise.
	 */
	private boolean isRootMessage(String topicId, String messageId) {
		return (messageId.equalsIgnoreCase(getRootMessage(topicId)));
	}

	/**
	 * Returns the text of the first message of a topic.
	 *
	 * @param topicId
	 *            The ID of the topic who's first message is going to be
	 *            returned.
	 * @return The text of the first message of the topic or null if such a
	 *         message does not exist.
	 */
	private String getRootMessage(String topicId) {
		Query query = em
				.createQuery("select object(o) from FrmMessage as o "
						+ "where o.createdOn in (select min(m.createdOn) from FrmMessage as m where m.frmTopicId.id=:topic_id)");
		query.setParameter("topic_id", topicId);
		FrmMessage message = (FrmMessage) query.getSingleResult();
		if (message.getId() != null) {
			return message.getId();
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param topicId
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public String getLatestMessageId(String topicId) throws QForumException {
		String retVal = null;

		Query query = em
				.createQuery("select m from FrmMessage m "
						+ " where m.frmTopicId = :topic "
						+ "order by m.createdOn DESC");
		query.setParameter("topic", LookupHelper.retrieveTopic(topicId, em));
		query.setMaxResults(1);
		List<FrmMessage> l = query.getResultList();
		if (l != null && !l.isEmpty()) {
			retVal = l.get(0).getId();
		}

		return retVal;
	}

	/**
	 * Checks that a post, delete & update operation is supported for a
	 * particular topic.
	 *
	 * @param topicId
	 *            The ID of the topic to be checked.
	 * @return The entity representation of the underlying topic.
	 * @throws QForumException
	 *             If any of post, delete & update can not be supported due to
	 *             the topic being locked, the forum being locked or the forum
	 *             is archived.
	 */
	private FrmTopic validateForumAndTopic(String topicId)
			throws QForumException {
		FrmTopic topic = LookupHelper.retrieveTopic(topicId, em);

		if (topic.getStatus() == ForumConstants.TOPIC_STATUS_LOCKED) {
			throw new QOperationNotSupported("Topic with id " + topicId
					+ " is disabled. The operation cannot be supported.");
		}
		if (topic.isArchived()) {
			throw new QOperationNotSupported("Topic with id " + topicId
					+ " is archived. The operation cannot be supported.");
		}
		FrmForum forum = topic.getFrmForumId();
		if (forum.getStatus() == ForumConstants.FORUM_STATUS_LOCKED) {
			throw new QOperationNotSupported("Forum with id " + forum.getId()
					+ " is disabled. The operation cannot be supported.");
		}
		if (forum.isArchived()) {
			throw new QOperationNotSupported("Forum with id " + forum.getId()
					+ " is archived. The operation cannot be supported.");
		}

		return topic;
	}

	/**
	 * Checks that the text of a message is not null.
	 *
	 * @param text
	 *            The text to be checked.
	 * @throws QForumException
	 *             If the text passed in is null.
	 */
	private void validateMessageTextForNotNull(String text)
			throws QForumException {
		if (text == null) {
			throw new QRequiredPropertyValueMissing(
					"Message text should not be null for posting the message.");
		}
	}

	/**
	 * Deletes a set of attachments.
	 *
	 * @param attachments
	 *            The attachments to be deleted.
	 */
	private void deleteAttachments(Set<FrmAttachment> attachments) {
		for (FrmAttachment attachment : attachments) {
			if (attachment != null) {
				em.remove(attachment);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param topicId
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public String getTopicRootMessage(String topicId) {
		String retVal = null;

		Query query = em.createQuery("select m from FrmMessage m "
				+ " where m.frmTopicId.id = :topic_id) "
				+ "order by m.createdOn DESC");
		query.setParameter("topic_id", topicId);
		query.setMaxResults(1);
		List l = query.getResultList();
		if (l != null && !l.isEmpty()) {
			FrmMessage message = (FrmMessage) l.get(0);
			if (message.getText() != null) {
				retVal = message.getText();
			}
		}

		return retVal;
	}

	/**
	 * Updates an existing attachment.
	 *
	 * @param attachmentDTO
	 *            The DTO holding the information to be updated.
	 * @param messageEntity
	 *            The message entity who's attachments are to be updated.
	 */
	private void updateAttachment(AttachmentDTO attachmentDTO,
			FrmMessage messageEntity) {
		LOGGER.log(Level.FINEST, "Updating message attachment.");
		deleteAttachments(messageEntity.getId());
		FrmAttachment attachment = ConverterUtil
				.convert2AttachmentModel(attachmentDTO);

		if (attachment != null) {
			attachment.setFrmMessageId(messageEntity);
			em.persist(attachment);
		}
	}

	/**
	 * Deletes all attachments of a particular message.
	 *
	 * @param messageId
	 *            The message ID who's attachments are to be deleted.
	 */
	private void deleteAttachments(String messageId) {
		if (hasAttachmentEntity(messageId)) {
			Query query = em
					.createQuery("delete from FrmAttachment as o where o.frmMessageId.id=:message_id");
			query.setParameter("message_id", messageId);
			query.executeUpdate();
		}
	}

	/**
	 * Checks whether a message has any attachments.
	 *
	 * @param messageId
	 *            The ID of the messages to be checked.
	 * @return true, if the message has >0 attachments, false otherwise.
	 */
	private boolean hasAttachmentEntity(String messageId) {
		Query query = em
				.createQuery("select m.id from FrmAttachment as m where m.frmMessageId.id=:message_id");
		query.setParameter("message_id", messageId);
		if (query.getResultList() != null)
			return true;
		else
			return false;
	}

	
	/**
	 * This method returns the total amount of logged in user's forum posts.
	 *
	 * @param userID
	 *            User ID
	 * @return long of total amount of logged in user's forum posts.
	 * @throws QForumException
	 *             Throws exception if provided userID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public long getAllMessages(String userID) throws QForumException {
		QFrmMessage qfrmMessage = QFrmMessage.frmMessage;
		long  frmPostsNum = new JPAQueryFactory(em).selectFrom(qfrmMessage).
				where(qfrmMessage.createdBy.eq(userID)).fetchCount();
		return frmPostsNum;
	}
	
}
