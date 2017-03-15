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

import java.util.Date;
import java.util.List;

/**
 * Data transfer object for internal messages.
 *
 * @author European Dynamics SA.
 */
public class InternalMessagesDTO extends MailBaseDTO {
	private static final long serialVersionUID = -1472700801352450573L;

	private String subject;
	private String message;
	private String from;
	private String to;
	private Date dateSent;
	private Date dateReceived;
	private String status;
	private String deleteType;
	private List<InternalAttachmentDTO> attachments;
	private String fwdAttachmentId;

	// -- Accessors

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Date getDateSent() {
		return dateSent;
	}

	public void setDateSent(Long dateSent) {
		this.dateSent = new Date(dateSent);
	}

	public Date getDateReceived() {
		return dateReceived;
	}

	public void setDateReceived(Long dateReceived) {
		this.dateReceived = new Date(dateReceived);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDeleteType() {
		return deleteType;
	}

	public void setDeleteType(String deleteType) {
		this.deleteType = deleteType;
	}

	public List<InternalAttachmentDTO> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<InternalAttachmentDTO> attachments) {
		this.attachments = attachments;
	}

	public String getFwdAttachmentId() {
		return fwdAttachmentId;
	}

	public void setFwdAttachmentId(String fwdAttachmentId) {
		this.fwdAttachmentId = fwdAttachmentId;
	}
}
