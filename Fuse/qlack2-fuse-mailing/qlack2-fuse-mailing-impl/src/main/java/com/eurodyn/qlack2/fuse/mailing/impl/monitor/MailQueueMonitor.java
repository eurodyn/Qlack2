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
package com.eurodyn.qlack2.fuse.mailing.impl.monitor;

import com.eurodyn.qlack2.fuse.mailing.api.MailService;
import com.eurodyn.qlack2.fuse.mailing.api.dto.AttachmentDTO;
import com.eurodyn.qlack2.fuse.mailing.api.dto.EmailDTO;
import com.eurodyn.qlack2.fuse.mailing.api.exception.QMailingException;
import com.eurodyn.qlack2.fuse.mailing.impl.model.Attachment;
import com.eurodyn.qlack2.fuse.mailing.impl.model.Email;
import com.eurodyn.qlack2.fuse.mailing.impl.util.ConverterUtil;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.apache.aries.blueprint.annotation.config.ConfigProperty;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Monitor email queue.
 *
 * @author European Dynamics SA
 */
@Transactional
@Singleton
public class MailQueueMonitor {

  /**
   * Logger reference
   */
  private static final Logger LOGGER = Logger.getLogger(MailQueueMonitor.class.getName());

  @PersistenceContext(unitName = "fuse-mailing")
  private EntityManager em;



  @ConfigProperty("${maxTries}")
  private byte maxTries;

  @Inject
  private MailQueueSender sender;

  private void send(Email email) {
    /** Create a DTO for the email about to be sent */
    EmailDTO dto = new EmailDTO();
    dto.setId(email.getId());
    dto.setSubject(email.getSubject());
    dto.setBody(email.getBody());
    dto.setFrom(email.getFromEmail());
    dto.setCharset(email.getCharset());
    if (email.getToEmails() != null) {
      dto.setToContact(ConverterUtil.createRecepientlist(email.getToEmails()));
    }
    if (email.getCcEmails() != null) {
      dto.setCcContact(ConverterUtil.createRecepientlist(email.getCcEmails()));
    }
    if (email.getBccEmails() != null) {
      dto.setBccContact(ConverterUtil.createRecepientlist(email.getBccEmails()));
    }
    if (email.getReplyToEmails() != null) {
      dto.setReplyToContact(ConverterUtil.createRecepientlist(email.getReplyToEmails()));
    }
    if (email.getEmailType().equals("HTML")) {
      dto.setEmailType(EmailDTO.EMAIL_TYPE.HTML);
    } else {
      dto.setEmailType(EmailDTO.EMAIL_TYPE.TEXT);
    }

    /** Process attachments. */
    Set<Attachment> attachments = email.getAttachments();
    for (Attachment attachment : attachments) {
      AttachmentDTO attachmentDTO = new AttachmentDTO();
      attachmentDTO.setContentType(attachment.getContentType());
      attachmentDTO.setData(attachment.getData());
      attachmentDTO.setFilename(attachment.getFilename());
      dto.addAttachment(attachmentDTO);
    }

    /** Update email's tries and date sent in the database, irrespectively of the outcome of the
     * sendig process.
     */
    email.setTries((byte) (email.getTries() + 1));
    email.setDateSent(System.currentTimeMillis());

    /** Try to send the email */
    try {
      sender.send(dto);

      /** If the email was sent successfully, we can update its status to Sent, so that the scheduler
       * does not try to resend it.
       */
      email.setStatus(MailService.EMAIL_STATUS.SENT.toString());
    } catch (QMailingException ex) {
      LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      /** Set the reason for failure in the database */
      //TODO This takes into account the top-level Exception which very often hides the real reason
      //something failed. It would be nice to have some helper method to unwrap all exceptions and
      //provide a concatenated string with all error messages.
      Throwable t = ex.getCause() != null ? ex.getCause() : ex;
      email.setServerResponse(t.getLocalizedMessage());
      email.setServerResponseDate(System.currentTimeMillis());
      /** If anything went wrong during delivery check if the maximum attempts have been reached
       * and mark the email as Failed in that case.
       */
      if (email.getTries() >= maxTries) {
        email.setStatus(MailService.EMAIL_STATUS.FAILED.toString());
      }
    }
    em.merge(email);
  }

  public void sendOne(String emailId) {
    send(Email.find(em, emailId));
  }

  /**
   * Check for QUEUED emails and send them.
   */
  public void checkAndSendQueued() {
    List<Email> emails = Email.findQueued(em, maxTries);
    LOGGER.log(Level.FINEST, "Found {0} email(s) to be sent.", emails.size());

    for (Email email : emails) {
      send(email);
    }
  }

}
