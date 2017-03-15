package com.eurodyn.qlack2.util.validator.util;

import java.util.Map;

@Deprecated
public class ValidationError {
	private String errorKey;
	private String errorMsg;
	private Map<String, Object> properties;
	private Object  errorValue;
	private String objectName;
	private String attributeName;
	private String rootClass;
	private String path;
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Map<String, Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	public String getErrorKey() {
		return errorKey;
	}
	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}
	public Object getErrorValue() {
		return errorValue;
	}
	public void setErrorValue(Object errorValue) {
		this.errorValue = errorValue;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public String getRootClass() {
		return rootClass;
	}
	public void setRootClass(String rootClass) {
		this.rootClass = rootClass;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
