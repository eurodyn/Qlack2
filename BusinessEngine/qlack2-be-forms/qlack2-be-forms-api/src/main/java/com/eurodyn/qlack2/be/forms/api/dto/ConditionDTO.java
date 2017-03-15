package com.eurodyn.qlack2.be.forms.api.dto;

import java.util.Comparator;

public class ConditionDTO {
	private String id;
	private String name;
	private Integer conditionType;
	private String workingSetId;
	private String ruleId;
	private ConditionDTO parentCondition;

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

	public Integer getConditionType() {
		return conditionType;
	}

	public void setConditionType(Integer conditionType) {
		this.conditionType = conditionType;
	}

	public String getWorkingSetId() {
		return workingSetId;
	}

	public void setWorkingSetId(String workingSetId) {
		this.workingSetId = workingSetId;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public ConditionDTO getParentCondition() {
		return parentCondition;
	}

	public void setParentCondition(ConditionDTO parentCondition) {
		this.parentCondition = parentCondition;
	}

	public static class ConditionDTOComparator implements
			Comparator<ConditionDTO> {

		@Override
		public int compare(ConditionDTO o1, ConditionDTO o2) {
			return o1.getId().compareTo(o2.getId());
		}

	}
}
