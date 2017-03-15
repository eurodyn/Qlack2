package com.eurodyn.qlack2.util.jsr.validator.util.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;

/**
 * Handler for ConstraintViolations.
 * 
 * @author European Dynamics SA
 *
 */
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
	public static final Logger LOGGER = Logger.getLogger(ValidationExceptionMapper.class.getName());

	@Override
	/**
	 * Parse validation errors and inject them in the response.
	 */
	public Response toResponse(ConstraintViolationException exception) {
		LOGGER.log(Level.FINE, "Constraint violation error.");

		Set<ValidationError> errors = new HashSet<>();

		for (ConstraintViolation<?> cv : exception.getConstraintViolations()) {
			LOGGER.log(Level.FINE, cv.toString());
			ValidationError error = new ValidationError();
			error.setPath(cleanupPath(cv));
			error.setMessage(cleanupMessage(cv));
			if (cv.getInvalidValue() != null) {
				error.setInvalidValue(cv.getInvalidValue().toString());
			}
			error.setAttributes(cv.getConstraintDescriptor().getAttributes());
			errors.add(error);
		}

		return Response.status(Response.Status.BAD_REQUEST)
				.type(MediaType.APPLICATION_JSON)
				.entity(errors)
				.build();
	}

	/**
	 * Messages come wrapped in {}, so remove them to facilitate the front-end.
	 * Dots in messages are also converted to underscores to facilitate JS
	 * key-mapping.
	 * 
	 * @param cv
	 *            The ConstraintViolation from which the message is extracted.
	 * @return
	 */
	private String cleanupMessage(@SuppressWarnings("rawtypes") ConstraintViolation cv) {
		String retVal = cv.getMessage();
		if (retVal.startsWith("{")) {
			retVal = retVal.substring(1);
		}
		if (retVal.endsWith("}")) {
			retVal = retVal.substring(0, retVal.length() - 1);
		}
		retVal = retVal.replace('.', '_');

		return retVal;
	}

	/**
	 * The path on the DTO is prefixed by the DTO class name as well as the
	 * position of the argument, e.g. MyDTO.arg0.field1, MyDTO.arg0.field2, etc.
	 * This method removes the prefix and only returns the path from the field
	 * down.
	 * 
	 * @param cv
	 * @return
	 */
	private String cleanupPath(ConstraintViolation cv) {
		String retVal = cv.getPropertyPath().toString();
		retVal = retVal.substring(retVal.indexOf(".", retVal.indexOf(".") + 1) + 1);

		return retVal;
	}

}
