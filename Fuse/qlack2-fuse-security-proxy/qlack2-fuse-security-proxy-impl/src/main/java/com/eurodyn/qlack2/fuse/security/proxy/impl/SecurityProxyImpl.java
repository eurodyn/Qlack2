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
package com.eurodyn.qlack2.fuse.security.proxy.impl;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import com.eurodyn.qlack2.fuse.aaa.api.OperationService;
import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;
import com.eurodyn.qlack2.fuse.idm.api.request.AuthenticateRequest;
import com.eurodyn.qlack2.fuse.idm.api.request.ValidateTicketRequest;
import com.eurodyn.qlack2.fuse.idm.api.response.AuthenticateResponse;
import com.eurodyn.qlack2.fuse.idm.api.response.ValidateTicketResponse;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.fuse.security.proxy.api.SecurityProxy;
import com.eurodyn.qlack2.fuse.security.proxy.api.dto.CheckPermissionRDTO;

/**
 * A REST proxy to the IDM service. Do not forget to properly configure it in
 * your blueprint by settings the IDMService as well as the ticketHeaderName.
 *
 * @author European Dynamics SA
 */

public class SecurityProxyImpl implements SecurityProxy {
	private static final Logger LOGGER = Logger.getLogger(SecurityProxy.class
			.getName());
	private IDMService idm;
	private OperationService operationService;
	private UserService userService;
	private String ticketHeaderName;

	public void setIdm(IDMService idm) {
		this.idm = idm;
	}

	public void setOperationService(OperationService operationService) {
		this.operationService = operationService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public String getTicketHeaderName() {
		return ticketHeaderName;
	}

	public void setTicketHeaderName(String ticketHeaderName) {
		this.ticketHeaderName = ticketHeaderName;
	}

	@Override
	public AuthenticateResponse authenticate(AuthenticateRequest req) {
		return idm.authenticate(req);
	}

	@Override
	public Set<String> genericPermissions(@Context HttpHeaders headers) {

		LOGGER.log(Level.FINE, "Getting generic permissions for user {0}");
		// Since IDM is a QLACK Fuse component it expects security to have
		// been enforced by its caller. Therefore, we have to check the validity
		// of the ticket before we check the allowed operations.
		SignedTicket signedTicket = getValidTicketFromHeaders(headers);

		if (signedTicket != null) {
			return operationService.getPermittedOperationsForUser(
					signedTicket.getUserID(), true);
		} else {
			throw new QInvalidTicketException(signedTicket);
		}
	}

	@Override
	public Boolean checkPermission(CheckPermissionRDTO rdto,
			@Context HttpHeaders headers) {
		// Since IDM is a QLACK Fuse component it expects security to have
		// been enforced by its caller. Therefore, we have to check the validity
		// of the ticket before we check the allowed operations.
		SignedTicket signedTicket = getValidTicketFromHeaders(headers);
		if (signedTicket != null) {
			return operationService.isPermitted(signedTicket.getUserID(),
					rdto.getPermission(), rdto.getObjectID());
		} else {
			throw new QInvalidTicketException(signedTicket);
		}
	}

	@Override
	public ValidateTicketResponse isTicketValid(ValidateTicketRequest req) {
		return idm.validateTicket(req);
	}

	@Override
	public UserDTO getUserDetails(@Context HttpHeaders headers) {
		// Since IDM is a QLACK Fuse component it expects security to have
		// been enforced by its caller. Therefore, we have to check the validity
		// of the ticket before we check the allowed operations.
		SignedTicket signedTicket = getValidTicketFromHeaders(headers);
		if (signedTicket != null) {
			return userService.getUserById(signedTicket.getUserID());
		} else {
			throw new QInvalidTicketException(signedTicket);
		}
	}

	/**
	 * A helper method to validate a ticket passed on the HTTP Headers of this
	 * request. This method is to be used internally by other methods which
	 * require a security-enabled call to the IDM. In case the ticket was found
	 * to be valid it is returned as SignedTicket; otherwise, a null value is
	 * returned (it is up to the caller of this method to decide whether to
	 * throws a QInvalidTicketException in that case or silently ignore it).
	 *
	 * @param headers
	 */
	private SignedTicket getValidTicketFromHeaders(HttpHeaders headers) {
		SignedTicket retVal = SignedTicket.fromVal(headers.getRequestHeaders()
				.getFirst(ticketHeaderName));
		if (!isTicketValid(new ValidateTicketRequest(retVal)).isValid()) {
			retVal = null;
		}
		return retVal;
	}
}
