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

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;


/**
 * The persistent class for the aaa_user_attributes database table.
 *
 */
@Entity
@Table(name="aaa_user_attributes")
public class UserAttribute implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Version
	private long dbversion;

	private byte[] bindata;

	@Column(name="content_type")
	private String contentType;

	private String data;

	private String name;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	public UserAttribute() {
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte[] getBindata() {
		return this.bindata;
	}

	public void setBindata(byte[] bindata) {
		this.bindata = bindata;
	}

	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getData() {
		return this.data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

  public long getDbversion() {
    return dbversion;
  }

  public void setDbversion(long dbversion) {
    this.dbversion = dbversion;
  }
}