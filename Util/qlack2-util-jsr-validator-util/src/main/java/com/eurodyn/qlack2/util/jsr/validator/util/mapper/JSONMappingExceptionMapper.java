package com.eurodyn.qlack2.util.jsr.validator.util.mapper;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.fasterxml.jackson.databind.JsonMappingException;

public class JSONMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
	public static final Logger LOGGER = Logger.getLogger(ValidationExceptionMapper.class.getName());
	
	@Override
	public Response toResponse(JsonMappingException exception) {
		LOGGER.log(Level.FINE, "JSON mapping error.");
		
		exception.printStackTrace();
		
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

}
