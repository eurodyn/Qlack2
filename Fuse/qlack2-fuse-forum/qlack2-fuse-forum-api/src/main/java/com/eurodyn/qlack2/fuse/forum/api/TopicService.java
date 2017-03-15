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
package com.eurodyn.qlack2.fuse.forum.api;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.forum.api.dto.AttachmentDTO;
import com.eurodyn.qlack2.fuse.forum.api.dto.TopicDTO;
import com.eurodyn.qlack2.fuse.forum.api.exception.QForumException;

import java.util.List;
import java.util.Set;

/**
 * An interface for EJBs providing topic management related services.
 *
 * @author European Dynamics SA
 */
public interface TopicService {

	public static enum QLACK_FORUM_TOPICS_DATE_ORDER {
		ASCEDING, DESCENDING
	}

	/**
	 * Posts a new topic in a forum.
	 *
	 * @param topic
	 *            The details of the topic to create. The following properties
	 *            of the TopicDTO class should be not null in the object passed
	 *            as topic parameter:<br>
	 *            - creatorId: The id of the creator of the topic. The creator
	 *            of the topic should be registered as a participant in the
	 *            forum in which the topic will be posted.<br>
	 *            - forumId: The id of the forum in which the topic will be
	 *            posted.<br>
	 *            - title: The title of the topic to create.<br>
	 *            The following properties of the TopicDTO object being passed
	 *            to this method are not taken into account even if set:<br>
	 *            - pendingMessages<br>
	 *            - acceptedMessages<br>
	 *            - lastMessageDate<br>
	 *            - lastMessageAuthorId<br>
	 * @param messageText
	 *            The text of the root message of the topic to be created. This
	 *            parameter should be not null.
	 * @return The newly created topic
	 * @throws QForumException
	 *             If the forumId passed to this method does not correspond to
	 *             an existing forum, if the forum in which the topic is to be
	 *             posted is disabled, if a topic with the specified title
	 *             already exists in the specified forum, if the topic's
	 *             moderated property is set without the forum having a
	 *             "supports moderation" moderated property
	 *
	 */
	public TopicDTO createTopic(TopicDTO topic, String messageText)
			throws QForumException;

	/**
	 * Posts a new topic in a forum.
	 *
	 * @param topic
	 *            The details of the topic to create. The following properties
	 *            of the TopicDTO class should be not null in the object passed
	 *            as topic parameter:<br>
	 *            - creatorId: The id of the creator of the topic. The creator
	 *            of the topic should be registered as a participant in the
	 *            forum in which the topic will be posted.<br>
	 *            - forumId: The id of the forum in which the topic will be
	 *            posted.<br>
	 *            - title: The title of the topic to create.<br>
	 *            The following properties of the TopicDTO object being passed
	 *            to this method are not taken into account even if set:<br>
	 *            - pendingMessages<br>
	 *            - acceptedMessages<br>
	 *            - lastMessageDate<br>
	 *            - lastMessageAuthorId<br>
	 * @param messageText
	 *            The text of the root message of the topic to be created. This
	 *            parameter should be not null.
	 * @param messageAttachments
	 *            The attachment(s) to the root message of the topic to be
	 *            created. This parameter should be not null. For each
	 *            attachment the content, filename and mimetype need to be set.
	 *            In case there are no message attachments the overloaded
	 *            version of this method without the messageAttachments
	 *            parameters should be used.
	 * @return The newly created topic
	 * @throws QForumException
	 *             If the forumId passed to this method does not correspond to
	 *             an existing forum, if the forum in which the topic is to be
	 *             posted is disabled, if a topic with the specified title
	 *             already exists in the specified forum, if the topic's
	 *             moderated property is set without the forum having a
	 *             "supports moderation" moderated property
	 */
	public TopicDTO createTopic(TopicDTO topic, String messageText,
			Set<AttachmentDTO> messageAttachments) throws QForumException;

	/**
	 * Updates a specific topic.
	 *
	 * @param topic
	 *            The updated values to be assigned to the topic. The topic id
	 *            should not be null since it is used to identify the topic to
	 *            be updated. The following properties of the TopicDTO object
	 *            being passed to this method are taken into account and updated
	 *            in the existing topic:<br>
	 *            - title<br>
	 *            - description<br>
	 *            - moderated<br>
	 *            - logo<br>
	 *            The new moderated property (if any) should be consistent with
	 *            the moderated property of the forum containing the topic,
	 *            while it cannot be set to not-moderated if the topic has
	 *            pending messages.
	 * @throws QForumException
	 *             If the topic or the forum in which it is contained is
	 *             disabled and thus cannot be edited, if a topic with the
	 *             provided id does not exist in the system, if a new title is
	 *             attempted to be given to the topic while another topic with
	 *             the same title already exists in the same forum, if the
	 *             constraints relative to the topic's moderated property are
	 *             not respected
	 *
	 */
	public void updateTopic(TopicDTO topic) throws QForumException;

	/**
	 * Deletes a specific topic from the system
	 *
	 * @param topicId
	 *            The id of the topic to delete
	 * @throws QForumException
	 *             If the forum in which the topic resides is disabled
	 */
	public void deleteTopic(String topicId) throws QForumException;

	/**
	 * Retrieves the details of a topic
	 *
	 * @param topicId
	 *            The id of the topic to retrieve
	 * @return A TopicDTO object holding the details of the requested topic
	 * @throws QForumException
	 *             If the topic or the forum in which it resides is disabled
	 */
	public TopicDTO getTopicById(String topicId) throws QForumException;

	/**
	 * Lists all the topics in a specific forum.
	 *
	 * @param forumId
	 *            The id of the forum to topics of which will be listed.
	 * @param includeDisabled
	 *            Whether the disabled topics of the specified forum should be
	 *            included in the result list
	 * @return A list of all the topics contained in the specified forum.
	 * @throws QForumException
	 *             If the specified forum does not exist, if the specified forum
	 *             is disabled
	 */
	public List<TopicDTO> listTopics(String forumId, boolean includeDisabled,
			Boolean archived, QLACK_FORUM_TOPICS_DATE_ORDER ordering)
			throws QForumException;

	/**
	 * Lists all the topics in a specific forum.
	 *
	 * @param forumId
	 *            The id of the forum to topics of which will be listed.
	 * @param includeDisabled
	 *            Whether the disabled topics of the specified forum should be
	 *            included in the result list
	 * @param pagingParams
	 *            Paging support parameters
	 * @return A list of all the topics contained in the specified forum.
	 * @throws QForumException
	 *             If the specified forum does not exist, if the specified forum
	 *             is disabled
	 */
	public List<TopicDTO> listTopics(String forumId, boolean includeDisabled,
			Boolean archived, PagingParams pagingParams,
			QLACK_FORUM_TOPICS_DATE_ORDER ordering) throws QForumException;

	/**
	 * Locks a topic
	 *
	 * @param topicId
	 *            The id of the topic to lock
	 * @return True if the topic was successfully locked, false otherwise (if
	 *         the topic is already locked)
	 * @throws QForumException
	 *             If a topic with the provided id does not exist in the system
	 */
	public boolean lockTopic(String topicId) throws QForumException;

	/**
	 * Unlocks a locked topic
	 *
	 * @param topicId
	 *            The id of the topic to unlock
	 * @return True if the topic was successfully unlocked, false otherwise (if
	 *         the topic is already unlocked)
	 * @throws QForumException
	 *             If a topic with the provided id does not exist in the system
	 */
	public boolean unlockTopic(String topicId) throws QForumException;

	/**
	 * Retrieves the status of a specific topic
	 *
	 * @param topicId
	 *            The id of the topic whose status will be retrieved
	 * @return The topic's status
	 * @throws QForumException
	 *             If a topic with the provided id does not exist in the system
	 */
	public short getTopicStatus(String topicId) throws QForumException;

	/**
	 * Retrieves the moderation status of a topic (accepter, rejected or
	 * pending). The topic's moderation status is in fact the moderation status
	 * of the topic's root message.
	 *
	 * @param topicId
	 *            The id of the topic whose moderation status will be retrieved
	 * @return The topic's moderation status
	 * @throws QForumException
	 *             If a topic with the provided id does not exist in the system
	 */
	public short getTopicModerationStatus(String topicId)
			throws QForumException;

	/**
	 * Accepts a pending topic.
	 *
	 * @param topicId
	 *            - The id of the topic to be accepted.
	 * @throws QForumException
	 *             If a topic with the provided id does not exist in the system,
	 *             if the specified topic is not pending.
	 */
	public void acceptTopic(String topicId) throws QForumException;

	/**
	 * Rejects a pending topic
	 *
	 * @param topicId
	 *            - The id of the topic to be rejected.
	 * @throws QForumException
	 *             If a topic with the provided id does not exist in the system
	 */
	public void rejectTopic(String topicId) throws QForumException;

	/**
	 * Archives a topic
	 *
	 * @param topicId
	 *            The id of the topic to archive
	 * @return True if the topic was successfully archived, false otherwise (if
	 *         the topic is already archived)
	 * @throws QForumException
	 *             If a topic with the provided id does not exist in the system
	 */
	public boolean archiveTopic(String topicId) throws QForumException;

	/**
	 * Un-Archives a topic
	 *
	 * @param topicId
	 *            The id of the topic to un-archive
	 * @return True if the topic was successfully un-archived, false otherwise
	 *         (if the topic is not archived)
	 * @throws QForumException
	 *             If a topic with the provided id does not exist in the system
	 */
	public boolean unarchiveTopic(String topicId) throws QForumException;
}
