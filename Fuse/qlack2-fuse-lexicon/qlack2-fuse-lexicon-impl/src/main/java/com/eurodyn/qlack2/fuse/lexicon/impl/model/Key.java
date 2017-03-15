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
package com.eurodyn.qlack2.fuse.lexicon.impl.model;

import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "lex_key")
public class Key {
	@Id
	private String id;
	@Version
	private long dbversion;
	private String name;
	@ManyToOne
	@JoinColumn(name = "group_id")
	private Group group;
	@OneToMany(mappedBy="key")
	private List<Data> data;

	public Key() {
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public List<Data> getData() {
		return data;
	}

	public void setData(List<Data> data) {
		this.data = data;
	}
	
	public static Key find(String keyId, EntityManager em) {
		return em.find(Key.class, keyId);
	}
	
	public static Key findByName(String keyName, String groupId, EntityManager em) {
		Query query = null;
		if (groupId != null) {
			query = em.createQuery("SELECT k FROM Key k WHERE k.name = :name AND k.group.id = :groupId");
			query.setParameter("name", keyName);
			query.setParameter("groupId", groupId);
		}
		else {
			query = em.createQuery("SELECT k FROM Key k WHERE k.name = :name AND k.group IS NULL");
			query.setParameter("name", keyName);
		}
		List<Key> queryResult = query.getResultList();
		if (queryResult.isEmpty()) {
			return null;
		}
		return queryResult.get(0);
	}

	public static List<Key> getAllKeys(EntityManager em) {
		Query query = em.createQuery("SELECT k FROM Key k");
		return query.getResultList();
	}

}
