package com.eurodyn.qlack2.be.workflow.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
public class XMLConditionsDTO {
	@XmlElement(name = "condition", type = XMLConditionDTO.class)
	private List<XMLConditionDTO> conditions;

	public XMLConditionsDTO() {
	}
	
	public List<XMLConditionDTO> getConditions() {
		if (this.conditions == null) {
			this.conditions = new ArrayList<XMLConditionDTO>();
        }
        return this.conditions;
	}
	
	public void setConditions(List<XMLConditionDTO> conditions) {
		this.conditions = conditions;
	}
}
