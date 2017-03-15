package com.eurodyn.qlack2.be.rules.impl.util;

import java.util.List;

public class StringDataModelVersion {

	private String id;

	private String name;

	private String className;

	private String superClassName;

	private List<StringDataModelField> fields;

	// -- Accessors

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSuperClassName() {
		return superClassName;
	}

	public void setSuperClassName(String superClassName) {
		this.superClassName = superClassName;
	}

	public List<StringDataModelField> getFields() {
		return fields;
	}

	public void setFields(List<StringDataModelField> fields) {
		this.fields = fields;
	}
}
