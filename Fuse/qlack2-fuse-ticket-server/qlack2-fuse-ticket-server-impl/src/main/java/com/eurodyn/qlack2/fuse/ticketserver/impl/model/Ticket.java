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
package com.eurodyn.qlack2.fuse.ticketserver.impl.model;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ts_ticket")
public class Ticket implements Serializable {
	private static final long serialVersionUID = -5040741364234631616L;
	@Id
	private String id;
	
	@Column(name = "created_at")
	private long createdAt;
	
	@Column(name = "last_modified_at")
	private Long lastModifiedAt;
	
	@Column(name = "revoked")
	private boolean revoked;
	
	@Column(name = "payload")
	private String payload;

	@Column(name = "valid_until")
	private Long validUntil;

	@Column(name = "auto_extend_until")
	private Long autoExtendUntil;

	@Column(name = "auto_extend_duration")
	private Long autoExtendDuration;

	@Column(name = "created_by")
	private String createdBy;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public Long getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Long lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public Long getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(Long validUntil) {
		this.validUntil = validUntil;
	}

	public Long getAutoExtendUntil() {
		return autoExtendUntil;
	}

	public void setAutoExtendUntil(Long autoExtendUntil) {
		this.autoExtendUntil = autoExtendUntil;
	}

	public Long getAutoExtendDuration() {
		return autoExtendDuration;
	}

	public void setAutoExtendDuration(Long autoExtendDuration) {
		this.autoExtendDuration = autoExtendDuration;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}


}
