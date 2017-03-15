package com.eurodyn.qlack2.util.validator.util.constraints;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * An extension of Hibernate's ScriptAssert annotation which includes
 * field names on which validation errors are to be put.
 * @author European Dynamics SA
 *
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = QScriptAssertValidator.class)
@Documented
@Deprecated
public @interface QScriptAssert {

	String message() default "{com.eurodyn.qlack2.util.validator.constraints.ScriptAssert}";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

	/**
	 * @return The name of the script language used by this constraint as
	 *         expected by the JSR 223 {@link javax.script.ScriptEngineManager}.
	 *         A {@link javax.validation.ConstraintDeclarationException} will be
	 *         thrown upon script evaluation, if no engine for the given
	 *         language could be found. Javascript is used by default since
	 *         the Java Scripting API is included by default in the classpath for Java 6
	 *         and later.
	 */
	String lang() default "javascript";

	/**
	 * @return The script to be executed. The script must return
	 *         <code>Boolean.TRUE</code>, if the annotated element could
	 *         successfully be validated, otherwise <code>Boolean.FALSE</code>.
	 *         Returning null or any type other than Boolean will cause a
	 *         {@link javax.validation.ConstraintDeclarationException} upon
	 *         validation. Any exception occurring during script evaluation will
	 *         be wrapped into a ConstraintDeclarationException, too. Within the
	 *         script, the validated object can be accessed from the
	 *         {@link javax.script.ScriptContext script context} using the name
	 *         specified in the <code>alias</code> attribute.
	 */
	String script();

	/**
	 * @return The name, under which the annotated element shall be registered
	 *         within the script context. Defaults to "_this".
	 */
	String alias() default "_this";
	String[] fieldNames();

	/**
	 * Defines several {@code @QScriptAssert} annotations on the same element.
	 */
	@Target({ TYPE })
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		QScriptAssert[] value();
	}
}
