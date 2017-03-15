package com.eurodyn.qlack2.be.forms.api.dto.xml;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class XmlConditionsDTO {
	private List<XmlConditionDTO> conditions;

	@XmlElement(name = "condition")
	public List<XmlConditionDTO> getConditions() {
		return conditions;
	}

	public void setConditions(List<XmlConditionDTO> conditions) {
		this.conditions = conditions;
	}

}
