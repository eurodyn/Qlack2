package com.eurodyn.qlack2.be.workflow.api.dto;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "condition", propOrder = {"name",
    "conditionType",
    "workingSet",
    "workingSetVersion",
    "rule",
    "ruleVersion",
    "parentCondition"
})
public class XMLConditionDTO {

	private String name;
	private String conditionType;
	private String workingSet;
	private String workingSetVersion;
	private String rule;
	private String ruleVersion;
	private String parentCondition;

	public XMLConditionDTO() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
		
	public String getConditionType() {
		return conditionType;
	}

	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}

	public String getWorkingSet() {
		return workingSet;
	}

	public void setWorkingSet(String workingSet) {
		this.workingSet = workingSet;
	}
	
	public String getWorkingSetVersion() {
		return workingSetVersion;
	}

	public void setWorkingSetVersion(String workingSetVersion) {
		this.workingSetVersion = workingSetVersion;
	}
	
	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}
	
	public String getRuleVersion() {
		return ruleVersion;
	}

	public void setRuleVersion(String ruleVersion) {
		this.ruleVersion = ruleVersion;
	}

	public String getParentCondition() {
		return parentCondition;
	}

	public void setParentCondition(String parent) {
		this.parentCondition = parent;
	}
}
