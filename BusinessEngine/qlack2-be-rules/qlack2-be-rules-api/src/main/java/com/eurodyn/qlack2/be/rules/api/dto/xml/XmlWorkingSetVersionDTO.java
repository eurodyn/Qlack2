package com.eurodyn.qlack2.be.rules.api.dto.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "workingSetVersion", propOrder = {
		"workingSetName",
		"name",
		"description",
		"ruleVersions",
		"dataModelVersions",
		"libraryVersions"
})
public class XmlWorkingSetVersionDTO {

	private String workingSetName;
	private String name;
	private String description;

	private XmlRuleVersionsDTO ruleVersions;
	private XmlDataModelVersionsDTO dataModelVersions;
	private XmlLibraryVersionsDTO libraryVersions;

	// -- Accessors

	@XmlElement
	public String getWorkingSetName() {
		return workingSetName;
	}

	public void setWorkingSetName(String workingSetName) {
		this.workingSetName = workingSetName;
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
	public XmlRuleVersionsDTO getRuleVersions() {
		return ruleVersions;
	}

	public void setRuleVersions(XmlRuleVersionsDTO ruleVersions) {
		this.ruleVersions = ruleVersions;
	}

	@XmlElement
	public XmlDataModelVersionsDTO getDataModelVersions() {
		return dataModelVersions;
	}

	public void setDataModelVersions(XmlDataModelVersionsDTO dataModelVersions) {
		this.dataModelVersions = dataModelVersions;
	}

	@XmlElement
	public XmlLibraryVersionsDTO getLibraryVersions() {
		return libraryVersions;
	}

	public void setLibraryVersions(XmlLibraryVersionsDTO libraryVersions) {
		this.libraryVersions = libraryVersions;
	}

}
