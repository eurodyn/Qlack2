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
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuditLogDTO implements Serializable {
	private static final long serialVersionUID = 7619419710921035617L;
	private String id;
	private String level;
	private Date createdOn;
	private String prinSessionId;
	private String shortDescription;
	private String event;
	private String traceData;
	private String referenceId;
	private String groupName;

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getPrinSessionId() {
		return prinSessionId;
	}

	public void setPrinSessionId(String prinSessionId) {
		this.prinSessionId = prinSessionId;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		if ((shortDescription != null) && (shortDescription.length() > 2048)) {
			Logger.getLogger(AuditLogDTO.class.getName()).log(
					Level.WARNING,
					"shortDescription value "
							+ "was truncated to 2048 characters");
			shortDescription = shortDescription.substring(0, 2047);
		}
		this.shortDescription = shortDescription;
	}

	public String getTraceData() {
		return traceData;
	}

	public void setTraceData(String traceData) {
		this.traceData = traceData;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
