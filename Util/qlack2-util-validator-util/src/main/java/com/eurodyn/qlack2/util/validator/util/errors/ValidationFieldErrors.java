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
package com.eurodyn.qlack2.util.validator.util.errors;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class ValidationFieldErrors {
	private String fieldName;
	private List<ValidationErrorType> errors;

	public String getFieldName() {
		return fieldName;
	}
	public List<ValidationErrorType> getErrors() {
		return errors;
	}
	private ValidationFieldErrors() {
	}
	public ValidationFieldErrors(String fieldName) {
		this.fieldName = fieldName;
		errors = new ArrayList<>();
	}
	public void addError(ValidationErrorType error) {
		errors.add(error);
	}
}
