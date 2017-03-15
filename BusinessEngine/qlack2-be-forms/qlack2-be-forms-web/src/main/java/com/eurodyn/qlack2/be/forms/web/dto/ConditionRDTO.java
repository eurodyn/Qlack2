package com.eurodyn.qlack2.be.forms.web.dto;

import java.util.Comparator;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConditionRDTO {
	private String id;

	@NotNull
	@NotEmpty
	@Length(min = 1, max = 255)
	private String name;

	@NotNull
	@Min(value = 0)
	@Max(value = 3)
	private Integer conditionType;

	@NotNull
	@NotEmpty
	private String workingSetId;

	@NotNull
	@NotEmpty
	private String ruleId;

	private ConditionRDTO parentCondition;

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

	public ConditionRDTO getParentCondition() {
		return parentCondition;
	}

	public void setParentCondition(ConditionRDTO parentCondition) {
		this.parentCondition = parentCondition;
	}

	public static class ConditionDTOComparator implements
			Comparator<ConditionRDTO> {

		@Override
		public int compare(ConditionRDTO o1, ConditionRDTO o2) {
			return o1.getId().compareTo(o2.getId());
		}

	}
}
