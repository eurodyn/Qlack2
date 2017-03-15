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
package com.eurodyn.qlack2.fuse.mailing.api;

import java.util.List;

import com.eurodyn.qlack2.fuse.mailing.api.dto.EmailDTO;

/**
 * Manage email queue.
 *
 * @author European Dynamics SA.
 *
 */
public interface MailService {

	public static enum EMAIL_STATUS {
		QUEUED, SENT, FAILED, CANCELED
	};

	void queueEmail(EmailDTO dto);

	void queueEmails(List<EmailDTO> dtos);

	void updateStatus(String id, EMAIL_STATUS status);

	void deleteFromQueue(String emailId);

	void cleanup(Long date, EMAIL_STATUS[] status);

}
