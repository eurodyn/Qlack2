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
package com.eurodyn.qlack2.fuse.auditclient.api;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLogDTO;

public interface AuditClientService {
	String audit(AuditLogDTO dto);
	void audit(String level, String event, String groupName, String description,
			String sessionID, Object traceData);
	String audit(String level, String event, String groupName, String description,
			String sessionID, Object traceData, String referenceId);
	void audit(String level, String event, String groupName, String description,
			String sessionID, String traceData);
	
	/**
	 * Returns whether the audit client is trying to persist records in the database or not.
	 * @return
	 */
	boolean isEnabled();

}
