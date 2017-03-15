package com.eurodyn.qlack2.be.rules.api.dto.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "dataModelVersion", propOrder = {
		"dataModelName",
		"name",
		"description",
		"modelPackage",
		"parentDataModel",
		"parentDataModelVersion",
		"fields"
})
public class XmlDataModelVersionDTO {

	private String dataModelName;
	private String name;
	private String description;
	private String modelPackage;

	private String parentDataModel;
	private String parentDataModelVersion;

	private XmlDataModelFieldsDTO fields;

	// -- Accessors

	@XmlElement
	public String getDataModelName() {
		return dataModelName;
	}

	public void setDataModelName(String dataModelName) {
		this.dataModelName = dataModelName;
	}

	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement
	public String getModelPackage() {
		return modelPackage;
	}

	public void setModelPackage(String modelPackage) {
		this.modelPackage = modelPackage;
	}

	@XmlElement
	public String getParentDataModel() {
		return parentDataModel;
	}

	public void setParentDataModel(String parentDataModel) {
		this.parentDataModel = parentDataModel;
	}

	@XmlElement
	public String getParentDataModelVersion() {
		return parentDataModelVersion;
	}

	public void setParentDataModelVersion(String parentDataModelVersion) {
		this.parentDataModelVersion = parentDataModelVersion;
	}

	@XmlElement
	public XmlDataModelFieldsDTO getFields() {
		return fields;
	}

	public void setFields(XmlDataModelFieldsDTO fields) {
		this.fields = fields;
	}

}
