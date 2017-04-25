package com.eurodyn.qlack2.util.jsr.validator.util.mapper;

import com.fasterxml.jackson.databind.JsonMappingException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A generic error handler for anything else that might go wrong during
 * processing.
 *
 * @author European Dynamics SA
 *
 */
public class JSONMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
	public static final Logger LOGGER = Logger.getLogger(ValidationExceptionMapper.class.getName());
	
	@Override
	public Response toResponse(JsonMappingException exception) {
		LOGGER.log(Level.WARNING, "JSON mapping error.", exception);

		return Response.status(Response.Status.BAD_REQUEST).build();
	}

}
