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
package com.eurodyn.qlack2.util.validator.util;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.eurodyn.qlack2.util.validator.util.errors.ValidationAttribute;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrorType;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrors;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationFieldErrors;
import com.eurodyn.qlack2.util.validator.util.osgi.util.HibernateValidationProviderResolver;
import com.eurodyn.qlack2.util.validator.util.osgi.util.MockMessageInterpolator;

@Deprecated
public class ValidationUtil {
	private static final Logger LOGGER = Logger.getLogger(ValidationUtil.class
			.getName());
	private Configuration<?> configuration = Validation.byDefaultProvider()
			.providerResolver(new HibernateValidationProviderResolver())
			.configure();
	ValidatorFactory factory = configuration
			.messageInterpolator(new MockMessageInterpolator())
			.buildValidatorFactory();
	Validator validator = factory.getValidator();

	public ValidationErrors validate(Object o) {
		Set<ConstraintViolation<Object>> violations = validator.validate(o);
		LOGGER.log(Level.FINE, "Validation errors found: {0}.",
				violations.size());

		ValidationErrors errors = new ValidationErrors();
		for (@SuppressWarnings("rawtypes")
		ConstraintViolation v : violations) {
			ValidationFieldErrors fieldError = new ValidationFieldErrors(v
					.getPropertyPath().toString());
			ValidationErrorType vet = new ValidationErrorType(v.getConstraintDescriptor().getAnnotation().annotationType().getName());
//			vet.putAttribute(ValidationAttribute.CheckName, v
//					.getConstraintDescriptor().getAnnotation().toString());
			vet.putAttribute(ValidationAttribute.InvalidValue, v.getInvalidValue());
			
			String message = v.getMessage();
			if(message != null && message.startsWith("{") &&
					message.endsWith(".message}")) {
				// Drop initial curly bracket and .message}
				message = message.substring(1, message.length());
				message = message.substring(0, message.lastIndexOf(".message}"));
			}
			
			vet.putAttribute(ValidationAttribute.Message, message);
			vet.putAttribute(ValidationAttribute.ValidatedObject, v.getLeafBean().toString());
			vet.putAttribute(ValidationAttribute.Raw, v.getConstraintDescriptor().getAttributes());

			if (v.getRootBean() != null && v.getRootBean() != v
					.getLeafBean()) {
				vet.putAttribute(ValidationAttribute.ParentObject, v
						.getRootBean().toString());
			}

			fieldError.addError(vet);
			errors.addValidationError(fieldError);
		}

		return errors;
	}
}
