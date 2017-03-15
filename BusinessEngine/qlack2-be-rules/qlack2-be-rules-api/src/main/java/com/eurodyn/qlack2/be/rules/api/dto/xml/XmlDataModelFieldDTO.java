package com.eurodyn.qlack2.be.rules.api.dto.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {
		"name",
		"fieldPrimitiveType",
		"fieldModelType",
		"fieldModelTypeVersion"
})
public class XmlDataModelFieldDTO {

	private String name;

	// exclusive or with model type
	private Integer fieldPrimitiveType;

	// exclusive or with primitive type
	private String fieldModelType;
	private String fieldModelTypeVersion;

	// -- Accessors

	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public Integer getFieldPrimitiveType() {
		return fieldPrimitiveType;
	}

	public void setFieldPrimitiveType(Integer fieldPrimitiveType) {
		this.fieldPrimitiveType = fieldPrimitiveType;
	}

	@XmlElement
	public String getFieldModelType() {
		return fieldModelType;
	}

	public void setFieldModelType(String fieldModelType) {
		this.fieldModelType = fieldModelType;
	}

	@XmlElement
	public String getFieldModelTypeVersion() {
		return fieldModelTypeVersion;
	}

	public void setFieldModelTypeVersion(String fieldModelTypeVersion) {
		this.fieldModelTypeVersion = fieldModelTypeVersion;
	}

}
