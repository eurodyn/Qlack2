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
package com.eurodyn.qlack2.fuse.security.proxy.api;

import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.fuse.idm.api.request.AuthenticateRequest;
import com.eurodyn.qlack2.fuse.idm.api.request.ValidateTicketRequest;
import com.eurodyn.qlack2.fuse.idm.api.response.AuthenticateResponse;
import com.eurodyn.qlack2.fuse.idm.api.response.ValidateTicketResponse;
import com.eurodyn.qlack2.fuse.security.proxy.api.dto.CheckPermissionRDTO;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Set;

@CrossOriginResourceSharing(allowAllOrigins = true)
@Path("/security-proxy")
public interface SecurityProxy {

	/**
	 * Given a set of username/password pair it tries to authenticate the user
	 * using IDM.
	 *
	 * @param req
	 * @return
	 */
	@Path("/authenticate")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AuthenticateResponse authenticate(AuthenticateRequest req);

	/**
	 * A convenience method to return all 'generic' permissions for the owner of
	 * the ticket. 'Generic' permissions imply operations which are not targeted
	 * towards a specific object ID. This is helpful, usually in routing, to be
	 * able to validate routes such as "show admin section" (e.x. isAdmin
	 * operation), "show system configuration" (e.x. canConfigureSystem
	 * operation) etc.
	 *
	 * @return The generic permissions of the user, or a QInvalidTicketException
	 *         exception in case the ticket that was used when issuing this
	 *         request (passed in via the HTTP Headers) was invalid.
	 */
	@Path("/generic-permissions")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Set<String> genericPermissions(@Context HttpHeaders headers);

	@Path("/check-permission")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean checkPermission(CheckPermissionRDTO rdto,
			@Context HttpHeaders headers);

	/**
	 * Checks the validity of a ticket. Be careful as this method expects the
	 * ticket to be present in its arguments (i.e. inside a ValidateTicketRDTO
	 * object) and does not use the HTTP Headers of the request. This is due to
	 * the reason that HTTP Headers are only used in the case where we need to
	 * secure the underlying call/request, whereas in the case of this method
	 * the actual call to validate the ticket does not require security checks
	 * (i.e. anybody can check the validity of a ticket without having to have a
	 * valid ticket for issuing such as request).
	 *
	 * @param req
	 * @return
	 */
	@Path("/validate-ticket")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ValidateTicketResponse isTicketValid(ValidateTicketRequest req);

	/**
	 * Gets the full user details (including user attributes) for the user
	 * authenticated with the signed ticket.
	 *
	 * @return
	 */
	@Path("/user-details")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public UserDTO getUserDetails(@Context HttpHeaders headers);
}
