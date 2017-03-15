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

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class ValidationErrorType {
	private String type;
	private Map<ValidationAttribute, Object> attributes;
	public String getType() {
		return type;
	}
	public Map<ValidationAttribute, Object> getAttributes() {
		return attributes;
	}
	@SuppressWarnings("unused")
	private ValidationErrorType() {
	}
	public ValidationErrorType(String type) {
		this.type = type;
		attributes = new HashMap<>();
	}
	public void putAttribute(ValidationAttribute attr, Object value) {
		attributes.put(attr, value);
	}
	public void putAttribute(ValidationAttribute attr, Map m) {
		attributes.put(attr, m);
	}
}
