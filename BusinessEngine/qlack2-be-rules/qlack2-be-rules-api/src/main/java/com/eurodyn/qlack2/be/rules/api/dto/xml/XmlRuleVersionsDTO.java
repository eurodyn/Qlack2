package com.eurodyn.qlack2.be.rules.api.dto.xml;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class XmlRuleVersionsDTO {
	private List<XmlRuleVersionDTO> ruleVersions;

	@XmlElement(name = "ruleVersion")
	public List<XmlRuleVersionDTO> getRuleVersions() {
		return ruleVersions;
	}

	public void setRuleVersions(List<XmlRuleVersionDTO> ruleVersions) {
		this.ruleVersions = ruleVersions;
	}

}
