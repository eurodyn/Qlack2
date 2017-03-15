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
package com.eurodyn.qlack2.fuse.auditing.impl.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;

@Entity
@Table(name = "al_audit_level")
public class AuditLevel {

	@Id
	private String id;
	private String name;
	private String description;
	@Column(name = "prin_session_id")
	private String prinSessionId;
	@Column(name = "created_on")
	private Long createdOn;

	public AuditLevel() {
		id = java.util.UUID.randomUUID().toString();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrinSessionId() {
		return this.prinSessionId;
	}

	public void setPrinSessionId(String prinSessionId) {
		this.prinSessionId = prinSessionId;
	}

	public Long getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Long createdOn) {
		this.createdOn = createdOn;
	}
	
	public static AuditLevel findByName(EntityManager em, String name) {
		Query q = em.createQuery("SELECT l FROM AuditLevel l WHERE l.name = :name");
		q.setParameter("name", name);
		List<AuditLevel> resultList = q.getResultList();
		return q.getResultList().isEmpty() ? null : resultList.get(0);
	}
	
	public static List<AuditLevel> findAll(EntityManager em) {
		Query q = em.createQuery("SELECT l FROM AuditLevel l");
		return q.getResultList();
	}

}
