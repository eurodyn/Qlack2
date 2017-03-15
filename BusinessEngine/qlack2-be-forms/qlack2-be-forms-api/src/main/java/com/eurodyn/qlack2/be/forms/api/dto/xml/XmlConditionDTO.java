package com.eurodyn.qlack2.be.forms.api.dto.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "name", "conditionType", "workingSet",
"workingSetVersion", "rule", "ruleVersion", "parentCondition" })
public class XmlConditionDTO {
	private String name;
	private Integer conditionType;
	private String workingSet;
	private String workingSetVersion;
	private String rule;
	private String ruleVersion;
	private String parentCondition;

	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public Integer getConditionType() {
		return conditionType;
	}

	public void setConditionType(Integer conditionType) {
		this.conditionType = conditionType;
	}

	@XmlElement
	public String getWorkingSet() {
		return workingSet;
	}

	public void setWorkingSet(String workingSet) {
		this.workingSet = workingSet;
	}

	@XmlElement
	public String getWorkingSetVersion() {
		return workingSetVersion;
	}

	public void setWorkingSetVersion(String workingSetVersion) {
		this.workingSetVersion = workingSetVersion;
	}

	@XmlElement
	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	@XmlElement
	public String getRuleVersion() {
		return ruleVersion;
	}

	public void setRuleVersion(String ruleVersion) {
		this.ruleVersion = ruleVersion;
	}

	@XmlElement
	public String getParentCondition() {
		return parentCondition;
	}

	public void setParentCondition(String parentCondition) {
		this.parentCondition = parentCondition;
	}

}
