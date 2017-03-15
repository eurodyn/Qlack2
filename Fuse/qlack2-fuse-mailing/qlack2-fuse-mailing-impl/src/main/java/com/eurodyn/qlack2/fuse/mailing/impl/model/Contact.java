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
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "mai_contact")
public class Contact implements java.io.Serializable {
	private static final long serialVersionUID = -6946929853243814803L;

	@Id
	private String id;

	@Column(name = "email", nullable = false, length = 45)
	private String email;

	@Column(name = "first_name", length = 254)
	private String firstName;

	@Column(name = "last_name", length = 254)
	private String lastName;

	@Column(name = "locale", length = 5)
	private String locale;

	@Column(name = "user_id", length = 36)
	private String userId;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "mai_distr_list_has_contact",
		joinColumns = {
			@JoinColumn(name = "contact_id", nullable = false, updatable = false)
		},
		inverseJoinColumns = {
			@JoinColumn(name = "distribution_list_id", nullable = false, updatable = false)
		}
	)
	private Set<DistributionList> distributionLists = new HashSet<DistributionList>(0);

	// -- Constructors

	public Contact() {
		this.id = java.util.UUID.randomUUID().toString();
	}

	// -- Accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLocale() {
		return this.locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Set<DistributionList> getDistributionLists() {
		return this.distributionLists;
	}

	public void setDistributionLists(Set<DistributionList> distributionLists) {
		this.distributionLists = distributionLists;
	}

}
