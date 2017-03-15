package com.eurodyn.qlack2.util.jsr.validator.util;

import java.util.Map;

public class ValidationError {
	private String path;
	private String message;
	private String invalidValue;
	private Map<String, Object> attributes;

	public ValidationError() {
		super();
	}
	
	public ValidationError(String path, String message, String invalidValue) {
		super();
		this.path = path;
		this.message = message;
		this.invalidValue = invalidValue;
	}

	public ValidationError(String path, String message) {
		super();
		this.path = path;
		this.message = message;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getInvalidValue() {
		return invalidValue;
	}

	public void setInvalidValue(String invalidValue) {
		this.invalidValue = invalidValue;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

}
