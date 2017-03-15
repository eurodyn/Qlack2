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
package com.eurodyn.qlack2.fuse.mailing.api;

import java.util.List;

import com.eurodyn.qlack2.fuse.mailing.api.dto.InternalAttachmentDTO;
import com.eurodyn.qlack2.fuse.mailing.api.dto.InternalMessagesDTO;

/**
 * Provide internal messages related services.
 *
 * Following use case are implemented by the service:
 * <ul>
 * <li>Send internal message.</li>
 * <li>Get internal INBOX folder messages.</li>
 * <li>Get internal SENT folder messages.</li>
 * </ul>
 *
 * @author European Dynamics SA.
 */
public interface InternalMessageService {

	/**
	 * Send internal message/email to the user.
	 *
	 * @param dto InternalMessagesDTO containing the message details.
	 * @return InternalMessagesDTO containing the message details.
	 */
	InternalMessagesDTO sendInternalMail(InternalMessagesDTO dto);

	/**
	 * Gets/Displays the Internal INBOX messages.
	 *
	 * @param userId user id of the user whose INBOX folder needs to be displayed.
	 * @return List of INBOX messages.
	 */
	List<InternalMessagesDTO> getInternalInboxFolder(String userId);

	/**
	 * Gets/Displays the Internal SENT messages.
	 *
	 * @param userId user id of the user whose SENT messages folder needs to be displayed.
	 * @return List of SENT messages.
	 */
	List<InternalMessagesDTO> getInternalSentFolder(String userId);

	/**
	 * This method marks message in INBOX as read.
	 *
	 * @param messageId
	 */
	void markMessageAsRead(String messageId);

	/**
	 * This method marks message in INBOX as replied.
	 *
	 * @param messageId
	 */
	void markMessageAsReplied(String messageId);

	/**
	 * This method marks message in INBOX as unread.
	 *
	 * @param messageId
	 */
	void markMessageAsUnread(String messageId);

	/**
	 * This method deletes message from INBOX or SENT folder according to folder type.
	 *
	 * @param messageId Internal Message PK.
	 * @param folderType
	 */
	void deleteMessage(String messageId, String folderType);

	/**
	 * This method views the email.
	 *
	 * @param messageId Internal Message PK.
	 * @return InternalMessagesDTO
	 */
	InternalMessagesDTO getInternalMessage(String messageId);

	/**
	 * Returns the List of attachments for a internal message.
	 *
	 * @param messageId Internal Message PK.
	 * @return List of internal message attachments.
	 */
	List<InternalAttachmentDTO> getInternalMessageAttachments(String messageId);

	/**
	 * Returns the internal attachment for saving in database.
	 *
	 * @param attachmentId
	 * @return InternalAttachmentDTO attachment for saving in db.
	 */
	InternalAttachmentDTO getInternalAttachment(String attachmentId);

	/**
	 * This method returns mail count for a User and for read/unread status of mail
	 *
	 * @param userId User Id of user (for null value count will return for all user)
	 * @param readStatus It can be UNREAD or READ (for null value count will return for all mail)
	 * @return Count of mails
	 */
	long getMailCount(String userId, String readStatus);
}
