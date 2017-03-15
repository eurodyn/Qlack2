package com.eurodyn.qlack2.util.jsr.validator.util.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class InvalidFormatExceptionMapper implements ExceptionMapper<InvalidFormatException> {
	public static final Logger LOGGER = Logger.getLogger(InvalidFormatExceptionMapper.class.getName());
	public static final String DEFAULT_ERROR_MESSAGE = "errors.field.type.mapping";
	
	@Override
	public Response toResponse(InvalidFormatException exception) {
		LOGGER.log(Level.FINE, "InvalidFormatException mapping error.");

		// Find the full path to the field that failed mapping.
		String fieldPath = exception.getPath().stream()
				.map(Reference::getFieldName).collect(Collectors.joining("."));
		
		// Create a validation error.
		Set<ValidationError> errors = new HashSet<>();
		errors.add(new ValidationError(fieldPath, DEFAULT_ERROR_MESSAGE, 
				exception.getValue().toString()));
		
		return Response.status(Response.Status.BAD_REQUEST)
				.type(MediaType.APPLICATION_JSON)
				.entity(errors)
				.build();
	}

}
