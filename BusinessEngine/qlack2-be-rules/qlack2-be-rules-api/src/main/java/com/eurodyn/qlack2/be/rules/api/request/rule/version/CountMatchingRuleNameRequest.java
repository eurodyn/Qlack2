package com.eurodyn.qlack2.be.rules.api.request.rule.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class CountMatchingRuleNameRequest extends QSignedRequest {
	private String ruleId;
	private String droolsRuleName;

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getDroolsRuleName() {
		return droolsRuleName;
	}

	public void setDroolsRuleName(String droolsRuleName) {
		this.droolsRuleName = droolsRuleName;
	}

}
