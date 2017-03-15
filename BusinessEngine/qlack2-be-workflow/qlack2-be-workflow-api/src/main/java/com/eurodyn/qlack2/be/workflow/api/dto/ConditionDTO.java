package com.eurodyn.qlack2.be.workflow.api.dto;

import java.util.Comparator;

import com.eurodyn.qlack2.be.workflow.api.dto.ConditionDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowVersionDTO;

public class ConditionDTO {

	private String id;
	private String name;
	private int conditionType;
	private String workingSetId;
	private String ruleId;
	private ConditionDTO parentCondition;

	public ConditionDTO() {
	}

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
		
	public int getConditionType() {
		return conditionType;
	}

	public void setConditionType(int conditionType) {
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

	public void setParentCondition(ConditionDTO parent) {
		this.parentCondition = parent;
	}
	
	public static class ConditionDTOComparator implements
	Comparator<ConditionDTO> {

		@Override
		public int compare(ConditionDTO o1, ConditionDTO o2) {
			return o1.getId().compareTo(o2.getId());
		}
	}
}
