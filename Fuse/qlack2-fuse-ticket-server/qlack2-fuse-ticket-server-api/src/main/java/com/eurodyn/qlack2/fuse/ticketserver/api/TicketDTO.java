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
package com.eurodyn.qlack2.fuse.ticketserver.api;


public class TicketDTO {
	// The Id of this ticket.
	private String id;
	// An EPOCH value indicating the creation date of the ticket.
	private long createdAt;
	// An EPOCH value indicating when was this ticket last updated.
	private Long lastModifiedAt;
	// Indicates whether the ticket has been revoked.
	private boolean revoked;
	// An arbitrary payload to include with the ticket. Use this field to 
	// include any additional data you require to accompany your ticket.
	private String payload;
	// The EPOCH until which the ticket is valid. Leave this empty for tickets
	// which are valid indefinitely.
	private Long validUntil;
	// An EPOCH value indicating the maximum date a ticket can be extended to.
	// Leave this value empty for tickets that can be auto-extended forever.
	// Please read the documentation of the Ticket Server to understand how 
	// (and more importantly when) tickets are actually extended before 
	// using this functionality.
	private Long autoExtendValidUntil;
	// Specifies the auto-extend interval (in msec), i.e. for how long is a 
	// ticket auto-extended.
	private Long autoExtendDuration;
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
	public Long getAutoExtendValidUntil() {
		return autoExtendValidUntil;
	}
	public void setAutoExtendValidUntil(Long autoExtendValidUntil) {
		this.autoExtendValidUntil = autoExtendValidUntil;
	}
	public Long getAutoExtendDuration() {
		return autoExtendDuration;
	}
	public void setAutoExtendDuration(Long autoExtendDuration) {
		this.autoExtendDuration= autoExtendDuration;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((autoExtendDuration == null) ? 0 : autoExtendDuration
						.hashCode());
		result = prime
				* result
				+ ((autoExtendValidUntil == null) ? 0 : autoExtendValidUntil
						.hashCode());
		result = prime * result + (int) (createdAt ^ (createdAt >>> 32));
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((lastModifiedAt == null) ? 0 : lastModifiedAt.hashCode());
		result = prime * result + ((payload == null) ? 0 : payload.hashCode());
		result = prime * result + (revoked ? 1231 : 1237);
		result = prime * result
				+ ((validUntil == null) ? 0 : validUntil.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TicketDTO other = (TicketDTO) obj;
		if (autoExtendDuration == null) {
			if (other.autoExtendDuration != null)
				return false;
		} else if (!autoExtendDuration.equals(other.autoExtendDuration))
			return false;
		if (autoExtendValidUntil == null) {
			if (other.autoExtendValidUntil != null)
				return false;
		} else if (!autoExtendValidUntil.equals(other.autoExtendValidUntil))
			return false;
		if (createdAt != other.createdAt)
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastModifiedAt == null) {
			if (other.lastModifiedAt != null)
				return false;
		} else if (!lastModifiedAt.equals(other.lastModifiedAt))
			return false;
		if (payload == null) {
			if (other.payload != null)
				return false;
		} else if (!payload.equals(other.payload))
			return false;
		if (revoked != other.revoked)
			return false;
		if (validUntil == null) {
			if (other.validUntil != null)
				return false;
		} else if (!validUntil.equals(other.validUntil))
			return false;
		return true;
	}
}
