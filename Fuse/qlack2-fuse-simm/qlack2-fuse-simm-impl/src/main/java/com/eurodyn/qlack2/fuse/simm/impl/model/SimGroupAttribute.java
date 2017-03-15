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

package com.eurodyn.qlack2.fuse.simm.impl.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * SimGroup attributes
 */
@Entity
@Table(name = "sim_group_attributes")
public class SimGroupAttribute implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id;

	@Version
	private long dbversion;

	@Lob
	private byte[] bindata;

	@Column(name = "content_type")
	private String contentType;

	// bi-directional many-to-one association to Group
	@ManyToOne
	@JoinColumn(name = "group_id")
	private SimGroup group;

	private String data;

	private String name;
	
	public SimGroupAttribute() {
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getDbversion() {
		return dbversion;
	}

	public void setDbversion(long dbversion) {
		this.dbversion = dbversion;
	}

	public byte[] getBindata() {
		return bindata;
	}

	public void setBindata(byte[] bindata) {
		this.bindata = bindata;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public SimGroup getGroup() {
		return group;
	}

	public void setGroup(SimGroup group) {
		this.group = group;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Finds group attribute with the given groupId
	 * 
	 * @param groupId
	 * @param em
	 * @return
	 */
	public static SimGroupAttribute findByGroupIdAndName(String groupId, String name, EntityManager em) {
		QSimGroupAttribute qgroupAttribute = QSimGroupAttribute.simGroupAttribute;
		SimGroupAttribute groupAttribute = new JPAQueryFactory(em).selectFrom(qgroupAttribute)
				.where(qgroupAttribute.group.id.eq(groupId).and(qgroupAttribute.name.eq(name))).fetchOne();
		return groupAttribute;
	}
	
}

