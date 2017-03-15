package com.eurodyn.qlack2.util.hibernate.validator.constraints;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

public class EqualFieldsValidator implements
		ConstraintValidator<EqualFields, Object> {
	private static final Logger LOGGER = Logger
			.getLogger(EqualFieldsValidator.class.getName());
	private String field1;
	private String field2;

	@Override
	public void initialize(EqualFields constraintAnnotation) {
		field1 = constraintAnnotation.field1();
		field2 = constraintAnnotation.field2();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {		
		try {
			final Object firstObj = BeanUtils.getProperty(value, field1);
			final Object secondObj = BeanUtils.getProperty(value, field2);
			
			if (firstObj == null || secondObj == null) {
				return false;
			} else {
				return firstObj.equals(secondObj);	
			}			
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "Could not apply EqualFields validator", e);			
		}

		return false;
	}

}
