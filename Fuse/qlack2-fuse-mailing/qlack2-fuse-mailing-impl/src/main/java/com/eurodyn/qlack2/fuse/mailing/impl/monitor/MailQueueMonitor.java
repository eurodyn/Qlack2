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
package com.eurodyn.qlack2.fuse.mailing.impl.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import com.eurodyn.qlack2.fuse.mailing.api.MailService;
import com.eurodyn.qlack2.fuse.mailing.api.dto.AttachmentDTO;
import com.eurodyn.qlack2.fuse.mailing.api.dto.EmailDTO;
import com.eurodyn.qlack2.fuse.mailing.impl.model.Attachment;
import com.eurodyn.qlack2.fuse.mailing.impl.model.Email;
import com.eurodyn.qlack2.fuse.mailing.impl.util.ConverterUtil;

/**
 * Monitor email queue.
 *
 * The JavaMail API does not support transaction management. Discussed solutions
 * on the Internet are based on the idea of decoupling the actual sending of the
 * email from the ongoing transaction. Mentioned solutions include using a JMS
 * queue, an EJB timer with timeout 0, or the 'afterCommit' callback of
 * transaction synchronizations.
 *
 * Our solution concides with the above direction. The client transaction just
 * writes an entry in the database table and another thread just reads and
 * updates each entry along with sending the emails.
 *
 * In case the 'send email' action fails, the whole transaction is rollbacked.
 * In case the database operation fails at flush and commit time (after this
 * method completes), we send an email twice which we consider acceptable.
 *
 * There is an Oracle CM product which seems to has support for applying
 * transaction management to messaging operations.
 *
 * @author European Dynamics SA
 */
@Transactional
public class MailQueueMonitor {
	private static final Logger LOGGER = Logger.getLogger(MailQueueMonitor.class.getName());
	@PersistenceContext(unitName = "fuse-mailing")
	private EntityManager em;
	
	private byte maxTries;

//	private TransactionManager tm;

	private MailQueueSender sender;

	public void setMaxTries(byte maxTries) {
		this.maxTries = maxTries;
	}

//	public void setEm(EntityManager em) {
//		this.em = em;
//	}

//	public void setTm(TransactionManager tm) {
//		this.tm = tm;
//	}

	public void setSender(MailQueueSender sender) {
		this.sender = sender;
	}

	/**
	 * Check for QUEUED emails and send them.
	 */
	public void checkAndSendQueued() {
		//logTransactionalContext();

		List<Email> emails = Email.findQueued(em, maxTries);
		LOGGER.log(Level.FINEST, "Found {0} email(s) to be sent.", emails.size());

		for (Email email : emails) {
			email.setTries((byte) (email.getTries() + 1));
			email.setDateSent(System.currentTimeMillis());

			// Create a DTO for this email.
			EmailDTO dto = new EmailDTO();
			dto.setId(email.getId());
			dto.setSubject(email.getSubject());
			dto.setBody(email.getBody());
			dto.setFrom(email.getFromEmail());
			if (email.getToEmails() != null) {
				dto.setToContact(ConverterUtil.createRecepientlist(email.getToEmails()));
			}
			if (email.getCcEmails() != null) {
				dto.setCcContact(ConverterUtil.createRecepientlist(email.getCcEmails()));
			}
			if (email.getBccEmails() != null) {
				dto.setBccContact(ConverterUtil.createRecepientlist(email.getBccEmails()));
			}
			if (email.getEmailType().equals("HTML")) {
				dto.setEmailType(EmailDTO.EMAIL_TYPE.HTML);
			}
			else {
				dto.setEmailType(EmailDTO.EMAIL_TYPE.TEXT);
			}

			// Process attachments.
			Set<Attachment> attachments = email.getAttachments();

			List<AttachmentDTO> attachmentList = new ArrayList<>();
			for (Attachment attachment : attachments) {
				AttachmentDTO attachmentDTO = new AttachmentDTO();
				attachmentDTO.setContentType(attachment.getContentType());
				attachmentDTO.setData(attachment.getData());
				attachmentDTO.setFilename(attachment.getFilename());
				attachmentList.add(attachmentDTO);
			}
			dto.setAttachments(attachmentList);

			try {
				sender.send(dto);

				email.setStatus(MailService.EMAIL_STATUS.SENT.toString());
			}
			catch (Exception ex) {
				LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
				if (email.getTries() >= maxTries) {
					email.setStatus(MailService.EMAIL_STATUS.FAILED.toString());

					Throwable t = ex.getCause() != null ? ex.getCause() : ex;
					email.setServerResponse(t.getLocalizedMessage());
					email.setServerResponseDate(System.currentTimeMillis());
				}
			}
		}
	}

//	private void logTransactionalContext() {
//		try {
//			Transaction tx = tm.getTransaction();
//			LOGGER.log(Level.FINEST, "Current transaction: {0}", tx);
//		}
//		catch (SystemException e) {
//			LOGGER.log(Level.WARNING, "Cannot get current transaction", e);
//		}
//	}

}
