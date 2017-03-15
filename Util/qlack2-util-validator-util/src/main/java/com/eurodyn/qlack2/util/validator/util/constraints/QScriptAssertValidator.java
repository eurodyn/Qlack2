package com.eurodyn.qlack2.util.validator.util.constraints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Deprecated
public class QScriptAssertValidator implements ConstraintValidator<QScriptAssert, Object> {
	private String script;
	private String languageName;
	private String alias;
	private List<String> fieldNames = new ArrayList<>();
	private String message;

	public void initialize(QScriptAssert constraintAnnotation) {
		validateParameters( constraintAnnotation );

		this.script = constraintAnnotation.script();
		this.languageName = constraintAnnotation.lang();
		this.alias = constraintAnnotation.alias();
		this.fieldNames.addAll(Arrays.asList(constraintAnnotation.fieldNames()));
		this.message = constraintAnnotation.message();
	}

	public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {

		Object evaluationResult;
//		ScriptEvaluator scriptEvaluator;
		ScriptEngine scriptEngine;
		
		scriptEngine = new ScriptEngineManager().getEngineByName(languageName);
		if (scriptEngine == null ) {
			throw new ConstraintDeclarationException("Unable to find script engine for language " + languageName);
		}
		String engineThreadingType = (String) scriptEngine.getFactory().getParameter( "THREADING" );
		try {
		if ("THREAD-ISOLATED".equals(engineThreadingType) || "STATELESS".equals(engineThreadingType)) {
			evaluationResult = evaluateScript(value, scriptEngine);
		} else {
			synchronized (scriptEngine) {
				evaluationResult = evaluateScript(value, scriptEngine);
			}
		}
		} catch (ScriptException e) {
			throw new ConstraintDeclarationException(
					"Error during execution of script \"" + script + "\" occurred.", e
			);
		}

		if (evaluationResult == null) {
			throw new ConstraintDeclarationException("Script \"" + script + "\" returned null, but must return either true or false.");
		}
		if (!(evaluationResult instanceof Boolean)) {
			throw new ConstraintDeclarationException(
					"Script \"" + script + "\" returned " + evaluationResult + " (of type " + evaluationResult.getClass()
							.getCanonicalName() + "), but must return either true or false."
			);
		}
		
		// If a specific field has been specified add the error message to that field
		// instead of setting a generic error message.
		if ((Boolean.FALSE.equals(evaluationResult)) && fieldNames.size() > 0) {
			constraintValidatorContext.disableDefaultConstraintViolation();
			for (String fieldName : fieldNames) {
				constraintValidatorContext.buildConstraintViolationWithTemplate(message)
					.addNode(fieldName).addConstraintViolation();
			}
		}

		return Boolean.TRUE.equals(evaluationResult);
	}

	private Object evaluateScript(Object value, ScriptEngine scriptEngine)
			throws ScriptException {
		Object evaluationResult;
		scriptEngine.put(alias, value);
		evaluationResult = scriptEngine.eval(script);
		return evaluationResult;
	}

	private void validateParameters(QScriptAssert constraintAnnotation) {
		if (constraintAnnotation.script().length() == 0) {
			throw new IllegalArgumentException( "The parameter \"script\" must not be empty." );
		}
		if (constraintAnnotation.lang().length() == 0) {
			throw new IllegalArgumentException( "The parameter \"lang\" must not be empty." );
		}
		if (constraintAnnotation.alias().length() == 0) {
			throw new IllegalArgumentException( "The parameter \"alias\" must not be empty." );
		}
		if (constraintAnnotation.message().length() == 0) {
			throw new IllegalArgumentException( "The parameter \"message\" must not be empty." );
		}
	}
}
