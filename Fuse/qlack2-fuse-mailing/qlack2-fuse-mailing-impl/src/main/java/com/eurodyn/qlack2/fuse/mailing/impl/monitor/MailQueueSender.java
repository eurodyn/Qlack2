package com.eurodyn.qlack2.fuse.mailing.impl.monitor;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.StringUtils;

import com.eurodyn.qlack2.fuse.mailing.api.dto.AttachmentDTO;
import com.eurodyn.qlack2.fuse.mailing.api.dto.EmailDTO;
import com.eurodyn.qlack2.fuse.mailing.api.exception.QMailingException;

public class MailQueueSender {
	private static final Logger LOGGER = Logger.getLogger(MailQueueSender.class
			.getName());

	private Session session;

	private boolean debug;
	private boolean starttls;

	private String host;
	private int port;

	private String username;
	private String password;

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setStarttls(boolean starttls) {
		this.starttls = starttls;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void init() {
		Properties properties = new Properties();

		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.auth", "false");
		properties.put("mail.smtp.starttls.enable", Boolean.toString(starttls));
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		
		properties.put("mail.debug", Boolean.toString(debug));

		session = Session.getInstance(properties);
	}

	public void destroy() {
	}

	/**
	 * Send the email.
	 * 
	 * @param vo
	 * @throws QMailingException
	 */
	public void send(EmailDTO vo) throws QMailingException {
		/*
		 * We currently create a new transport for each queued email. We may be
		 * better off creating one transport for each batch of queued mails.
		 */
		Transport transport = null;
		try {
			// Create a message
			MimeMessage msg = new MimeMessage(session);

			// Set the From field
			if (StringUtils.isEmpty(vo.getFrom())) {
				msg.setFrom(new InternetAddress(
						"Qlack Mailing <no-reply@qlack.eurodyn.com>"));
			} else {
				msg.setFrom(new InternetAddress(vo.getFrom()));
			}

			// Set the To field. (Multiple addresses can be separated by
			// semicolons)
			List<String> toAddresses = vo.getToContact();
			if (toAddresses != null && !toAddresses.isEmpty()) {
				msg.setRecipients(Message.RecipientType.TO,
						stringListToAddressList(toAddresses));
			}

			// Set the CC field.
			List<String> ccAddresses = vo.getCcContact();
			if (ccAddresses != null && !ccAddresses.isEmpty()) {
				msg.setRecipients(Message.RecipientType.CC,
						stringListToAddressList(ccAddresses));
			}

			// Set the BCC field.
			List<String> bccAddresses = vo.getBccContact();
			if (bccAddresses != null && !bccAddresses.isEmpty()) {
				msg.setRecipients(Message.RecipientType.BCC,
						stringListToAddressList(bccAddresses));
			}

			// Set the subject and date.
			msg.setSubject(vo.getSubject(), "utf-8");
			msg.setSentDate(new Date());

			// Set the content. Need to work in parts in case of attachements.
			if ((vo.getAttachments() != null) && !vo.getAttachments().isEmpty()) {
				Multipart multipart = new MimeMultipart();

				// Part one is the general text of the message.
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				if (vo.getEmailType() == EmailDTO.EMAIL_TYPE.TEXT) {
					messageBodyPart.setText(vo.getBody(), "utf-8");
				} else if (vo.getEmailType() == EmailDTO.EMAIL_TYPE.HTML) {
					messageBodyPart.setContent(vo.getBody(),
							"text/html; charset=utf-8");
				}
				multipart.addBodyPart(messageBodyPart);

				// Part two, three, four, etc. are attachments
				List<AttachmentDTO> attachmentList = vo.getAttachments();
				for (AttachmentDTO attachmentDTO : attachmentList) {
					String filename = attachmentDTO.getFilename();
					byte[] data = attachmentDTO.getData();
					String contentType = attachmentDTO.getContentType();
					if (filename != null && !filename.equals("")
							&& data != null) {
						ByteArrayDataSource mimePartDataSource = new ByteArrayDataSource(
								data, contentType);
						MimeBodyPart attachment = new MimeBodyPart();
						attachment.setDataHandler(new DataHandler(
								mimePartDataSource));
						attachment.setFileName(filename);
						multipart.addBodyPart(attachment);
					}
				}

				// Put parts in message
				msg.setContent(multipart);
			} else { // A message without attachments.
				if (vo.getEmailType() == EmailDTO.EMAIL_TYPE.TEXT) {
					msg.setText(vo.getBody(), "utf-8");
				} else if (vo.getEmailType() == EmailDTO.EMAIL_TYPE.HTML) {
					msg.setContent(vo.getBody(), "text/html;charset=\"UTF-8\"");
				}
			}

			// Initiate the send process.
			transport = session.getTransport();
			if (StringUtils.isBlank(username)) {
				LOGGER.log(Level.FINEST, "Sending email with no auth");
				transport.connect();
			} else {
				LOGGER.log(Level.FINEST, "Sending email with auth, user: " + username);
				transport.connect(host, port, username, password);	
			}

			transport.sendMessage(msg, msg.getAllRecipients());
		} catch (MessagingException ex) {
			throw new QMailingException("Cannot send message", ex);
		} finally {
			try {
				if (transport != null) {
					transport.close();
				}
			} catch (MessagingException ex) {
				LOGGER.log(Level.WARNING, "Unexpected error during close", ex);
			}
		}
	}

	private Address[] stringListToAddressList(List<String> addresses)
			throws AddressException {
		Address[] retVal = new Address[addresses.size()];
		for (int i = 0; i < addresses.size(); i++) {
			retVal[i] = new InternetAddress(addresses.get(i));
		}
		return retVal;
	}

}
