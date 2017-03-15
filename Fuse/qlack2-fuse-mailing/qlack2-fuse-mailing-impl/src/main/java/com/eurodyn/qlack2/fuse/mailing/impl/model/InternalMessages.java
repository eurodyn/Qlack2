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
import javax.persistence.TypedQuery;

@Entity
@Table(name = "mai_internal_messages")
public class InternalMessages implements java.io.Serializable {
	private static final long serialVersionUID = -8617985677007090630L;

	@Id
	private String id;

	@Column(name = "subject", nullable = false, length = 100)
	private String subject;

	@Column(name = "message", nullable = false, length = 65535)
	private String message;

	@Column(name = "mail_from", nullable = false, length = 36)
	private String mailFrom;

	@Column(name = "mail_to", nullable = false, length = 36)
	private String mailTo;

	@Column(name = "date_sent")
	private Long dateSent;

	@Column(name = "date_received")
	private Long dateReceived;

	@Column(name = "status", length = 7)
	private String status;

	@Column(name = "delete_type", nullable = false, length = 1)
	private String deleteType;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "messages")
	private Set<InternalAttachment> attachments = new HashSet<InternalAttachment>(0);

	// -- Constructors

	public InternalMessages() {
		this.id = java.util.UUID.randomUUID().toString();
	}

	// -- Queries

	public static List<InternalMessages> findAll(EntityManager em) {
		String jpql = "SELECT m FROM InternalMessages m";

		return em.createQuery(jpql, InternalMessages.class).getResultList();
	}

	public static List<InternalMessages> findUserInbox(EntityManager em, String userId) {
		String jpql =
				"SELECT m FROM InternalMessages m " +
				"WHERE m.mailTo = :userId AND m.deleteType <> 'I'";

		return em.createQuery(jpql, InternalMessages.class)
				.setParameter("userId", userId)
				.getResultList();
	}

	public static List<InternalMessages> findUserSent(EntityManager em, String userId) {
		String jpql =
				"SELECT m FROM InternalMessages m " +
				"WHERE m.mailFrom = :userId AND m.deleteType <> 'S'";

		return em.createQuery(jpql, InternalMessages.class)
				.setParameter("userId", userId)
				.getResultList();
	}

	public static long countByUserAndStatus(EntityManager em, String userId, String status) {
		String select =
				"SELECT count(m) FROM InternalMessages m";

		List<String> predicates = new ArrayList<>(2);
		if (userId != null) {
			predicates.add("(m.mailTo = :userId AND m.deleteType <> 'I')");
		}
		if (status != null) {
			predicates.add("(UPPER(m.status) = :status)");
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

		TypedQuery<Long> q = em.createQuery(jpql, Long.class);
		if (userId != null) {
			q.setParameter("mailTo", userId);
		}
		if (status != null) {
			q.setParameter("status", status.toUpperCase());
		}

		return q.getSingleResult();
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

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMailFrom() {
		return this.mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getMailTo() {
		return this.mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public Long getDateSent() {
		return this.dateSent;
	}

	public void setDateSent(Long dateSent) {
		this.dateSent = dateSent;
	}

	public Long getDateReceived() {
		return this.dateReceived;
	}

	public void setDateReceived(Long dateReceived) {
		this.dateReceived = dateReceived;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDeleteType() {
		return this.deleteType;
	}

	public void setDeleteType(String deleteType) {
		this.deleteType = deleteType;
	}

	public Set<InternalAttachment> getAttachments() {
		return this.attachments;
	}

	public void setAttachments(Set<InternalAttachment> attachments) {
		this.attachments = attachments;
	}

}
