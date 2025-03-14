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

import com.eurodyn.qlack2.fuse.mailing.api.MailService;
import com.eurodyn.qlack2.fuse.mailing.api.dto.AttachmentDTO;
import com.eurodyn.qlack2.fuse.mailing.api.dto.EmailDTO;
import com.eurodyn.qlack2.fuse.mailing.impl.model.Attachment;
import com.eurodyn.qlack2.fuse.mailing.impl.model.Email;
import com.eurodyn.qlack2.fuse.mailing.impl.monitor.MailQueueMonitor;
import com.eurodyn.qlack2.fuse.mailing.impl.util.ConverterUtil;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Bean implementation for Send and Search Mail functionality
 *
 * @author European Dynamics SA.
 */
@Transactional
@Singleton
@OsgiServiceProvider(classes = {MailService.class})
public class MailServiceImpl implements MailService {

  @Inject
  MailQueueMonitor mailQueueMonitor;
  @PersistenceContext(unitName = "fuse-mailing")
  private EntityManager em;

  /**
   * Queue a list of Emails.
   *
   * @param dtos - list of email data transfer objects.
   * @return List of email ids
   */
  @Override
  @Transactional(TxType.REQUIRED)
  public List<String> queueEmails(List<EmailDTO> dtos) {
    List<String> emails = new ArrayList<>();
    for (EmailDTO dto : dtos) {
      emails.add(queueEmail(dto));
    }

    return emails;
  }

  /**
   * Queue an email.
   *
   * @param emailDto - an email data transfer object.
   * @return email id
   */
  @Override
  @Transactional(TxType.REQUIRED)
  public String queueEmail(EmailDTO emailDto) {
    Email email = new Email();

    email.setSubject(emailDto.getSubject());
    email.setBody(emailDto.getBody());
    email.setFromEmail(emailDto.getFrom());
    email.setToEmails(ConverterUtil.createRecepientlist(emailDto.getToContact()));
    email.setCcEmails(ConverterUtil.createRecepientlist(emailDto.getCcContact()));
    email.setBccEmails(ConverterUtil.createRecepientlist(emailDto.getBccContact()));
    email.setReplyToEmails(ConverterUtil.createRecepientlist(emailDto.getReplyToContact()));
    email.setEmailType(emailDto.getEmailType().toString());

    email.setTries((byte) 0);
    email.setStatus(EMAIL_STATUS.QUEUED.toString());
    email.setAddedOnDate(System.currentTimeMillis());
    email.setCharset(emailDto.getCharset());

    em.persist(email);

    // Process attachements.
    if (emailDto.getAttachments() != null && !emailDto.getAttachments().isEmpty()) {
      Set<Attachment> attachments = new HashSet<Attachment>();
      for (AttachmentDTO attachmentDto : emailDto.getAttachments()) {
        Attachment attachment = new Attachment();

        attachment.setEmail(email);
        attachment.setFilename(attachmentDto.getFilename());
        attachment.setContentType(attachmentDto.getContentType());
        attachment.setData(attachmentDto.getData());
        attachment.setAttachmentSize(Long.valueOf(attachmentDto.getData().length));

        attachments.add(attachment);

        em.persist(attachment);
      }
      email.setAttachments(attachments);
    }

    return email.getId();
  }

  /**
   * Removes all e-mails prior to the specified date having the requested status. Warning: If you
   * pass a <code>null</code> date all emails irrespectively of date will be removed.
   *
   * @param date the date before which all e-mails will be removed.
   * @param status the status to be processed. Be cautious to not include e-mails of status QUEUED
   * as such e-mails might not have been tried to be delivered yet.
   */
  @Override
  @Transactional(TxType.REQUIRED)
  public void cleanup(Long date, EMAIL_STATUS[] status) {
    List<Email> emails = Email.findByDateAndStatus(em, date, status);
    for (Email email : emails) {
      em.remove(email);
    }
  }

  /**
   * Delete an email from the queue.
   *
   * @param emailId - the email id.
   */
  @Override
  @Transactional(TxType.REQUIRED)
  public void deleteFromQueue(String emailId) {
    Email email = Email.find(em, emailId);
    em.remove(email);
  }

  @Override
  @Transactional(TxType.REQUIRED)
  public void updateStatus(String emailId, EMAIL_STATUS status) {
    Email email = Email.find(em, emailId);
    email.setStatus(status.toString());
  }

  @Override
  @Transactional(TxType.REQUIRED)
  public EmailDTO getMail(String emailId) {
    Email email = Email.find(em, emailId);
    if (email == null) {
      return null;
    }

    return ConverterUtil.emailConvert(email);
  }

  @Override
  public List<EmailDTO> getByStatus(EMAIL_STATUS status) {
    return Email.findByDateAndStatus(em, null, status)
      .stream()
      .map(o -> ConverterUtil.emailConvert(o))
      .collect(Collectors.toList());
  }

  @Override
  public void sendOne(String emailId) {
    mailQueueMonitor.sendOne(emailId);
  }

}
