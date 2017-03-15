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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "mai_distribution_list")
public class DistributionList implements java.io.Serializable {
	private static final long serialVersionUID = 9030420428317901264L;

	@Id
	private String id;

	@Column(name = "list_name", nullable = false, length = 45)
	private String name;

	@Column(name = "description", length = 45)
	private String description;

	@Column(name = "created_by", length = 254)
	private String createdBy;

	@Column(name = "created_on")
	private Long createdOn;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "mai_distr_list_has_contact",
		joinColumns = {
			@JoinColumn(name = "distribution_list_id", nullable = false, updatable = false)
		},
		inverseJoinColumns = {
			@JoinColumn(name = "contact_id", nullable = false, updatable = false)
		}
	)
	private Set<Contact> contacts = new HashSet<Contact>(0);

	// -- Constructors

	public DistributionList() {
		this.id = java.util.UUID.randomUUID().toString();
	}

	// -- Queries

	public static List<DistributionList> findAll(EntityManager em) {
		String jpql = "SELECT dl FROM DistributionList dl";

		return em.createQuery(jpql, DistributionList.class).getResultList();
	}

	public static List<DistributionList> findByName(EntityManager em, String name) {
		String jpql = "SELECT dl FROM DistributionList dl WHERE dl.name = :name";

		return em.createQuery(jpql, DistributionList.class).setParameter("name", name).getResultList();
	}

	// -- Accessors

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

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Long getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Long createdOn) {
		this.createdOn = createdOn;
	}

	public Set<Contact> getContacts() {
		return this.contacts;
	}

	public void setContacts(Set<Contact> contacts) {
		this.contacts = contacts;
	}

}
