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
package com.eurodyn.qlack2.fuse.idm.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.crypto.api.CryptoService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.request.AuthenticateRequest;
import com.eurodyn.qlack2.fuse.idm.api.request.AuthenticateSSORequest;
import com.eurodyn.qlack2.fuse.idm.api.request.ValidateTicketRequest;
import com.eurodyn.qlack2.fuse.idm.api.response.AuthenticateResponse;
import com.eurodyn.qlack2.fuse.idm.api.response.ValidateTicketResponse;
import com.eurodyn.qlack2.fuse.idm.api.signing.Signed;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.Ticket;
import com.eurodyn.qlack2.fuse.ticketserver.api.TicketDTO;
import com.eurodyn.qlack2.fuse.ticketserver.api.TicketServerService;

public class IDMServiceImpl implements IDMService {
	private static final Logger LOGGER = Logger.getLogger(IDMServiceImpl.class
			.getName());
	private TicketServerService ticketService;
	private UserService userService;
	private CryptoService cryptoService;
	private Long ticketValidUntil;
	private Long ticketAutoExtendValidUntil;
	private Long ticketAutoExtendDuration;
	private String secret;
	private List<Integer> validUserStatus;

	public void setValidUserStatus(String s) {
		validUserStatus = new ArrayList<>();
		for (String status : s.split(",")) {
			validUserStatus.add(Integer.parseInt(status));
		}
	}

	public Long getTicketValidUntil() {
		return ticketValidUntil;
	}

	public void setTicketValidUntil(Long ticketValidUntil) {
		this.ticketValidUntil = ticketValidUntil;
	}

	public Long getTicketAutoExtendValidUntil() {
		return ticketAutoExtendValidUntil;
	}

	public void setTicketAutoExtendValidUntil(Long ticketAutoExtendValidUntil) {
		this.ticketAutoExtendValidUntil = ticketAutoExtendValidUntil;
	}

	public Long getTicketAutoExtendDuration() {
		return ticketAutoExtendDuration;
	}

	public void setTicketAutoExtendDuration(Long ticketAutoExtendDuration) {
		this.ticketAutoExtendDuration = ticketAutoExtendDuration;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public void setTicketService(TicketServerService ticketService) {
		this.ticketService = ticketService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setCryptoService(CryptoService cryptoService) {
		this.cryptoService = cryptoService;
	}

	private AuthenticateResponse authenticate(String username, String password,
			boolean isSSO) {
		LOGGER.log(Level.FINE, "Requesting authentication for {0} [SSO={1}].",
				new Object[] { username, isSSO });
		AuthenticateResponse retVal = new AuthenticateResponse();

		if (StringUtils.isEmpty(username)) {
			return retVal;
		}
		
		String userID;		
		// Skip credentials verification if this is an SSO login.
		if (!isSSO) {
			// Do not try to authenticate users with empty credentials.
			if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
				LOGGER.log(Level.FINE, "User could not be authenticated via"
						+ " username and password [username or password was"
						+ "empty].", username);
				return retVal;
			}
	
			// Check if the user can be authenticated.
			userID = userService.canAuthenticate(username, password);
			if (userID == null) {
				LOGGER.log(Level.FINE, "User {0} could not be authenticated via"
						+ " username and password [credentials did not match].",
						username);
				return retVal;
			}
			// Check if the status of the user implies an active user.
			int userStatus = userService.getUserById(userID).getStatus();
			if (!validUserStatus.contains(userStatus)) {
				retVal.setActive(false);
				retVal.setStatus(userStatus);
				return retVal;
			}
		} else {
			// The AuthenticateResponse will actually be just a wrapper around the 
			// SignedTicket. The SignedTicket contains a reference to the user ID 
			// (i.e. a UUID) that was successfully authenticated. Since on an SSO 
			// authentication we do not call AAA's canAuthenticate (therefore we do 
			// not get such a user ID) we setup one manually here (which is the
			// username that SSO authentication took place with).
			userID = username;
		}

		// Generate a ticket for the just authenticated user.
		SignedTicket signedTicket = null;
		try {
			TicketDTO ticketDTO = new TicketDTO();
			ticketDTO.setCreatedBy("QLACK IDM");
			ticketDTO.setPayload(username);
			if (ticketValidUntil != null && ticketValidUntil > 0) {
				ticketDTO.setValidUntil(DateTime.now().getMillis()
						+ ticketValidUntil);
			}
			if (ticketAutoExtendValidUntil != null
					&& ticketAutoExtendValidUntil > 0) {
				ticketDTO.setAutoExtendValidUntil(DateTime.now().getMillis()
						+ ticketAutoExtendValidUntil);
			}
			if (ticketAutoExtendDuration != null
					&& ticketAutoExtendDuration > 0) {
				ticketDTO.setAutoExtendDuration(ticketAutoExtendDuration);
			}
			String ticketID = ticketService.createTicket(ticketDTO);

			if (ticketID != null) {
				signedTicket = new SignedTicket();
				signedTicket.setTicketID(ticketID);
				signedTicket.setUserID(userID);
				signedTicket.setUsername(username);
				signedTicket.setValidUntil(ticketDTO.getValidUntil());
				signedTicket.setAutoExtendDuration(ticketDTO
						.getAutoExtendDuration());
				signedTicket.setAutoExtendValidUntil(ticketDTO
						.getAutoExtendValidUntil());
				signedTicket.setSignature(generateSignature(signedTicket));
			}
		} catch (InvalidKeyException | NoSuchAlgorithmException
				| IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			LOGGER.log(Level.SEVERE, MessageFormat.format(
					"Could not create a ticket for user {0}.", username), e);
		}

		return new AuthenticateResponse(signedTicket);
	}

	@Override
	public AuthenticateResponse authenticate(AuthenticateRequest req) {
		return authenticate(req.getUsername(), req.getPassword(), false);
	}

	@Override
	public AuthenticateResponse authenticate(AuthenticateSSORequest req) {
		return authenticate(req.getUsername(), null, true);
	}

	@Override
	public ValidateTicketResponse validateTicket(ValidateTicketRequest req) {
		SignedTicket ticket = req.getSignedTicket();
		// Do not attempt to validate empty tickets or tickets missing
		// information.
		if ((ticket == null)
				|| (StringUtils.isBlank(ticket.getTicketID()) || StringUtils
						.isBlank(ticket.getSignature()))) {
			return new ValidateTicketResponse(false);
		}

		// Validate the HMAC.
		boolean retVal = false;
		try {
			if (!validateSignature(ticket)) {
				LOGGER.log(Level.FINE,
						"Could not verify the signature of ticket {0}.",
						ticket.getTicketID());
			} else {
				if (ticketService.getTicket(ticket.getTicketID()) == null) {
					LOGGER.log(Level.FINE, "Could not find ticket {0} in "
							+ "Ticket Server.", ticket.getTicketID());
				} else {
					retVal = ticketService.isValid(ticket.getTicketID());
					if (!retVal) {
						LOGGER.log(
								Level.FINE,
								"Authenticity verification for ticket"
										+ "{0} passed but the ticket was invalid or "
										+ "has been revoked.",
								ticket.getTicketID());
					}
				}
			}
		} catch (InvalidKeyException | NoSuchAlgorithmException
				| IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			LOGGER.log(Level.SEVERE, MessageFormat.format("There was an error "
					+ "verifying the authenticity of the ticket {0}",
					ticket.getTicketID()), e);
		}

		return new ValidateTicketResponse(retVal);
	}

	/**
	 * Returns an alphabetically sorted list of the Fields of the class
	 * {@link Ticket} which are annotated with the {@link Signed} annotation.
	 *
	 * @return
	 */
	private List<Field> sortedSignedFields() {
		// Iterate through the fields of the Ticket class to find
		// which ones should participate in the signing process.
		List<Field> signatureParticipants = new ArrayList<>();
		for (Field f : Ticket.class.getDeclaredFields()) {
			if (f.isAnnotationPresent(Signed.class)) {
				signatureParticipants.add(f);
			}
		}

		// Sort the list of fields alphabetically.
		Collections.sort(signatureParticipants, new SignatureFieldComparator());

		return signatureParticipants;
	}

	private boolean validateSignature(SignedTicket t)
			throws NoSuchAlgorithmException, InvalidKeyException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		return generateSignature(t).equals(t.getSignature());
	}

	// TODO cache lookup of annotations.
	private String generateSignature(SignedTicket t)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, InvalidKeyException,
			NoSuchAlgorithmException {
		// Generate the signature.
		StringBuilder bodyToSign = new StringBuilder();
		for (Field f : sortedSignedFields()) {
			bodyToSign.append(PropertyUtils.getProperty(t, f.getName()));
		}

		// Sign the resulting text.
		return cryptoService.hmacSha256(secret, bodyToSign.toString(),
				Charset.forName("UTF-8"));
	}

	private class SignatureFieldComparator implements Comparator<Field> {
		@Override
		public int compare(Field f1, Field f2) {
			return f1.getName().compareTo(f2.getName());
		}
	}
}
