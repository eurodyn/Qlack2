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
package com.eurodyn.qlack2.fuse.mailing.impl.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.eurodyn.qlack2.fuse.mailing.api.MailService;
import com.eurodyn.qlack2.fuse.mailing.api.MailService.EMAIL_STATUS;

@Entity
@Table(name = "mai_email")
public class Email implements java.io.Serializable {
	private static final long serialVersionUID = -7910084656626067867L;

	@Id
	private String id;

	@Column(name = "subject", length = 254)
	private String subject;

	@Column(name = "body", length = 65535)
	private String body;

	@Column(name = "from_email")
	private String fromEmail;

	@Column(name = "to_emails", length = 1024)
	private String toEmails;

	@Column(name = "cc_emails", length = 1024)
	private String ccEmails;

	@Column(name = "bcc_emails", length = 1024)
	private String bccEmails;

	@Column(name = "email_type", length = 64)
	private String emailType;

	@Column(name = "status", length = 32)
	private String status;

	@Column(name = "tries", nullable = false)
	private byte tries;

	@Column(name = "added_on_date", nullable = false)
	private long addedOnDate;

	@Column(name = "date_sent")
	private Long dateSent;

	@Column(name = "server_response_date")
	private Long serverResponseDate;

	@Column(name = "server_response", length = 1024)
	private String serverResponse;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "email")
	private Set<Attachment> attachments = new HashSet<Attachment>(0);

	// -- Constructors

	public Email() {
		this.id = java.util.UUID.randomUUID().toString();
	}

	// -- Queries

	public static List<Email> findQueued(EntityManager em, byte maxTries) {
		String jpql =
				"SELECT m FROM Email m " +
				"WHERE m.status = :status AND m.tries < :tries";

		return em.createQuery(jpql, Email.class)
				.setParameter("status", MailService.EMAIL_STATUS.QUEUED.toString())
				.setParameter("tries", maxTries)
				.getResultList();
	}

	public static List<Email> findByDateAndStatus(EntityManager em, Long date, EMAIL_STATUS[] statuses) {
		String select =
				"SELECT m FROM Email m ";

		List<String> predicates = new ArrayList<>(2);
		if (date != null) {
			predicates.add("(addedOnDate <= " + date.longValue() + ")");
		}
		if (statuses != null && statuses.length > 0) {
			// open-coded join()
			StringBuilder sb = new StringBuilder("(status IN (");
			sb.append(statuses[0].toString());
			for (int i = 1; i < statuses.length; i++) {
				sb.append(", ").append(statuses[i].toString());
			}
			sb.append("))");
			predicates.add(sb.toString());
		}

		// open-coded join()
		StringBuilder sb = new StringBuilder(select);
		Iterator<String> iter = predicates.iterator();
		if (iter.hasNext()) {
			sb.append(" WHERE ").append(iter.next());
			while (iter.hasNext()) {
				sb.append(" AND ").append(iter.next());
			}
		}
		String jpql = sb.toString();

		return em.createQuery(jpql, Email.class).getResultList();
	}

	// -- Accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getFromEmail() {
		return this.fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getToEmails() {
		return this.toEmails;
	}

	public void setToEmails(String toEmails) {
		this.toEmails = toEmails;
	}

	public String getCcEmails() {
		return this.ccEmails;
	}

	public void setCcEmails(String ccEmails) {
		this.ccEmails = ccEmails;
	}

	public String getBccEmails() {
		return this.bccEmails;
	}

	public void setBccEmails(String bccEmails) {
		this.bccEmails = bccEmails;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getServerResponse() {
		return this.serverResponse;
	}

	public void setServerResponse(String serverResponse) {
		this.serverResponse = serverResponse;
	}

	public Long getServerResponseDate() {
		return this.serverResponseDate;
	}

	public void setServerResponseDate(Long serverResponseDate) {
		this.serverResponseDate = serverResponseDate;
	}

	public String getEmailType() {
		return this.emailType;
	}

	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}

	public Long getDateSent() {
		return this.dateSent;
	}

	public void setDateSent(Long dateSent) {
		this.dateSent = dateSent;
	}

	public long getAddedOnDate() {
		return this.addedOnDate;
	}

	public void setAddedOnDate(long addedOnDate) {
		this.addedOnDate = addedOnDate;
	}

	public byte getTries() {
		return this.tries;
	}

	public void setTries(byte tries) {
		this.tries = tries;
	}

	public Set<Attachment> getAttachments() {
		return this.attachments;
	}

	public void setAttachments(Set<Attachment> attachments) {
		this.attachments = attachments;
	}

}
