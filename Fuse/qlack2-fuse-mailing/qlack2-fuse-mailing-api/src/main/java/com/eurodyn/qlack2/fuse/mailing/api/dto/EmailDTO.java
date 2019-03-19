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
package com.eurodyn.qlack2.fuse.mailing.api.dto;

import com.eurodyn.qlack2.fuse.mailing.api.util.EmailCharset;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DTO for Email data.
 */
public class EmailDTO implements Serializable {

  private static final long serialVersionUID = -7917759516707628906L;
  private String id;

  ;
  private String messageId;
  private String subject;
  private String body;
  private String from;
  private List<String> toContact;
  private List<String> ccContact;
  private List<String> bccContact;
  private List<String> replyToContact;
  private EMAIL_TYPE emailType;
  private String status;
  private List<AttachmentDTO> attachments;
  private Date dateSent;
  private String serverResponse;
  private String charset = EmailCharset.UTF_8.getValue();
  public EmailDTO() {
    this.emailType = EMAIL_TYPE.TEXT;
  }

  // -- Constructors

  public String getId() {
    return id;
  }

  // -- Accessors

  public void setId(String id) {
    this.id = id;
  }

  public String getMessageId() {
    return messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public List<String> getToContact() {
    return toContact;
  }

  public void setToContact(String toContact) {
    List<String> l = new ArrayList<String>();
    l.add(toContact);
    setToContact(l);
  }

  public void setToContact(List<String> toContact) {
    this.toContact = toContact;
  }

  public List<String> getCcContact() {
    return ccContact;
  }

  public void setCcContact(List<String> ccContact) {
    this.ccContact = ccContact;
  }

  public List<String> getBccContact() {
    return bccContact;
  }

  public void setBccContact(List<String> bccContact) {
    this.bccContact = bccContact;
  }

  public List<String> getReplyToContact() {
    return replyToContact;
  }

  public void setReplyToContact(List<String> replyToContact) {
    this.replyToContact = replyToContact;
  }

  public EMAIL_TYPE getEmailType() {
    return emailType;
  }

  public void setEmailType(EMAIL_TYPE emailType) {
    this.emailType = emailType;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<AttachmentDTO> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<AttachmentDTO> attachment) {
    this.attachments = attachment;
  }

  public void addAttachment(AttachmentDTO attachmentDTO) {
    if (attachments == null) {
      attachments = new ArrayList<>();
    }
    attachments.add(attachmentDTO);
  }

  public Date getDateSent() {
    return dateSent;
  }

  public void setDateSent(Long dateSent) {
    if (dateSent != null) {
      this.dateSent = new Date(dateSent);
    }
  }

  public String getServerResponse() {
    return serverResponse;
  }

  public void setServerResponse(String serverResponse) {
    this.serverResponse = serverResponse;
  }

  public String getCharset() {
    return charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public void resetAllRecipients() {
    this.toContact = null;
    this.ccContact = null;
    this.bccContact = null;
  }

  @Override
  public String toString() {
    StringBuffer strBuf = new StringBuffer();
    strBuf.append("DTO id is: " + getId())
      .append("Subject is: " + getSubject())
      .append("To contact List: ").append(getToContact() != null ? getToContact().toString() : null)
      .append("CC contact List: ").append(getCcContact() != null ? getCcContact().toString() : null)
      .append("BCC contact List: ")
      .append(getBccContact() != null ? getBccContact().toString() : null)
      .append("body: ").append(body)
      .append("status: ").append(status)
      .append("Date sent: ").append(dateSent)
      .append("Server Response: ").append(serverResponse)
      .append("attachment: ").append(attachments)
      .append("email type: ").append(emailType)
      .append("message Id: ").append(messageId);
    return strBuf.toString();
  }

  public static enum EMAIL_TYPE {
    TEXT, HTML
  }
}
