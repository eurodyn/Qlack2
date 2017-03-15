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

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "aaa_application")
public class Application {
	@Id
	private String id;
	@Version
	private long dbversion;
	@Column(name = "symbolic_name")
	private String symbolicName;
	private String checksum;
	@Column(name = "executed_on")
	private long executedOn;
	
	public Application() {
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public long getExecutedOn() {
		return executedOn;
	}

	public void setExecutedOn(long executedOn) {
		this.executedOn = executedOn;
	}

	public static Application findBySymbolicName(String symbolicName,
			EntityManager em) {
		Query query = em.createQuery("SELECT a FROM Application a WHERE a.symbolicName = :name");
		query.setParameter("name", symbolicName);
		List<Application> queryResult = query.getResultList();
		if (queryResult.isEmpty()) {
			return null;
		}
		return queryResult.get(0);
	}

}
