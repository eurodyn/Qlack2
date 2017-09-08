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
package com.eurodyn.qlack2.fuse.mailing.impl;

import com.eurodyn.qlack2.fuse.mailing.api.InternalMessageService;
import com.eurodyn.qlack2.fuse.mailing.api.dto.InternalAttachmentDTO;
import com.eurodyn.qlack2.fuse.mailing.api.dto.InternalMessagesDTO;
import com.eurodyn.qlack2.fuse.mailing.impl.model.InternalAttachment;
import com.eurodyn.qlack2.fuse.mailing.impl.model.InternalMessages;
import com.eurodyn.qlack2.fuse.mailing.impl.util.ConverterUtil;
import com.eurodyn.qlack2.fuse.mailing.impl.util.MaiConstants;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Provide internal messages related services. For details regarding the functionality offered see
 * the respective interfaces.
 *
 * @author European Dynamics SA.
 */
@Transactional
@Singleton
@OsgiServiceProvider(classes = {InternalMessageService.class})
public class InternalMessageServiceImpl implements InternalMessageService {

  @PersistenceContext(unitName = "fuse-mailing")
  private EntityManager em;

  /**
   * Send a new internal message.
   *
   * @param dto - the internal message data
   * @return - an InternalMessagesDTO object
   */
  @Override
  @Transactional(TxType.REQUIRED)
  public InternalMessagesDTO sendInternalMail(InternalMessagesDTO dto) {

    InternalMessages internalMessages = ConverterUtil.internalMessageConvert(dto);

    // Status can be READ, UNREAD, REPLIED.
    internalMessages.setStatus("UNREAD");
    internalMessages.setDeleteType("N");

    em.persist(internalMessages);

    List<InternalAttachmentDTO> internalAttachments = dto.getAttachments();
    if (internalAttachments == null) {
      internalAttachments = new ArrayList<>();
    }

    String fwdAttachmentId = dto.getFwdAttachmentId();
    if (fwdAttachmentId != null) {
      InternalAttachmentDTO fwdInternalAttachmentDto = getInternalAttachment(fwdAttachmentId);
      internalAttachments.add(fwdInternalAttachmentDto);
    }

    for (InternalAttachmentDTO attachmentDto : internalAttachments) {
      InternalAttachment attachment = ConverterUtil.internalAttachmentConvert(attachmentDto);
      attachment.setMessages(internalMessages);
      em.persist(attachment);
    }

    return ConverterUtil.internalMessageConvert(internalMessages);
  }

  /**
   * Get the Inbox.
   *
   * @param userId - the person that the message was sent to
   * @return a list of InternalMessagesDTO
   */
  @Override
  @Transactional(TxType.REQUIRED)
  public List<InternalMessagesDTO> getInternalInboxFolder(String userId) {
    List<InternalMessages> internalMessagesList = InternalMessages.findUserInbox(em, userId);

    List<InternalMessagesDTO> dtoList = ConverterUtil
      .internalMessageConvertList(internalMessagesList);

    return dtoList;
  }

  /**
   * Get the sent folder.
   *
   * @param userId - the person that sent the message
   * @return a list of messages
   */
  @Override
  @Transactional(TxType.REQUIRED)
  public List<InternalMessagesDTO> getInternalSentFolder(String userId) {
    List<InternalMessages> internalMessagesList = InternalMessages.findUserSent(em, userId);

    List<InternalMessagesDTO> dtoList = ConverterUtil
      .internalMessageConvertList(internalMessagesList);

    return dtoList;
  }

  /**
   * Get the number of the messages
   *
   * @param userId - the person that the message was sent to
   * @param status - the status (read, unread, replied)
   * @return the No of messages.
   */
  @Override
  @Transactional(TxType.REQUIRED)
  public long getMailCount(String userId, String status) {
    Long count = InternalMessages.countByUserAndStatus(em, userId, status);
    return count;
  }

  /**
   * Mark a message as Read
   *
   * @param messageId - the message Id
   */
  @Override
  @Transactional(TxType.REQUIRED)
  public void markMessageAsRead(String messageId) {
    InternalMessages internalMessages = em.find(InternalMessages.class, messageId);
    internalMessages.setStatus(MaiConstants.MARK_READ);
  }

  /**
   * Mark a message as Replied.
   *
   * @param messageId - the message Id
   */
  @Override
  @Transactional(TxType.REQUIRED)
  public void markMessageAsReplied(String messageId) {
    InternalMessages internalMessages = em.find(InternalMessages.class, messageId);
    internalMessages.setStatus(MaiConstants.MARK_REPLIED);
  }

  /**
   * Mark a message as Unread.
   *
   * @param messageId - the message Id
   */
  @Override
  @Transactional(TxType.REQUIRED)
  public void markMessageAsUnread(String messageId) {
    InternalMessages internalMessages = em.find(InternalMessages.class, messageId);
    internalMessages.setStatus(MaiConstants.MARK_UNREAD);
  }

  /**
   * Delete a message.
   *
   * Depending on the folder type (inbox or sent) this method perform the following: <ul> <li> if
   * the folder that contains the message is the inbox and the sender has already deleted the
   * message, then the message is permanently removed from the system.</li> <li> if the folder that
   * contains the message is the inbox and the sender has not deleted the message, then the message
   * is marked as "deleted from the sender".</li> <li> if the folder that contains the message is
   * the sent folder and the receiver has already deleted the message, then the message is
   * permanently removed from the system.</li> <li> if the folder that contains the message is the
   * sent folder and the receiver has not deleted the message, then the message is marked as
   * "deleted from the receiver".</li> </ul>
   *
   * @param messageId - the message Id
   * @param folderType - the folder type (inbox or sent)
   */
  @Override
  @Transactional(TxType.REQUIRED)
  public void deleteMessage(String messageId, String folderType) {
    InternalMessages internalMessages = em.find(InternalMessages.class, messageId);
    if (MaiConstants.INBOX_FOLDER_TYPE.equals(folderType)) {
      if ("S".equals(internalMessages.getDeleteType())) {
        em.remove(em.merge(internalMessages));
      } else {
        internalMessages.setDeleteType("I");
      }
    }
    if (MaiConstants.SENT_FOLDER_TYPE.equals(folderType)) {
      if ("I".equals(internalMessages.getDeleteType())) {
        em.remove(em.merge(internalMessages));
      } else {
        internalMessages.setDeleteType("S");
      }
    }
  }

  /**
   * View the details of a message.
   *
   * @param messageId - the message Id
   * @return the message
   */
  @Override
  @Transactional(TxType.REQUIRED)
  public InternalMessagesDTO getInternalMessage(String messageId) {
    InternalMessages internalMessages = em.find(InternalMessages.class, messageId);
    return ConverterUtil.internalMessageConvert(internalMessages);
  }

  /**
   * Get the attachments of a message.
   *
   * @param messageId - the message Id
   */
  @Override
  @Transactional(TxType.REQUIRED)
  public List<InternalAttachmentDTO> getInternalMessageAttachments(String messageId) {
    List<InternalAttachment> internalAttachments = InternalAttachment
      .findByMessagesId(em, messageId);

    List<InternalAttachmentDTO> internalAttachmentDtos = new ArrayList<>();
    for (InternalAttachment internalAttachment : internalAttachments) {
      internalAttachmentDtos.add(ConverterUtil.internalAttachmentConvert(internalAttachment));
    }
    return internalAttachmentDtos;
  }

  /**
   * Get an attachment based on its Id.
   *
   * @param attachmentId - the attachment Id.
   * @return the attachment
   */
  @Override
  @Transactional(TxType.REQUIRED)
  public InternalAttachmentDTO getInternalAttachment(String attachmentId) {
    InternalAttachment internalAttachment = InternalAttachment.findById(em, attachmentId);
    return ConverterUtil.internalAttachmentConvert(internalAttachment);
  }

}
