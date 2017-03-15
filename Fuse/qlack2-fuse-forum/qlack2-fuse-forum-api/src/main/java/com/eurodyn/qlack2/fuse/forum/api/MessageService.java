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
import com.eurodyn.qlack2.fuse.forum.api.dto.MessageDTO;
import com.eurodyn.qlack2.fuse.forum.api.exception.QForumException;

import java.util.List;

/**
 * An interface for EJBs providing message management related services.
 *
 * @author European Dynamics SA
 */
public interface MessageService {

	/**
	 * Posts a new message in the topic.
	 *
	 * @param message
	 *            The details of the message in the topic to post. This
	 *            parameter should not be null. The following properties of the
	 *            MessageDTO class should be not null in the object passed as
	 *            topic parameter:<br>
	 *            - creatorId: The id of the creator of the message. The creator
	 *            of the message should be registered as a participant in the
	 *            forum/topic in which the message will be posted.<br>
	 *            - createdOn: The date at the time of new message creation.<br>
	 *            - topicId: The id of the topic under which the new message to
	 *            be posted. The topic should not be disabled or archived for
	 *            message creation.<br>
	 *            - parentId: The id of the parent message to which the user
	 *            replies as a new message.<br>
	 *            - messageText: The text of the message for createing new
	 *            message. This parameter should not be null.<br>
	 *            - Status: The status of the new message. This will depend on
	 *            the topic moderation. If the topic is moderated, the message
	 *            status will be pending (MESSAGE STATUS PENDING = 2) otherwise
	 *            the ststus will be accepted(MESSAGE STATUS ACCEPTED = 1).<br>
	 *            - attachments: List of attachments of new message
	 * @return The newly created message with id
	 * @throws QForumException
	 *             If the topic in which the message to be posted, is disabled.
	 *
	 */
	public MessageDTO postMessage(MessageDTO message) throws QForumException;

	/**
	 * Deletes a specific message in a topic from the system for the given
	 * message id.
	 *
	 * @param messageId
	 *            The id of the message to delete. This parameter should not be
	 *            null.
	 * @throws QForumException
	 *             If the forum/topic is disabled in which the message exists
	 *             and trying to delete it, if the message to delete doesn't
	 *             exist under the topic in the system.
	 */
	public void deleteMessage(String messageId) throws QForumException;

	/**
	 * Updates a specific message information in the topic.
	 *
	 * @param messageDTO
	 *            The message details in DTO for updating the specific
	 *            topic.Following details should not be null: -Message id : id
	 *            of the message to update. This input should not be null.<br>
	 *            -text : The message text to update. This input should not be
	 *            null.<br>
	 * @return The updated topic
	 * @throws QForumException
	 *             - If the status of forum or topic is disabled for message
	 *             update.
	 */
	public MessageDTO updateMessage(MessageDTO messageDTO)
			throws QForumException;

	/**
	 * Retrieves the details of a message in a topic
	 *
	 * @param messageId
	 *            The id of the message to retrieve. This parameter should not
	 *            be null.
	 * @return A MessageDTO object holding the details of the requested message
	 * @throws QForumException
	 *             If the forum/topic in which the message resides, is disabled
	 */
	public MessageDTO getMessageById(String messageId) throws QForumException;

	/**
	 * Lists all the messages exists in specific topic.
	 *
	 * @param topicId
	 *            - The id of the topic for which all messages to list. This
	 *            parameter should not be null.
	 * @return List of message objects.
	 * @throws QForumException
	 *             - If a topic with the specified id does not exist in the
	 *             system, if the specified forum/topic is disabled and
	 *             therefore its messages cannot be retrieved
	 */
	public List<MessageDTO> listMessages(String topicId) throws QForumException;

	/**
	 * Lists all the messages exists in specific topic.
	 *
	 * @param topicId
	 *            - The id of the topic for which all messages to list. This
	 *            parameter should not be null.
	 * @param pagingParams
	 *            - Parameters for pagination such as current page no, page size
	 *            etc.
	 * @return List of message objects.
	 * @throws QForumException
	 *             - If a topic with the specified id does not exist in the
	 *             system, if the specified forum/topic is disabled and
	 *             therefore its messages cannot be retrieved
	 */
	public List<MessageDTO> listMessages(String topicId,
			PagingParams pagingParams) throws QForumException;

	/**
	 * Retrieves the moderation status of a message (accepter, rejected or
	 * pending).
	 *
	 * @param messageId
	 *            The id of the message whose moderation status will be
	 *            retrieved
	 * @return The message's moderation status
	 * @throws QForumException
	 *             If a message with the provided id does not exist in the
	 *             system
	 */
	public short getMessageModerationStatus(String messageId)
			throws QForumException;

	/**
	 * Sets the status of a message to accepted which is MESSAGE_ACCEPTED = 1.
	 *
	 * @param messageId
	 *            - The id of the message to be accepted.
	 * @return boolean If the message status updates to accept (MESSAGE STATUS
	 *         ACCEPTED = 1) then returns true otherwise returns false.
	 * @throws QForumException
	 *             If a message with the specified id does not exist
	 */
	public boolean acceptMessage(String messageId) throws QForumException;

	/**
	 * Sets the status of a message to rejected which is MESSAGE_REJECTED = 0.
	 *
	 * @param messageId
	 *            - The id of the message for which status to set.
	 * @return boolean If the message status updates to reject (MESSAGE STATUS
	 *         REJECT = 0) then returns true otherwise returns false.
	 * @throws QForumException
	 *             If the message doesn't exists for the provided message id.
	 */
	public boolean rejectMessage(String messageId) throws QForumException;

	/**
	 * Method used to get the attachment for a attachment id.
	 *
	 * @param attachmentId
	 *            - The id of the attachment for retrieving.
	 * @return AttachmentDTO -dto with attachment information
	 * @throws QForumException
	 *             If the attachment doesn't exists for the provided attachment
	 *             id.
	 */
	public AttachmentDTO getAttachment(String attachmentId)
			throws QForumException;

	/**
	 * Method used for retrieving the message with latest date created in a
	 * topic.
	 *
	 * @param topicIs
	 *            - The id of the topic for getting the latest message
	 * @return String - Id of the message
	 */
	public String getLatestMessageId(String topicIs) throws QForumException;

	/**
	 * Returns the text of the first message of a topic.
	 *
	 * @param topicId
	 *            The topic ID who's first message's text is going to be
	 *            returned.
	 * @return The text of the first message of the topic or null if such a
	 *         topic/message does not exist.
	 */
	public String getTopicRootMessage(String topicId);
	
	
	
	/**
	 * Method used for retrieving the total number of forum posts.
	 *
	 * @param userId
	 *            - The id of the topic for getting the latest message
	 * @return long - total amount of logged in user's forum posts
	 */
	public long getAllMessages(String userId) throws QForumException;
	
	
	
	
}
