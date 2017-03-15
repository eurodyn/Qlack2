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

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.eurodyn.qlack2.util.validator.util.errors.ValidationAttribute;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrorType;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrors;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationFieldErrors;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * A custom handler for exceptions when mapping REST JSON calls to objects.
 * 
 * @author European Dynamics SA
 *
 */
@Provider
@Deprecated
public class MappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
	private static final Logger LOGGER = Logger.getLogger("qlack");

	@Override
	public Response toResponse(JsonMappingException exception) {

		LOGGER.log(Level.FINE, "QBECXFExceptionMapper: JSON Mapping Exception", exception);
		JsonMappingException jsonException = (JsonMappingException) exception;
		String fieldName = "";
		Iterator<JsonMappingException.Reference> referenceIt = jsonException.getPath().iterator();
		while (referenceIt.hasNext()) {
			fieldName = fieldName.concat(referenceIt.next().getFieldName());
			if (referenceIt.hasNext()) {
				fieldName = fieldName.concat(".");
			}
		}
		ValidationErrors errors = new ValidationErrors();
		ValidationFieldErrors vfe = new ValidationFieldErrors(fieldName);
		ValidationErrorType vet = new ValidationErrorType("org.codehaus.jackson.map.JsonMappingException");
		vet.putAttribute(ValidationAttribute.Message, "org.codehaus.jackson.map.JsonMappingException");
		vfe.addError(vet);
		errors.addValidationError(vfe);
		return Response.status(Response.Status.BAD_REQUEST)
				.entity(errors)
				.build();
	}
}
