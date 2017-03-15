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
package com.eurodyn.qlack2.fuse.auditing.impl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "al_audit")
public class Audit {

	@Id
	private String id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "level_id")
	private AuditLevel levelId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trace_id")
	private AuditTrace traceId;
	@Column(name = "prin_session_id")
	private String prinSessionId;
	@Column(name = "short_description")
	private String shortDescription;
	@Column(name = "event")
	private String event;
	@Column(name = "created_on")
	private Long createdOn;
	@Column(name = "reference_id")
	private String referenceId;
	@Column(name = "group_name")
	private String groupName;

	public Audit() {
		id = java.util.UUID.randomUUID().toString();
	}
	
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AuditLevel getLevelId() {
		return this.levelId;
	}

	public void setLevelId(AuditLevel levelId) {
		this.levelId = levelId;
	}

	public AuditTrace getTraceId() {
		return this.traceId;
	}

	public void setTraceId(AuditTrace traceId) {
		this.traceId = traceId;
	}

	public String getPrinSessionId() {
		return this.prinSessionId;
	}

	public void setPrinSessionId(String prinSessionId) {
		this.prinSessionId = prinSessionId;
	}

	public String getShortDescription() {
		return this.shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getEvent() {
		return this.event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public Long getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Long createdOn) {
		this.createdOn = createdOn;
	}

	public String getReferenceId() {
		return this.referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
