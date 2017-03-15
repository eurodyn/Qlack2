package com.eurodyn.qlack2.be.rules.api.request.rule.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class CreateRuleVersionRequest extends QSignedRequest {

	private String ruleId;

	private String name;
	private String description;
	private String basedOnId;

	// -- Accessors

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBasedOnId() {
		return basedOnId;
	}

	public void setBasedOnId(String basedOnId) {
		this.basedOnId = basedOnId;
	}

}
