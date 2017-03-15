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
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.commons.lang3.StringUtils;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.forum.api.TopicService;
import com.eurodyn.qlack2.fuse.forum.api.dto.AttachmentDTO;
import com.eurodyn.qlack2.fuse.forum.api.dto.ForumConstants;
import com.eurodyn.qlack2.fuse.forum.api.dto.TopicDTO;
import com.eurodyn.qlack2.fuse.forum.api.exception.QForumException;
import com.eurodyn.qlack2.fuse.forum.api.exception.QInvalidModeration;
import com.eurodyn.qlack2.fuse.forum.api.exception.QOperationNotSupported;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmAttachment;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmForum;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmMessage;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmTopic;
import com.eurodyn.qlack2.fuse.forum.impl.util.ConverterUtil;
import com.eurodyn.qlack2.fuse.forum.impl.util.LookupHelper;

/**
 * A Stateless Session EJB providing services to manage a forum topic.
 *
 * @author European Dynamics SA
 */
@Transactional
public class TopicServiceImpl implements TopicService {
	// The class LOGGER.
	public static final Logger LOGGER = Logger.getLogger(TopicServiceImpl.class
			.getName());

	// The persistence context for database operation.
	@PersistenceContext(unitName = "fuse-forum")
	private EntityManager em;
	
	public void setEm(EntityManager em) {
		this.em = em;
	}
	/**
	 * Helper method to check if a newly created topic (or an update to an
	 * existing one) conforms to the moderation logic: a) A moderated forum can
	 * not have non-moderated topics. b) A non-moderated forum can not have
	 * moderated topics. c) A moderated topic can not be switched to a
	 * non-moderated one if it has messages still pending moderation.
	 *
	 * @param topicId
	 *            The topic ID to be checked.
	 * @param forumModerated
	 *            The moderation status of the forum.
	 * @param topicModerated
	 *            The requested moderation status of the topic.
	 * @throws QForumException
	 *             If the topic can not be switched to the requested moderation
	 *             status.
	 */
	private void checkModeratedProperty(String topicId, short forumModerated,
			boolean topicModerated) throws QForumException {
		switch (forumModerated) {
		case ForumConstants.FORUM_MODERATED:
			if (!topicModerated) {
				throw new QInvalidModeration(
						"The topic's"
								+ "moderated property cannot be set to false; the parent forum"
								+ " is moderated.");
			}
			break;
		case ForumConstants.FORUM_NOT_MODERATED:
			if (topicModerated) {
				throw new QInvalidModeration("The topic's"
						+ "moderated property cannot be set; the parent forum "
						+ "is not moderated.");
			}
			break;
		case ForumConstants.FORUM_SUPPORTS_MODERATION:
			// Before we allow a topic to be switched to a non-moderated one, we
			// check
			// whether there are pending messages to be approved or not.
			Query pendingMessagesQuery = em
					.createQuery("SELECT COUNT(m) FROM FrmMessage m "
							+ "WHERE m.frmTopicId.id = :topicId and m.moderationStatus = :status");
			pendingMessagesQuery.setParameter("topicId", topicId);
			pendingMessagesQuery.setParameter("status",
					ForumConstants.MODERATION_STATUS_PENDING);
			Long pendingMessages = (Long) pendingMessagesQuery
					.getSingleResult();
			if ((!topicModerated) && (pendingMessages > 0)) {
				throw new QInvalidModeration(
						"The topic's moderated "
								+ "property cannot  be set to false; the topic has pending messages.");
			}
		}
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public TopicDTO createTopic(TopicDTO topic, String messageText)
			throws QForumException {
		if (messageText == null) {
			throw new NullPointerException(
					"Mandatory parameter messageText is null");
		}
		FrmForum forum = LookupHelper.retrieveForum(topic.getForumId(), em);
		LookupHelper.checkForumStatus(forum.getStatus());
		LookupHelper.checkForumArchived(forum.isArchived());

		// Check if topic title is unique
		// Query q = em.createQuery("SELECT t FROM FrmTopic t "
		// + "WHERE t.frmForumId = :forum AND t.title = :title");
		// q.setParameter("forum", forum);
		// q.setParameter("title", topic.getTitle());
		// List resultList = q.getResultList();
		// if (!resultList.isEmpty()) {
		// throw new QForumException(CODES.ERR_FORUM_0006,
		// "A topic with title '"
		// + topic.getTitle() + "' already exists in the forum with id '" +
		// topic.getForumId() + "'");
		// }

		// Enforce moderation logic.
		if (forum.getModerated() == ForumConstants.FORUM_MODERATED) {
			topic.setModerated(true);
			topic.setModerationStatus(ForumConstants.MODERATION_STATUS_PENDING);
		} else if (forum.getModerated() == ForumConstants.FORUM_NOT_MODERATED) {
			topic.setModerated(false);
			topic.setModerationStatus(ForumConstants.MODERATION_STATUS_ACCEPTED);
		} else { // ForumConstants.FORUM_SUPPORTS_MODERATION
			if (topic.getModerated() == null) {
				topic.setModerated(false);
				topic.setModerationStatus(ForumConstants.MODERATION_STATUS_ACCEPTED);
			} else {
				topic.setModerated(topic.getModerated().booleanValue());
				if (topic.getModerated().booleanValue()) {
					topic.setModerationStatus(ForumConstants.MODERATION_STATUS_PENDING);
				} else {
					topic.setModerationStatus(ForumConstants.MODERATION_STATUS_ACCEPTED);
				}
			}
		}

		// Create topic
		topic.setCreatedOn(System.currentTimeMillis());
		topic.setStatus(ForumConstants.TOPIC_STATUS_UNLOCKED);
		topic.setArchived(false);
		FrmTopic topicEntity = ConverterUtil.convert2TopicModel(topic);
		topicEntity.setFrmForumId(forum);
		em.persist(topicEntity);

		// Create the first message for this topic.
		if (StringUtils.isNotEmpty(messageText)) {
			FrmMessage message = new FrmMessage();
			message.setCreatedBy(topic.getCreatorId());
			message.setCreatedOn(topic.getCreatedOn());
			message.setText(messageText);
			message.setModerationStatus(topic.getModerated() ? ForumConstants.MODERATION_STATUS_PENDING
					: ForumConstants.MODERATION_STATUS_ACCEPTED);
			message.setFrmTopicId(topicEntity);
			em.persist(message);
		}

		topic.setId(topicEntity.getId());

		// Post a notification about the event.
		// if
		// (PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.Forum.realtime.JMS.notifications").equals("true"))
		// {
		// ForumMessage jmsMessage = new ForumMessage();
		// jmsMessage.setType(ForumMessage.MSGTYPE__TOPIC_CREATED);
		// jmsMessage.setSrcUserID(topic.getCreatorId());
		// jmsMessage.setStringProperty(ForumMessage.PRIVATE_USERID,
		// topic.getCreatorId());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__FORUM_ID,
		// forum.getId());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__FORUM_TITLE,
		// forum.getTitle());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__TOPIC_TITLE,
		// topic.getTitle());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__TOPIC_ID,
		// topic.getId());
		// try {
		// Messenger.post(connectionFactory, notificationTopic, jmsMessage);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage());
		// throw new QForumException(CODES.ERR_FORUM_0009,
		// ex.getLocalizedMessage());
		// }
		// }

		return topic;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param topic
	 *            {@inheritDoc}
	 * @param messageText
	 *            {@inheritDoc}
	 * @param messageAttachments
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public TopicDTO createTopic(TopicDTO topic, String messageText,
			Set<AttachmentDTO> messageAttachments) throws QForumException {
		if (messageAttachments == null) {
			throw new NullPointerException(
					"Mandatory parameter messageAttachments is null");
		}

		topic = createTopic(topic, messageText);

		Query query = em
				.createQuery("SELECT m FROM FrmMessage m WHERE m.frmTopicId.id = :topicId");
		query.setParameter("topicId", topic.getId());
		FrmMessage message = (FrmMessage) query.getSingleResult();
		for (AttachmentDTO attachment : messageAttachments) {
			attachment.setMessageId(message.getId());
			FrmAttachment attEntity = ConverterUtil
					.convert2AttachmentModel(attachment);
			attEntity.setFrmMessageId(message);
			em.persist(attEntity);
		}

		return topic;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void updateTopic(TopicDTO topic) throws QForumException {
		FrmTopic topicEntity = LookupHelper.retrieveTopic(topic.getId(), em);

		LookupHelper.checkTopicStatus(topicEntity.getStatus());
		LookupHelper.checkTopicArchived(topicEntity.isArchived());
		FrmForum forum = topicEntity.getFrmForumId();
		LookupHelper.checkForumStatus(forum.getStatus());
		LookupHelper.checkForumArchived(forum.isArchived());

		if (topic.getTitle() != null) {
			topicEntity.setTitle(topic.getTitle());
		}
		if (topic.getDescription() != null) {
			topicEntity.setDescription(topic.getDescription());
		}
		if (topic.getLogo() != null) {
			topicEntity.setLogo(topic.getLogo());
		}
		if (topic.getModerated() != null) {
			checkModeratedProperty(topic.getId(), forum.getModerated(),
					topic.getModerated());
			topicEntity.setModerated(topic.getModerated());
		}
		topicEntity.setArchived(topic.isArchived());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param topicId
	 *            {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteTopic(String topicId) throws QForumException {
		FrmTopic topicEntity = LookupHelper.retrieveTopic(topicId, em);
		LookupHelper.checkForumStatus(topicEntity.getFrmForumId().getStatus());
		LookupHelper.checkForumArchived(topicEntity.getFrmForumId()
				.isArchived());

		em.remove(topicEntity);
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
	public TopicDTO getTopicById(String topicId) throws QForumException {
		TopicDTO topic = null;
		FrmTopic topicEntity = em.find(FrmTopic.class, topicId);
		if (topicEntity == null) {
			return null;
		}
		topic = ConverterUtil.convert2TopicDTO(topicEntity);
		topic = getMessageInfo(topic);

		return topic;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param forumId
	 *            {@inheritDoc}
	 * @param includeDisabled
	 *            {@inheritDoc}
	 * @param archived
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public List<TopicDTO> listTopics(String forumId, boolean includeDisabled,
			Boolean archived, QLACK_FORUM_TOPICS_DATE_ORDER ordering)
			throws QForumException {
		return listTopics(forumId, includeDisabled, archived, null, ordering);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param forumId
	 *            {@inheritDoc}
	 * @param includeDisabled
	 *            {@inheritDoc}
	 * @param archived
	 *            {@inheritDoc}
	 * @param pagingParams
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public List<TopicDTO> listTopics(String forumId, boolean includeDisabled,
			Boolean archived, PagingParams pagingParams,
			QLACK_FORUM_TOPICS_DATE_ORDER ordering) throws QForumException {
		List<TopicDTO> resultList = new ArrayList();
		// TODO Kaskoura
		// Criteria criteria = CriteriaFactory.createCriteria("FrmTopic");
		// criteria.createAlias("frmForumId", "forum");
		// criteria.add(Restrictions.eq("forum.id", forumId));
		// if (!includeDisabled) {
		// criteria.add(Restrictions.ne("status",
		// ForumConstants.TOPIC_STATUS_LOCKED));
		// }
		// if (archived != null) {
		// criteria.add(Restrictions.eq("archived", archived));
		// }
		// switch (ordering) {
		// case ASCEDING:
		// criteria.addOrder(Order.ascending("createdOn"));
		// break;
		// case DESCENDING:
		// criteria.addOrder(Order.descending("createdOn"));
		// }
		// Query query = criteria.prepareQuery(em);
		//
		// if (pagingParams != null) {
		// query.setFirstResult((pagingParams.getCurrentPage() - 1)
		// * pagingParams.getPageSize());
		// query.setMaxResults(pagingParams.getPageSize());
		// }
		// List<FrmTopic> queryResults = query.getResultList();
		//
		// for (FrmTopic topic : queryResults) {
		// TopicDTO dto = ConverterUtil.convert2TopicDTO(topic);
		// dto = getMessageInfo(dto);
		// try {
		// dto.setModerationStatus(getTopicModerationStatus(topic.getId()));
		// } catch (QForumException ex) {
		// LOGGER.log(Level.SEVERE, ex.getMessage().toString());
		// dto = null;
		// }
		// resultList.add(dto);
		// }
		//
		// return resultList;

		return null;
	}

	/**
	 * Calculates the total number of messages as well as the last post for a
	 * particular topic.
	 *
	 * @param topic
	 *            The DTO containing the topic ID.
	 * @return The topic DTO passed in, populated with the collected info.
	 */
	private TopicDTO getMessageInfo(TopicDTO topic) throws QForumException {
		TopicDTO retVal = topic;

		// Get the total number of accepted messages.
		Query query = em.createQuery("SELECT m FROM FrmMessage m "
				+ "WHERE m.frmTopicId.id = :topicId "
				+ "  AND m.moderationStatus = :messageStatus "
				+ "ORDER BY m.createdOn DESC");
		query.setParameter("topicId", topic.getId());
		query.setParameter("messageStatus",
				ForumConstants.MODERATION_STATUS_ACCEPTED);
		List l = query.getResultList();
		retVal.setAcceptedMessages(Long.valueOf((long) l.size()));
		// Get the info on the last message posted on that topic.
		if (!l.isEmpty()) {
			FrmMessage latestMessage = (FrmMessage) l.get(0);
			retVal.setLastMessageAuthorId(latestMessage.getCreatedBy());
			retVal.setLastMessageDate(latestMessage.getCreatedOn());
		}
		// If this is a moderated topic, check the number of pending messages.
		if (topic.getModerated()) {
			query.setParameter("messageStatus",
					ForumConstants.MODERATION_STATUS_PENDING);
			retVal.setPendingMessages(Long.valueOf((long) query.getResultList()
					.size()));
		}

		return retVal;
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
	public boolean lockTopic(String topicId) throws QForumException {
		FrmTopic topicEntity = LookupHelper.retrieveTopic(topicId, em);
		if (topicEntity.getStatus() == ForumConstants.TOPIC_STATUS_UNLOCKED) {
			topicEntity.setStatus(ForumConstants.TOPIC_STATUS_LOCKED);
			return true;
		}

		return false;
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
	public boolean unlockTopic(String topicId) throws QForumException {
		FrmTopic topicEntity = LookupHelper.retrieveTopic(topicId, em);
		if (topicEntity.getStatus() == ForumConstants.TOPIC_STATUS_LOCKED) {
			topicEntity.setStatus(ForumConstants.TOPIC_STATUS_UNLOCKED);
			return true;
		}
		return false;
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
	public short getTopicStatus(String topicId) throws QForumException {
		FrmTopic topicEntity = LookupHelper.retrieveTopic(topicId, em);

		return topicEntity.getStatus();
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
	public short getTopicModerationStatus(String topicId)
			throws QForumException {
		FrmTopic topic = LookupHelper.retrieveTopic(topicId, em);

		return topic.getModerationStatus();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param topicId
	 *            {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void acceptTopic(String topicId) throws QForumException {
		FrmTopic topic = LookupHelper.retrieveTopic(topicId, em);

		if (topic.getModerationStatus() == ForumConstants.MODERATION_STATUS_PENDING) {
			topic.setModerationStatus(ForumConstants.MODERATION_STATUS_ACCEPTED);
		} else {
			throw new QOperationNotSupported("Topic with id '" + topicId
					+ "' is not pending moderation.");
		}

		// Post a notification about the event.
		// if
		// (PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.Forum.realtime.JMS.notifications").equals("true"))
		// {
		// ForumMessage jmsMessage = new ForumMessage();
		// jmsMessage.setType(ForumMessage.MSGTYPE__ACCEPT_TOPIC);
		// jmsMessage.setSrcUserID(topic.getFrmForumId().getCreatedBy());
		// jmsMessage.setStringProperty(ForumMessage.PRIVATE_USERID,
		// topic.getCreatedBy());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__FORUM_ID,
		// topic.getFrmForumId().getId());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__FORUM_TITLE,
		// topic.getFrmForumId().getTitle());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__TOPIC_TITLE,
		// topic.getTitle());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__TOPIC_ID,
		// topic.getId());
		// try {
		// Messenger.post(connectionFactory, notificationTopic, jmsMessage);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage());
		// throw new QForumException(CODES.ERR_FORUM_0009,
		// ex.getLocalizedMessage());
		// }
		// }
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param topicId
	 *            {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void rejectTopic(String topicId) throws QForumException {
		FrmTopic topic = LookupHelper.retrieveTopic(topicId, em);
		if (topic.getModerationStatus() == ForumConstants.MODERATION_STATUS_PENDING) {
			topic.setModerationStatus(ForumConstants.MODERATION_STATUS_REJECTED);
		} else {
			throw new QOperationNotSupported("Topic with id '" + topicId
					+ "' is not pending moderation.");
		}

		// Post a notification about the event.
		// if
		// (PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.Forum.realtime.JMS.notifications").equals("true"))
		// {
		// ForumMessage jmsMessage = new ForumMessage();
		// jmsMessage.setType(ForumMessage.MSGTYPE__REJECT_TOPIC);
		// jmsMessage.setSrcUserID(topic.getFrmForumId().getCreatedBy());
		// jmsMessage.setStringProperty(ForumMessage.PRIVATE_USERID,
		// topic.getFrmForumId().getCreatedBy());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__FORUM_ID,
		// topic.getFrmForumId().getId());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__FORUM_TITLE,
		// topic.getFrmForumId().getTitle());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__TOPIC_TITLE,
		// topic.getTitle());
		// jmsMessage.setStringProperty(ForumMessage.PROPERTY__TOPIC_ID,
		// topic.getId());
		// try {
		// Messenger.post(connectionFactory, notificationTopic, jmsMessage);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage());
		// throw new QForumException(CODES.ERR_FORUM_0009,
		// ex.getLocalizedMessage());
		// }
		// }
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
	public boolean archiveTopic(String topicId) throws QForumException {
		FrmTopic topicEntity = LookupHelper.retrieveTopic(topicId, em);

		if (!topicEntity.isArchived()) {
			topicEntity.setArchived(true);
			return true;
		}
		return false;
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
	public boolean unarchiveTopic(String topicId) throws QForumException {
		FrmTopic topicEntity = LookupHelper.retrieveTopic(topicId, em);

		if (topicEntity.isArchived()) {
			topicEntity.setArchived(false);
			return true;
		}
		return false;
	}

}
