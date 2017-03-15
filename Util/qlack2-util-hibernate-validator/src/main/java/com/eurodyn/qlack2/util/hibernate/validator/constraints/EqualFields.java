package com.eurodyn.qlack2.util.hibernate.validator.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EqualFieldsValidator.class)
public @interface EqualFields {
	String message() default "{ov.constraints.equalFields}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	/**
	 * @return The first field
	 */
	String field1();

	/**
	 * @return The second field
	 */
	String field2();
	
	/**
	 * Allows you to specify on which field this validation error will be
	 * bound to.
	 * @return
	 */
	String[] showOnFields() default "";
}
