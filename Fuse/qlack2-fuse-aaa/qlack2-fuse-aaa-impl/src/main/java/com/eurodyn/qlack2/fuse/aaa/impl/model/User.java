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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the aaa_user database table.
 *
 */
@Entity
@Table(name="aaa_user")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Version
	private long dbversion;

	@Column(name="pswd")
	private String password;

	private String salt;

	private byte status;

	private String username;

	private boolean superadmin;

	private Boolean external;

	//bi-directional many-to-one association to UserHasOperation
	@OneToMany(mappedBy="user")
	private List<UserHasOperation> userHasOperations;

	//bi-directional many-to-one association to Session
	@OneToMany(mappedBy="user")
	private List<Session> sessions;

	//bi-directional many-to-many association to Group
	@ManyToMany(mappedBy = "users")
	private List<Group> groups;

	//bi-directional many-to-one association to UserAttribute
	@OneToMany(mappedBy="user")
	private List<UserAttribute> userAttributes;

	public User() {
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public byte getStatus() {
		return this.status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<UserHasOperation> getUserHasOperations() {
		return this.userHasOperations;
	}

	public void setUserHasOperations(List<UserHasOperation> userHasOperations) {
		this.userHasOperations = userHasOperations;
	}

	public boolean isSuperadmin() {
		return superadmin;
	}

	public void setSuperadmin(boolean superadmin) {
		this.superadmin = superadmin;
	}

	public Boolean isExternal() {
		return external;
	}

	public void setExternal(Boolean external) {
		this.external = external;
	}

	public UserHasOperation addUserHasOperation(UserHasOperation userHasOperations) {
		if (getUserHasOperations() == null) {
			setUserHasOperations(new ArrayList<UserHasOperation>());
		}
		getUserHasOperations().add(userHasOperations);
		userHasOperations.setUser(this);

		return userHasOperations;
	}

	public UserHasOperation removeUserHasOperation(UserHasOperation userHasOperations) {
		getUserHasOperations().remove(userHasOperations);
		userHasOperations.setUser(null);

		return userHasOperations;
	}

	public List<Session> getSessions() {
		return this.sessions;
	}

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}

	public Session addSession(Session session) {
		getSessions().add(session);
		session.setUser(this);

		return session;
	}

	public Session removeSession(Session session) {
		getSessions().remove(session);
		session.setUser(null);

		return session;
	}

	public List<Group> getGroups() {
		return this.groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<UserAttribute> getUserAttributes() {
		return this.userAttributes;
	}

	public void setUserAttributes(List<UserAttribute> userAttributes) {
		this.userAttributes = userAttributes;
	}

	public UserAttribute addUserAttribute(UserAttribute userAttribute) {
		getUserAttributes().add(userAttribute);
		userAttribute.setUser(this);

		return userAttribute;
	}

	public UserAttribute removeUserAttribute(UserAttribute userAttribute) {
		getUserAttributes().remove(userAttribute);
		userAttribute.setUser(null);

		return userAttribute;
	}

	public static User find(String userID, EntityManager em) {
		return em.find(User.class, userID);
	}

	public static User findByUsername(String username, EntityManager em) {
        Query query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username");
        query.setParameter("username", username);
        List<User> resultList = query.getResultList();

        return resultList.isEmpty() ? null : resultList.get(0);
    }

	public static UserAttribute findAttribute(String userId, String attributeName, EntityManager em) {
        UserAttribute retVal = null;
        Query query = em.createQuery("SELECT a FROM UserAttribute a "
                + "WHERE a.user.id = :id AND a.name = :name");
        query.setParameter("id", userId);
        query.setParameter("name", attributeName);
        List<UserAttribute> l = query.getResultList();
        if (!l.isEmpty()) {
            retVal = l.get(0);
        }

        return retVal;
	}

	public static Set<String> getAllUserIds(EntityManager em) {
		Set<String> retVal = new HashSet<>();
		Query query = em.createQuery("SELECT u.id FROM User u");
		retVal.addAll(query.getResultList());
		return retVal;
	}

	public static Set<String> getNormalUserIds(EntityManager em) {
		Set<String> retVal = new HashSet<>();
		Query query = em.createQuery("SELECT u.id FROM User u WHERE u.superadmin = false");
		retVal.addAll(query.getResultList());
		return retVal;
	}

	public static Set<String> getSuperadminUserIds(EntityManager em) {
		Set<String> retVal = new HashSet<>();
		Query query = em.createQuery("SELECT u.id FROM User u WHERE u.superadmin = true");
		retVal.addAll(query.getResultList());
		return retVal;
	}

}