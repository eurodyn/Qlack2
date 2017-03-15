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
package com.eurodyn.qlack2.fuse.auditing.api.dto;

import java.io.Serializable;
import java.util.Date;

public class AuditLevelDTO implements Serializable {
	private static final long serialVersionUID = -1759498275639776136L;
	private String name;
	private String id;
	private String description;
	private String prinSessionId;
	private Date createdOn;

	public enum DefaultLevels {
		trace, debug, info, warn, error, fatal
	}

	/**
	 * Default constructor
	 */
	public AuditLevelDTO() {

	}

	/**
	 * parameterized Constructor
	 *
	 * @param name
	 */
	public AuditLevelDTO(String name) {
		this.setName(name);
	}

	/**
	 * Get the audit level description
	 *
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the audit level description
	 *
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * get id
	 *
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * set the id
	 *
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * get name
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * set the name
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * get principal session id
	 *
	 * @return
	 */
	public String getPrinSessionId() {
		return prinSessionId;
	}

	/**
	 * set principal id
	 *
	 * @param prinSessionId
	 */
	public void setPrinSessionId(String prinSessionId) {
		this.prinSessionId = prinSessionId;
	}

	/**
	 * Get the creation date of the level
	 *
	 * @return
	 */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * Set the creation date of the level
	 *
	 * @param createdOn
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
}
