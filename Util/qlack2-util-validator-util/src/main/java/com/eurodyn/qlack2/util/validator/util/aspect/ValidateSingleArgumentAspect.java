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
package com.eurodyn.qlack2.util.validator.util.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.eurodyn.qlack2.util.validator.util.ValidationUtil;
import com.eurodyn.qlack2.util.validator.util.annotation.ValidateSingleArgument;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrors;
import com.eurodyn.qlack2.util.validator.util.exception.QValidationException;

@Aspect
@Deprecated
public class ValidateSingleArgumentAspect {
	private static final ValidationUtil validationUtil = new ValidationUtil();

	@Pointcut("execution(@com.eurodyn.qlack2.util.validator.util.annotation.ValidateSingleArgument * * (..))")
	void validateObjectPointCut() {
	}

	@Before("validateObjectPointCut()")
	public void validateObjectAroundAction(JoinPoint jp)
	throws Throwable {
		ValidateSingleArgument annotation =
				((MethodSignature) jp.getStaticPart().getSignature())
				.getMethod().getAnnotation(ValidateSingleArgument.class);
		int requestIndex = annotation.requestIndex();
		// Get a reference to the object to be validated.
		Object obj = (jp.getArgs()[requestIndex]);

		// Validate.
		ValidationErrors errors = validationUtil.validate(obj);

		if (errors.getValidationErrors().size() > 0) {
			throw new QValidationException(errors);
		}
	}
}
