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
package com.eurodyn.qlack2.fuse.auditclient.impl;

import java.util.HashMap;

import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.auditing.api.AuditLoggingService;
import com.eurodyn.qlack2.fuse.auditing.api.Constants;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLogDTO;
import com.eurodyn.qlack2.fuse.eventpublisher.api.EventPublisherService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuditClientServiceImpl implements AuditClientService {
	private boolean synchronous;
	private boolean enabled;
	private boolean traceEnabled;
	private AuditLoggingService auditLoggingService;
	private EventPublisherService eventPublisherService;
	private static final ObjectMapper mapper = new ObjectMapper();

	public void setSynchronous(boolean synchronous) {
		this.synchronous = synchronous;
	}

	public void setEventPublisherService(
			EventPublisherService eventPublisherService) {
		this.eventPublisherService = eventPublisherService;
	}

	public void setAuditLoggingService(AuditLoggingService auditLoggingService) {
		this.auditLoggingService = auditLoggingService;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setTraceEnabled(boolean traceEnabled) {
		this.traceEnabled = traceEnabled;
	}

	@SuppressWarnings("serial")
	@Override
	public String audit(final AuditLogDTO dto) {
		String auditId = null;
		if (enabled) {
			if (synchronous) {
				auditId = auditLoggingService.logAudit(dto);
			} else {
				eventPublisherService.publishAsync(new HashMap<String, Object>() {
					{
						put(Constants.EVENT_ADMIN_DTO_PROPERTY, dto);
					}
				}, Constants.EVENT_ADMIN_TOPIC);
			}
		}
		return auditId;
	}

	@Override
	public void audit(String level, String event, String groupName,
			String description, String sessionID, Object traceData) {
		String traceDataStr = "";
		if (traceData != null) {
			try {
				traceDataStr = mapper.writeValueAsString(traceData);
			} catch (Exception e) {
				traceDataStr = e.getLocalizedMessage();
			}
		}
		audit(level, event, groupName, description, 
				sessionID, traceDataStr);
	}

	@Override
	public String audit(String level, String event, String groupName, String description,
			String sessionID, Object traceData, String referenceId){
		AuditLogDTO dto = new AuditLogDTO();
		dto.setLevel(level);
		dto.setEvent(event);
		dto.setGroupName(groupName);
		dto.setShortDescription(description);
		dto.setPrinSessionId(sessionID);
		dto.setReferenceId(referenceId);
		if (traceEnabled) {
			String traceDataStr = "";
			if (traceData != null) {
				try {
					traceDataStr = mapper.writeValueAsString(traceData);
				} catch (Exception e) {
					traceDataStr = e.getLocalizedMessage();
				}
			}
			dto.setTraceData(traceDataStr);
		}
		return audit(dto);
	}

	@Override
	public void audit(String level, String event, String groupName,
			String description, String sessionID, String traceData) {
		AuditLogDTO dto = new AuditLogDTO();
		dto.setLevel(level);
		dto.setEvent(event);
		dto.setGroupName(groupName);
		dto.setShortDescription(description);
		dto.setPrinSessionId(sessionID);
		if (traceEnabled) {
			dto.setTraceData(traceData);
		}
		audit(dto);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
