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
package com.eurodyn.qlack2.fuse.auditing.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.transaction.Transactional;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.eurodyn.qlack2.fuse.auditing.api.AuditLoggingService;
import com.eurodyn.qlack2.fuse.auditing.api.Constants;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLogDTO;

public class AuditEventListener implements EventHandler {
	private static final Logger LOGGER = Logger
			.getLogger(AuditEventListener.class.getName());

	private AuditLoggingService auditLoggingService;

	public void setAuditLoggingService(AuditLoggingService auditLoggingService) {
		this.auditLoggingService = auditLoggingService;
	}

	@Override
	public void handleEvent(Event event) {
		LOGGER.log(Level.FINE, "Got an auditing event via Event Admin.");
		AuditLogDTO auditLogDTO = (AuditLogDTO) event
				.getProperty(Constants.EVENT_ADMIN_DTO_PROPERTY);
		auditLoggingService.logAudit(auditLogDTO);
	}

}
