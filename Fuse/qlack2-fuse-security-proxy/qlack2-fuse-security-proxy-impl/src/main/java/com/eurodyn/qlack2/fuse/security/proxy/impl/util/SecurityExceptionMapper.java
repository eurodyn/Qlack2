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
package com.eurodyn.qlack2.fuse.security.proxy.impl.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.eurodyn.qlack2.fuse.idm.api.exception.QIDMException;

/**
 * A custom handler for exceptions in REST calls. The main idea here is to
 * catch exceptions Fuse IDM produces and provide a more meaningful status code
 * as well as validation errors when they exist.
 * @author European Dynamics SA
 *
 */
@Provider
public class SecurityExceptionMapper implements ExceptionMapper<QIDMException> {
	@Override
	public Response toResponse(QIDMException exception) {
		return Response.status(Response.Status.UNAUTHORIZED).build();
	}

}
