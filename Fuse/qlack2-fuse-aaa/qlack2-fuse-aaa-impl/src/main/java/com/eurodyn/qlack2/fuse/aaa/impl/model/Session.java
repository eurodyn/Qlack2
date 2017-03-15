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
package com.eurodyn.qlack2.fuse.aaa.impl.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the aaa_session database table.
 *
 */
@Entity
@Table(name="aaa_session")
public class Session implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	
	@Version
	private long dbversion;

	@Column(name="application_session_id")
	private String applicationSessionId;

	@Column(name="created_on")
	private long createdOn;

	@Column(name="terminated_on")
	private Long terminatedOn;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	//bi-directional many-to-one association to SessionAttribute
	@OneToMany(mappedBy="session")
	private List<SessionAttribute> sessionAttributes;

	public Session() {
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApplicationSessionId() {
		return this.applicationSessionId;
	}

	public void setApplicationSessionId(String applicationSessionId) {
		this.applicationSessionId = applicationSessionId;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public Long getTerminatedOn() {
		return terminatedOn;
	}

	public void setTerminatedOn(Long terminatedOn) {
		this.terminatedOn = terminatedOn;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<SessionAttribute> getSessionAttributes() {
		return this.sessionAttributes;
	}

	public void setSessionAttributes(List<SessionAttribute> sessionAttributes) {
		this.sessionAttributes = sessionAttributes;
	}
	
	public static Session find(String sessionID, EntityManager em) {
		return em.find(Session.class, sessionID);
	}
	
	public static SessionAttribute findAttribute(String sessionId, String attributeName, EntityManager em) {
		SessionAttribute retVal = null;
        Query query = em.createQuery("SELECT a FROM SessionAttribute a "
                + "WHERE a.session.id = :id AND a.name = :name");
        query.setParameter("id", sessionId);
        query.setParameter("name", attributeName);
        List<SessionAttribute> l = query.getResultList();
        if (!l.isEmpty()) {
            retVal = (SessionAttribute) l.get(0);
        }

        return retVal;
	}

}