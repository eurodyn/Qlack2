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
package com.eurodyn.qlack2.util.rest.mapper;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.eurodyn.qlack2.common.util.exception.QSecurityAuthenticationException;
import com.eurodyn.qlack2.common.util.exception.QSecurityAuthorisationException;
import com.eurodyn.qlack2.common.util.exception.QSecurityException;

/**
 * A generic security exception mapper to return appropriate status codes.
 * 
 * @author European Dynamics SA
 *
 */
@Provider
public class SecurityExceptionMapper implements ExceptionMapper<QSecurityException> {
	private static final Logger LOGGER = Logger.getLogger(SecurityExceptionMapper.class.getName());

	@Override
	public Response toResponse(QSecurityException exception) {
		LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
		
		if (exception instanceof QSecurityAuthorisationException) {
			return Response.status(Response.Status.FORBIDDEN).build();
		} else if (exception instanceof QSecurityAuthenticationException) {
			return Response.status(Response.Status.UNAUTHORIZED).build();	
		} else {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
	}
}
