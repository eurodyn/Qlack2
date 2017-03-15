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
package com.eurodyn.qlack2.util.validator.util.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.eurodyn.qlack2.util.validator.util.exception.QValidationException;

/**
 * A custom handler for data-validation exceptions in REST calls.
 * 
 * @author European Dynamics SA
 *
 */
@Provider
@Deprecated
public class ValidationExceptionMapper implements ExceptionMapper<QValidationException> {
	private static final Logger LOGGER = Logger.getLogger("qlack");

	@Override
	public Response toResponse(QValidationException exception) {
		LOGGER.log(Level.FINE, "QBECXFExceptionMapper: Validation exception",
				exception);
		return Response.status(Response.Status.BAD_REQUEST)
				.entity(((QValidationException) exception).getErrors())
				.build();

	}

}
