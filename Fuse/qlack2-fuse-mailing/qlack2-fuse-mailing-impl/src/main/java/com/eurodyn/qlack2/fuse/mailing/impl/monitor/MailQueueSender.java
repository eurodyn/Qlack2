package com.eurodyn.qlack2.fuse.mailing.impl.monitor;

import com.eurodyn.qlack2.fuse.mailing.api.dto.AttachmentDTO;
import com.eurodyn.qlack2.fuse.mailing.api.dto.EmailDTO;
import com.eurodyn.qlack2.fuse.mailing.api.dto.EmailDTO.EMAIL_TYPE;
import com.eurodyn.qlack2.fuse.mailing.api.exception.QMailingException;
import javax.activation.DataSource;
import javax.inject.Singleton;
import javax.mail.util.ByteArrayDataSource;
import org.apache.aries.blueprint.annotation.config.ConfigProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class MailQueueSender {

  /**
   * Logger reference
   */
  private static final Logger LOGGER = Logger.getLogger(MailQueueSender.class.getName());

  @ConfigProperty("${debug}")
  private boolean debug;
  @ConfigProperty("${server.starttls}")
  private boolean startTLS;
  @ConfigProperty("${server.host}")
  private String host;
  @ConfigProperty("${server.port}")
  private int port;
  @ConfigProperty("${server.username}")
  private String username;
  @ConfigProperty("${server.password}")
  private String password;

  /**
   * Setup commons attributes of the email not related to its type.
   *
   * @param email The Email to set the attributes to.
   * @param vo The DTO with the attributes to set.
   * @throws EmailException Indicating an error while setting recipients.
   */
  private void setupCommons(Email email, EmailDTO vo) throws EmailException {
    email.setHostName(host);
    email.setSmtpPort(port);
    email.setFrom(vo.getFrom());
    email.setSubject(vo.getSubject());
    email.setSentDate(new Date());

    if (CollectionUtils.isNotEmpty(vo.getToContact())) {
      for (String recipient : vo.getToContact()) {
        email.addTo(recipient);
      }
    }

    if (CollectionUtils.isNotEmpty(vo.getCcContact())) {
      for (String recipient : vo.getCcContact()) {
        email.addCc(recipient);
      }
    }

    if (CollectionUtils.isNotEmpty(vo.getBccContact())) {
      for (String recipient : vo.getBccContact()) {
        email.addBcc(recipient);
      }
    }

    if (CollectionUtils.isNotEmpty(vo.getReplyToContact())) {
      for (String recipient : vo.getReplyToContact()) {
        email.addReplyTo(recipient);
      }
    }
  }

  /**
   * Attaches email attachments.
   * @param email The email to attach the attachments to.
   * @param vo The DTO with the attachments to attach.
   * @throws EmailException Indicating an error while processing attachments.
   */
  private void setupAttachments(MultiPartEmail email,  EmailDTO vo) throws EmailException {
    for (AttachmentDTO attachmentDTO : vo.getAttachments()) {
      DataSource source = new ByteArrayDataSource(attachmentDTO.getData(), attachmentDTO.getContentType());
      email.attach(source, attachmentDTO.getFilename(), attachmentDTO.getFilename());
    }
  }

  /**
   * Send the email.
   */
  public void send(EmailDTO vo) throws QMailingException {
    Email email;

    try {
      if (vo.getEmailType() == EMAIL_TYPE.HTML) { // HTML email
        email = new HtmlEmail();
        setupAttachments(((HtmlEmail) email), vo);
        ((HtmlEmail) email).setHtmlMsg(vo.getBody());
      } else {  // Plaintext email
        if (!CollectionUtils.isEmpty(vo.getAttachments())) {
          email = new MultiPartEmail();
          setupAttachments(((MultiPartEmail) email), vo);
        } else {
          email = new SimpleEmail();
        }
        email.setMsg(vo.getBody());
      }
      setupCommons(email, vo);

      LOGGER.log(Level.FINEST, "Sending email {0} to {1} with TLS={2}.", new Object[]{
        vo.getSubject(), Arrays.asList(vo.getToContact()), startTLS
      });

      /** Enable authentication */
      if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
        email.setAuthentication(username, password);
      }

      /** Enable STARTTLS */
      email.setStartTLSRequired(startTLS);

      email.send();
    } catch (Exception e) {
      throw new QMailingException("There was a problem sending email.", e);
    }
  }
}
