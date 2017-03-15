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
package com.eurodyn.qlack2.fuse.forum.impl.util;

import javax.persistence.EntityManager;

import com.eurodyn.qlack2.fuse.forum.api.dto.ForumConstants;
import com.eurodyn.qlack2.fuse.forum.api.exception.QForumException;
import com.eurodyn.qlack2.fuse.forum.api.exception.QForumNotFound;
import com.eurodyn.qlack2.fuse.forum.api.exception.QMessageNotFound;
import com.eurodyn.qlack2.fuse.forum.api.exception.QOperationNotSupported;
import com.eurodyn.qlack2.fuse.forum.api.exception.QTopicNotFound;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmForum;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmMessage;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmTopic;

/**
 * Utility class to perform lookups.
 *
 * @author European Dynamics SA
 */
public class LookupHelper {

	/**
	 * Convenience method to check the status of a forum.
	 *
	 * @param forumStatus
	 *            The status to be checked against.
	 * @throws QForumException
	 *             If the status is locked.
	 */
	public static void checkForumStatus(short forumStatus)
			throws QForumException {
		if (forumStatus == ForumConstants.FORUM_STATUS_LOCKED) {
			throw new QOperationNotSupported(
					"Forum is disabled. The requested operation cannot be performed.");
		}
	}

	/**
	 * Convenience method to check whether a forum is archived or not.
	 *
	 * @param forumArchived
	 *            The archived status to be checked against.
	 * @throws QForumException
	 *             If the forum is archived.
	 */
	public static void checkForumArchived(boolean forumArchived)
			throws QForumException {
		if (forumArchived) {
			throw new QOperationNotSupported(
					"Forum is archived. The requested operation cannot be performed.");
		}
	}

	/**
	 * Convenience method to check the status of the topic.
	 *
	 * @param topicStatus
	 *            The status of the topic to be checked against.
	 * @throws QForumException
	 *             If the topic is locked.
	 */
	public static void checkTopicStatus(short topicStatus)
			throws QForumException {
		if (topicStatus == ForumConstants.TOPIC_STATUS_LOCKED) {
			throw new QOperationNotSupported(
					"Topic is disabled. The requested operation cannot be performed.");
		}
	}

	/**
	 * Convenience method to check whether a topic is archived or not.
	 *
	 * @param topicArchived
	 *            The archived status to be checked against.
	 * @throws QForumException
	 */
	public static void checkTopicArchived(boolean topicArchived)
			throws QForumException {
		if (topicArchived) {
			throw new QOperationNotSupported(
					"Topic is archived. The requested operation cannot be performed.");
		}
	}

	/**
	 * Retrieves a forum entity by its forum ID.
	 *
	 * @param forumId
	 *            The forum ID to lookup.
	 * @return A forum entity for the ID passed in.
	 * @throws QForumException
	 *             If the requested forum ID does not exist.
	 */
	public static FrmForum retrieveForum(String forumId, EntityManager em)
			throws QForumException {
		FrmForum forum = em.find(FrmForum.class, forumId);
		if (forum == null) {
			throw new QForumNotFound("Forum with id '" + forumId
					+ "' does not exist.");
		}
		return forum;
	}

	/**
	 * Retrieves a topic entity by its topic ID
	 *
	 * @param topicId
	 *            The topic ID to lookup.
	 * @return A topic entity for the ID passed in.
	 * @throws QForumException
	 *             If the requested topic ID does not exist.
	 */
	public static FrmTopic retrieveTopic(String topicId, EntityManager em)
			throws QForumException {
		FrmTopic topicEntity = em.find(FrmTopic.class, topicId);
		if (topicEntity == null) {
			throw new QTopicNotFound("Topic with id " + topicId
					+ "does not exist in the system.");
		}
		return topicEntity;
	}

	/**
	 * Retrieves a message by its message ID.
	 *
	 * @param messageID
	 *            The message ID to lookup.
	 * @return The message entity for the ID passed in.
	 * @throws QForumException
	 *             If the requested message ID does not exist.
	 */
	public static FrmMessage retrieveMessage(String messageID, EntityManager em)
			throws QForumException {
		FrmMessage retVal = em.find(FrmMessage.class, messageID);
		if (retVal == null) {
			throw new QMessageNotFound("Message with id '" + messageID
					+ "' could not be found.");
		}

		return retVal;
	}
}
